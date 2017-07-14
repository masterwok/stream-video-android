package com.masterwok.stream_video_android.services;

import android.os.Handler;
import android.os.HandlerThread;

import com.masterwok.stream_video_android.contracts.StreamFactory;
import com.masterwok.stream_video_android.utils.WriteStreamRunner;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketStreamService extends WebSocketServer {

    public static final int ChunkSize = 2000000;
    private final StreamFactory streamFactory;
    private HandlerThread streamWriteThread;

    public WebSocketStreamService(
            int port,
            StreamFactory streamFactory
    ) {
        super(new InetSocketAddress(port));
        this.streamFactory = streamFactory;
    }

    @Override
    public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
        super.onWebsocketClosing(conn, code, reason, remote);
    }

    @Override
    public void onOpen(
            final WebSocket conn,
            ClientHandshake handshake
    ) {
        streamWriteThread = new HandlerThread("WebSocketThread");
        streamWriteThread.start();

        new Handler(streamWriteThread.getLooper()).post(
                new WriteStreamRunner(conn, streamFactory, ChunkSize)
        );
    }


    @Override
    public void onClose(
            WebSocket conn,
            int code,
            String reason,
            boolean remote
    ) {
        streamWriteThread.quit();
        int x = 1;
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
