package com.masterwok.stream_video_android.listeners;

import android.os.Handler;
import android.os.HandlerThread;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.masterwok.stream_video_android.utils.TorrentStreamWebSocketRunner;


public class TorrentStreamListener implements TorrentListener {

    private HandlerThread webSocketThread;

    @Override
    public void onStreamPrepared(Torrent torrent) {
        torrent.startDownload();
    }

    @Override
    public void onStreamReady(Torrent torrent) {
        webSocketThread = new HandlerThread("WebSocketThread");
        webSocketThread.start();

        new Handler(webSocketThread.getLooper())
                .post(new TorrentStreamWebSocketRunner(torrent));
    }

    @Override
    public void onStreamStarted(Torrent torrent) {
    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {
    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
    }

    @Override
    public void onStreamStopped() {
    }
}
