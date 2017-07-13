package com.masterwok.stream_video_android.services;

import com.github.se_bastiaan.torrentstream.Torrent;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class WebSocketStreamService extends WebSocketServer {

    private final Torrent torrent;
    private int offset = 0;

    public WebSocketStreamService(
            int port,
            Torrent torrent) {
        super(new InetSocketAddress(port));

        this.torrent = torrent;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        byte[] chunk = new byte[2000000];

        try {

            InputStream stream = torrent.getVideoStream();

            int read = stream.read(chunk);

            if(read > 0) {
                conn.send(chunk);
                offset += chunk.length;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
