package com.masterwok.stream_video_android.services;

import android.util.Log;

import com.github.se_bastiaan.torrentstream.Torrent;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class WebSocketStreamService extends WebSocketServer {

    private final int ChunkSize = 2000;

    private InputStream inputStream;

    public WebSocketStreamService( int port, InputStream stream) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        byte[] buffer = new byte[ChunkSize];

        if(inputStream == null) {
            conn.close();
        }

        try {
            int count = inputStream.read(buffer);

            if(count > 0) {
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
