package com.masterwok.stream_video_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.masterwok.stream_video_android.services.TorrentStreamService;
import com.masterwok.stream_video_android.services.WebSocketStreamService;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements TorrentListener {
    private HandlerThread websocketThread;
    private Handler threadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final String streamUrl = "magnet:?xt=urn:btih:608c9e4070398f02757492cf3817783ee93fa32d&dn=Hackers+%281995%29+720p+BrRip+x264+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";
        final String streamUrl = "magnet:?xt=urn:btih:475e515a79daecc6917b4606dd06d44045263efd&dn=Ghost+in+the+Shell+%282017%29+%5B1080p%5D+%5BEnglish%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";

        TorrentStreamService
                .getInstance()
                .startStream(streamUrl, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onStreamPrepared(Torrent torrent) {
        torrent.startDownload();
    }

    @Override
    public void onStreamStarted(Torrent torrent) {

    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {

    }

    @Override
    public void onStreamReady(Torrent torrent) {
        websocketThread = new HandlerThread("WebSocketThread");
        websocketThread.start();

        threadHandler = new Handler(websocketThread.getLooper());
        threadHandler.post(new Derp(torrent));
    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {

    }

    @Override
    public void onStreamStopped() {

    }

    public class Derp implements Runnable {

        private final Torrent torrent;

        public Derp(Torrent torrent) {
            this.torrent = torrent;
        }

        @Override
        public void run() {
            new WebSocketStreamService(1234, torrent).start();
        }
    }
}
