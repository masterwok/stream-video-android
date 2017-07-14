package com.masterwok.stream_video_android.listeners;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;


public class TorrentStreamListener implements TorrentListener {

    @Override
    public void onStreamPrepared(Torrent torrent) {
        torrent.startDownload();
    }

    @Override
    public void onStreamReady(final Torrent torrent) {
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
