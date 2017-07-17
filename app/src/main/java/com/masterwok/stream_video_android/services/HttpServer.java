package com.masterwok.stream_video_android.services;

import com.frostwire.jlibtorrent.Pair;
import com.masterwok.stream_video_android.contracts.StreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class HttpServer extends NanoHTTPD {

    private int chunkSize;
    private final StreamFactory streamFactory;

    public HttpServer(
            int port,
            int chunkSize,
            StreamFactory streamFactory
    ) {
        super(port);
        this.chunkSize = chunkSize;
        this.streamFactory = streamFactory;
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

        InputStream inputStream = streamFactory.getStream();
        long fileSize = streamFactory.getLength();

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
                streamFactory.getContentType(),
                inputStream
        );

        response.addHeader("Accept-Range", "bytes");
        response.addHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, fileSize));

        if (len > 0) {
            response.addHeader("Content-Length", String.valueOf(len));
        }

        return response;
    }

    private Response getInitialResponse(
            InputStream inputStream,
            long size
    ) {
        Response response = new Response(
                Response.Status.OK,
                streamFactory.getContentType(),
                inputStream
        );

        response.addHeader("Accept-Range", "bytes");
        response.addHeader("Content-Length", String.valueOf(size));

        return response;
    }
}
