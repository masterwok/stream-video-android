package com.masterwok.stream_video_android.services;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.masterwok.stream_video_android.Constants.Config;
import com.masterwok.stream_video_android.factories.TorrentStreamFactory;
import com.masterwok.stream_video_android.listeners.TorrentStreamListener;

import java.io.IOException;

public class TorrentStreamService {

    private static final TorrentStreamService instance = new TorrentStreamService();

    private TorrentStream torrentStream;
    private Torrent currentTorrent;

    public static TorrentStreamService getInstance() {
        return instance;
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
                startHttpServer();
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                super.onStreamError(torrent, e);
                currentTorrent = null;
            }
        });

        torrentStream.startStream(url);
    }


    private void startHttpServer() {
        HandlerThread httpServerThread = new HandlerThread("HttpServerThread");
        httpServerThread.start();

        new Handler(httpServerThread.getLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new HttpServer(
                                    Config.HttpServerPort,
                                    Config.ChunkSize,
                                    new TorrentStreamFactory(currentTorrent)
                            ).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
