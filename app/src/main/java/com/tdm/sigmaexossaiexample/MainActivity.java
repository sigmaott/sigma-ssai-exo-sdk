package com.tdm.sigmaexossaiexample;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.tdm.adstracking.AdsTracking;
//import com.tdm.adstracking.AdsTracking;
import com.tdm.adstracking.FullLog;
import com.tdm.adstracking.core.listener.ResponseInitListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements  Player.Listener {
    ExoPlayer exoPlayer;
    StyledPlayerView playerView;

    public String SESSION_URL =
            "https://ssai-stream-dev.sigmaott.com/manifest/manipulation/session/bea37c7f-bea6-4fc4-8a49-6a2dc385f2b8/origin04/scte35-av4s-clear/master.m3u8";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view_id);

        Activity mainActivity = this;
        //
        playerView.post(new Runnable() {
            @Override
            public void run() {
                // In ra kích thước
                AdsTracking.getInstance().init(
                        mainActivity,
                        playerView,
                        SESSION_URL,
                        new ResponseInitListener() {
                            @Override
                            public void onInitSuccess(String url) {
                                configPlayer(url);
                            }
                            @Override
                            public void onInitFailed(String url, int code, String msg) {
                                Log.d("onInitFailed:", + code + ':' + msg);
                            }
                        });
            }
        });
    }

    private void configPlayer(String url) {
        exoPlayer = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this).setLiveTargetOffsetMs(5000))
                .build();
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(url)
                        .setLiveConfiguration(new MediaItem.LiveConfiguration.Builder().build())
                        .build();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(false);
        playerView.setPlayer(exoPlayer);

        AdsTracking.getInstance().initPlayerView(exoPlayer);
    }
    @SuppressLint("SetTextI18n")

    @Override
    protected void onDestroy() {
        AdsTracking.getInstance().destroy();
        super.onDestroy();
    }
}