package com.masterwok.stream_video_android.services;

import com.frostwire.jlibtorrent.Pair;
import com.masterwok.stream_video_android.contracts.IStreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


/**
 * A service that provides media streaming over HTTP using the provided stream factory.
 */
public class HttpStreamService extends NanoHTTPD {

    private int chunkSize;
    private final IStreamFactory IStreamFactory;


    /**
     * Create a new instance of the HttpStreamService.
     * @param port - The listening port.
     * @param chunkSize - Max offset when no end is provided in the request.
     * @param IStreamFactory - Stream factory of media.
     */
    public HttpStreamService(
            int port,
            int chunkSize,
            IStreamFactory IStreamFactory
    ) {
        super(port);
        this.chunkSize = chunkSize;
        this.IStreamFactory = IStreamFactory;
    }

    @Override
    public Response serve(
            String uri,
            Method method,
            Map<String, String> header,
            Map<String, String> params,
            Map<String, String> files
    ) {
        if (uri.equals("/favicon.ico")) {
            return new Response(Response.Status.NOT_FOUND, "", "");
        }

        InputStream inputStream = IStreamFactory.getStream();
        long fileSize = IStreamFactory.getLength();

        if (header.containsKey("range")) {
            return getRangeResponse(
                    inputStream,
                    getRangeFromHeader(header, fileSize),
                    fileSize
            );
        }

        return getInitialResponse(
                inputStream,
                fileSize
        );
    }

    /**
     * Get range values from request headers.
     * @param header - Request headers.
     * @param fileSize - Size of the media.
     * @return - A pair containing the range start and end.
     */
    private Pair<Long, Long> getRangeFromHeader(
            Map<String, String> header,
            long fileSize
    ) {
        String range = header.get("range");

        long start = 0;
        long end = -1;

        range = range.substring("bytes=".length());

        int dashIndex = range.indexOf('-');

        try {
            start = Long.parseLong(range.substring(0, dashIndex));
            end = Long.parseLong(range.substring(dashIndex + 1));
        } catch (NumberFormatException ignored) {
        }

        if (end < 0) {
            end = start + chunkSize;
        }

        if (end > fileSize - 1) {
            end = fileSize - 1;
        }

        return new Pair<>(start, end);
    }


    /**
     * Create a range response for media.
     * @param inputStream - InputStream of media.
     * @param range - Range of bytes requested.
     * @param fileSize - The media file size.
     * @return - An Http response for the range request.
     */
    private Response getRangeResponse(
            InputStream inputStream,
            Pair<Long, Long> range,
            long fileSize
    ) {
        long start = range.first;
        long end = range.second;
        long len = end - start;

        if (len < 0) {
            len = 0;
        }

        try {
            inputStream.skip(start);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Response response = new Response(
                Response.Status.PARTIAL_CONTENT,
                IStreamFactory.getContentType(),
                inputStream
        );

        response.addHeader("Accept-Range", "bytes");
        response.addHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, fileSize));

        if (len > 0) {
            response.addHeader("Content-Length", String.valueOf(len));
        }

        return response;
    }

    /**
     * Create a request that states ranges are supported.
     * @param inputStream - Input stream of media.
     * @param fileSize - The media file size.
     * @return - A response for the initial range request.
     */
    private Response getInitialResponse(
            InputStream inputStream,
            long fileSize
    ) {
        Response response = new Response(
                Response.Status.OK,
                IStreamFactory.getContentType(),
                inputStream
        );

        response.addHeader("Accept-Range", "bytes");
        response.addHeader("Content-Length", String.valueOf(fileSize));

        return response;
    }
}
