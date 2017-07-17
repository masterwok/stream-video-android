package com.masterwok.stream_video_android.contracts;


import java.io.File;

public interface ITorrentStreamService {
    void startStream(
            String url,
            File saveLocation
    );

    void stopStream();
}
