package com.masterwok.stream_video_android.services;

import android.os.Environment;

import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.masterwok.stream_video_android.listeners.TorrentStreamListener;

public class TorrentStreamService {

    private static final TorrentStreamService instance = new TorrentStreamService();

    private TorrentStream torrentStream;

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

        torrentStream.addListener(new TorrentStreamListener());
        torrentStream.startStream(url);
    }

}
