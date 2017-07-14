package com.masterwok.stream_video_android.services;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.masterwok.stream_video_android.contracts.StreamFactory;
import com.masterwok.stream_video_android.listeners.TorrentStreamListener;
import com.masterwok.stream_video_android.utils.InputStreamWebSocketRunner;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class TorrentStreamService {

    private static final TorrentStreamService instance = new TorrentStreamService();

    private Torrent currentTorrent;
    private TorrentStream torrentStream;

    public static TorrentStreamService getInstance() {
        return instance;
    }

    public Torrent getCurrentTorrent() {
        return currentTorrent;
    }

    private TorrentStreamService() {
    }

    public void startStream(String url) {
        torrentStream = TorrentStream.init(
                new TorrentOptions.Builder()
                        .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                        .removeFilesAfterStop(true)
                        .build()
        );

        torrentStream.addListener(new TorrentStreamListener() {
            @Override
            public void onStreamStarted(Torrent torrent) {
                super.onStreamStarted(torrent);
                currentTorrent = torrent;
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                super.onStreamReady(torrent);
                startWebSocketStream();
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                super.onStreamError(torrent, e);

                if(currentTorrent != null) {
                    currentTorrent = null;
                }
            }
        });

        torrentStream.startStream(url);
    }

    private void startWebSocketStream() {
        HandlerThread webSocketThread = new HandlerThread("WebSocketThread");
        webSocketThread.start();

        StreamFactory streamFactory = new StreamFactory() {
            @Override
            public InputStream getStream() {
                InputStream stream = null;

                try {
                    stream = currentTorrent.getVideoStream();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return stream;
            }
        };

        new Handler(webSocketThread.getLooper()).post(
                new InputStreamWebSocketRunner(streamFactory)
        );
    }
}
