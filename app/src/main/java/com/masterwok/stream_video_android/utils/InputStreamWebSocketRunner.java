package com.masterwok.stream_video_android.utils;

import com.masterwok.stream_video_android.services.WebSocketStreamService;

import java.io.InputStream;

public class InputStreamWebSocketRunner implements Runnable {
    public static final int Port = 1234;

    private final InputStream inputStream;


    public InputStreamWebSocketRunner(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        new WebSocketStreamService(Port, inputStream).start();
    }
}