package com.masterwok.stream_video_android.services;

import com.masterwok.stream_video_android.contracts.StreamFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class WebSocketStreamService extends WebSocketServer {

    public static final int ChunkSize = 2000000;
    private final StreamFactory streamFactory;

    public WebSocketStreamService(int port, StreamFactory streamFactory) {
        super(new InetSocketAddress(port));
        this.streamFactory = streamFactory;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        InputStream inputStream = streamFactory.getStream();
        byte[] buffer = new byte[ChunkSize];

        if (inputStream == null) {
            conn.close();
        }

        try {
            while (inputStream.read(buffer) != -1) {
                conn.send(buffer);
            }

            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }
}
