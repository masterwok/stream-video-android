package com.masterwok.stream_video_android.services;


import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.masterwok.stream_video_android.Constants.Config;
import com.masterwok.stream_video_android.contracts.ITorrentStreamService;
import com.masterwok.stream_video_android.factories.TorrentStreamFactory;
import com.masterwok.stream_video_android.listeners.TorrentStreamListener;

import java.io.File;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class TorrentStreamService implements ITorrentStreamService {

    private static final ITorrentStreamService instance = new TorrentStreamService();

    private TorrentStream torrentStream;
    private Torrent currentTorrent;
    private NanoHTTPD httpStreamService;

    public static ITorrentStreamService getInstance() {
        return instance;
    }

    private TorrentStreamService() {
    }

    public void startStream(
            String url,
            File saveLocation
    ) {
        torrentStream = TorrentStream.init(
                new TorrentOptions.Builder()
                        .saveLocation(saveLocation)
                        .removeFilesAfterStop(true)
                        .build()
        );

        torrentStream.addListener(createTorrentStreamListener());
        torrentStream.startStream(url);
    }

    private TorrentStreamListener createTorrentStreamListener() {
        return new TorrentStreamListener() {
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
                stopStream();
            }
        };
    }

    public void stopStream() {
        torrentStream.stopStream();
        httpStreamService.stop();
        currentTorrent = null;
    }

    private void startHttpServer() {
        httpStreamService = new HttpStreamService(
                Config.HttpServerPort,
                Config.ChunkSize,
                new TorrentStreamFactory(currentTorrent)
        );

        try {
            httpStreamService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
