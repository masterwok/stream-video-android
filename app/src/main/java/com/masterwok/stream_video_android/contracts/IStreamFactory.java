package com.masterwok.stream_video_android.contracts;

import java.io.InputStream;

/**
 * A factory that provides an input stream. It also provides the
 * length of the stream as well as the content type of the file.
 */
public interface IStreamFactory {

    /**
     * Get media input stream.
     */
    InputStream getStream();

    /**
     * Media length in bytes.
     */
    long getLength();

    /**
     * The content-type of the media.
     */
    String getContentType();
}
