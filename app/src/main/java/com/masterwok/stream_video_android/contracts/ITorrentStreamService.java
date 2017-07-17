package com.masterwok.stream_video_android.contracts;


import java.io.File;

/**
 * A service that creates a torrent stream for a given url. Once the torrent stream is ready,
 * an HTTP server is started so that the media can be streamed over HTTP.
 */
public interface ITorrentStreamService {

    /**
     * Start torrent stream. Once the torrent stream is ready an HTTP server will
     * be started.
     * @param url - The magnet or torrent url.
     * @param saveLocation - The torrent to store the downloaded torrent.
     */
    void startStream(
            String url,
            File saveLocation
    );

    /**
     * Stop the torrent download stream and the HTTP server.
     */
    void stopStream();
}
