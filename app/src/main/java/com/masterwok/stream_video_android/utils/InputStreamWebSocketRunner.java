package com.masterwok.stream_video_android.utils;

import com.masterwok.stream_video_android.contracts.StreamFactory;
import com.masterwok.stream_video_android.services.WebSocketStreamService;

import java.io.InputStream;

public class InputStreamWebSocketRunner implements Runnable {
    public static final int Port = 1234;

    private final StreamFactory streamFactory;

    public InputStreamWebSocketRunner(StreamFactory streamFactory) {
        this.streamFactory = streamFactory;
    }

    @Override
    public void run() {
        new WebSocketStreamService(Port, streamFactory).start();
    }
}