package com.masterwok.stream_video_android.utils;

import com.github.se_bastiaan.torrentstream.Torrent;
import com.masterwok.stream_video_android.services.WebSocketStreamService;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class TorrentStreamWebSocketRunner implements Runnable {
        public static final int Port = 1234;

        private final Torrent torrent;

        public TorrentStreamWebSocketRunner(Torrent torrent) {
            this.torrent = torrent;
        }

        @Override
        public void run() {

            try {
                InputStream videoStream = torrent.getVideoStream();
                new WebSocketStreamService(Port, videoStream).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
}