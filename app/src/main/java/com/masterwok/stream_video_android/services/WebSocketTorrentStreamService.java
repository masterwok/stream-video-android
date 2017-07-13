package com.masterwok.stream_video_android.services;

import android.util.Log;

import com.github.se_bastiaan.torrentstream.Torrent;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class WebSocketTorrentStreamService extends WebSocketServer {

    private final Torrent torrent;
    private InputStream videoStream;
    private int offset = 0;

    public WebSocketTorrentStreamService(
            int port,
            Torrent torrent) {
        super(new InetSocketAddress(port));

        this.torrent = torrent;

        try {
            videoStream = torrent.getVideoStream();
        } catch(Exception ex) {
            Log.d("WebSocketServer", ex.toString());
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        byte[] buffer = new byte[2000];

        if(videoStream == null) {
            conn.close();
        }

        try {
            int count = videoStream.read(buffer);

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
