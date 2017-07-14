package com.masterwok.stream_video_android.utils;

import com.masterwok.stream_video_android.contracts.StreamFactory;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.InputStream;


public class WriteStreamRunner implements Runnable {

    private WebSocket webSocket;
    private StreamFactory streamFactory;
    private int chunkSize;

    public WriteStreamRunner(
            WebSocket webSocket,
            StreamFactory streamFactory,
            int chunkSize
    ) {
        this.webSocket = webSocket;
        this.streamFactory = streamFactory;
        this.chunkSize = chunkSize;
    }

    @Override
    public void run() {
        InputStream inputStream = streamFactory.getStream();
        byte[] buffer = new byte[chunkSize];

        if (inputStream == null) {
            webSocket.close();
        }

        try {
            while (inputStream.read(buffer) != -1) {
                webSocket.send(buffer);
            }

            webSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
