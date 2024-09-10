package com.tdm.sigmaexossaiexample;


import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.tdm.adstracking.AdsTracking;
import com.tdm.adstracking.FullLog;
import com.tdm.adstracking.core.listener.ResponseInitListener;

public class MainActivity extends AppCompatActivity implements Player.Listener {
    Player player;
    PlayerView playerView;
    // PlayerView playerView;
    public String SESSION_URL =
            "https://ssai-stream-dev.sigmaott.com/manifest/manipulation/session/bea37c7f-bea6-4fc4-8a49-6a2dc385f2b8/origin04/scte35-av4s-clear/master.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view_id);

        Context context = this;
        playerView.post(new Runnable() {
            @Override
            public void run() {
                // Lấy width và height của view player sau khi playerView đã được layout
                int width = playerView.getWidth();
                int height = playerView.getHeight();

                // In ra kích thước
                AdsTracking.getInstance().init(
                        context,
                        playerView,
                        SESSION_URL,
                        new ResponseInitListener() {
                            @Override
                            public void onInitSuccess(String url) {
                                configPlayer(url);
                            }

                            @Override
                            public void onInitFailed(String url, String msg) {}
                        });
            }
        });
    }

    private void configPlayer(String url) {
      player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this).setLiveTargetOffsetMs(5000))
                .build();
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(url)
                        .setLiveConfiguration(new MediaItem.LiveConfiguration.Builder().build())
                        .build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(false);
        playerView.setPlayer(player);

        AdsTracking.getInstance().initPlayer(player);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        AdsTracking.getInstance().destroy();
        super.onDestroy();
    }

}