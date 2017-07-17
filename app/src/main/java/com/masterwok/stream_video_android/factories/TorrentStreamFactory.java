package com.masterwok.stream_video_android.factories;

import com.github.se_bastiaan.torrentstream.Torrent;
import com.masterwok.stream_video_android.contracts.IStreamFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;

public class TorrentStreamFactory implements IStreamFactory {

    private Torrent torrent;

    public TorrentStreamFactory(Torrent torrent) {
        this.torrent = torrent;
    }

    @Override
    public InputStream getStream() {
        InputStream stream = null;

        try {
            stream = torrent.getVideoStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return stream;
    }

    @Override
    public long getLength() {
        return torrent.getVideoFile().length();
    }

    @Override
    public String getContentType() {
        return URLConnection.guessContentTypeFromName(
                torrent.getVideoFile().getName()
        );
    }
}
