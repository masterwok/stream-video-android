package com.masterwok.stream_video_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.masterwok.stream_video_android.contracts.StreamFactory;
import com.masterwok.stream_video_android.services.TorrentStreamService;
import com.masterwok.stream_video_android.utils.InputStreamWebSocketRunner;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String streamUrl = "magnet:?xt=urn:btih:475e515a79daecc6917b4606dd06d44045263efd&dn=Ghost+in+the+Shell+%282017%29+%5B1080p%5D+%5BEnglish%5D&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969";

        TorrentStreamService
                .getInstance()
                .startStream(streamUrl);

//        createTestFileStream();
    }


    private void createTestFileStream() {
        HandlerThread webSocketThread = new HandlerThread("WebSocketThread");
        webSocketThread.start();

        StreamFactory streamFactory = new StreamFactory() {
            @Override
            public InputStream getStream() {
                return getResources().openRawResource(R.raw.small);
            }
        };

        new Handler(webSocketThread.getLooper()).post(
                new InputStreamWebSocketRunner(streamFactory)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}
