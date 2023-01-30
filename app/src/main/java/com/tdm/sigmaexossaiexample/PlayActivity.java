package com.tdm.sigmaexossaiexample;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.gson.Gson;
import com.tdm.adstracking.AdsTracking;

public class PlayActivity extends AppCompatActivity {
    ExoPlayer exoPlayer;
    StyledPlayerView playerView;
    public static final String TAG = "PlayActivity";
    private Timeline.Window windowLive = null;
    private Timeline.Period periodLive = null;
    public static final String TAG_LISTENER = "ExoPlayer_listener";
    private AdsTracking.TrackingParams trackingParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
    }


    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            int typeEx = intent.getIntExtra("type", 0);

            // input is url SSAI
            if (typeEx == MainActivity.EX_SSAI_LINK) {
                String urlSSAI = intent.getStringExtra("url_ssai");
                AdsTracking.getInstance().initSession(urlSSAI, new AdsTracking.InitSessionListener() {
                            @Override
                            public void onResponse(int code, String url) {
                                PlayActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        configPlayer(url);
                                    }
                                });
                            }
                            @Override
                            public void onError(int code) {
                                Log.e(TAG, "Code " + code);
                            }

                        });
            }
            // input is url source and url tracking
            else if (typeEx == MainActivity.EX_SOURCE_LINK) {
                String urlSource = intent.getStringExtra("url_source");
                String urlTracking = intent.getStringExtra("url_tracking");
                AdsTracking.getInstance().setUrlTracking(urlTracking);
                configPlayer(urlSource);

            }
        }
        playerView = findViewById(R.id.player_view_id);

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
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();
        playerView.setPlayer(exoPlayer);
        playerView.setUseController(false);
        Handler handler = new Handler();
        handler.postDelayed(() -> updatePosition(), 1000);
        exoPlayer.addListener(listener);

        trackingParams = new AdsTracking.TrackingParams() {
            @Override
            public long onTimeUpdate() {
                if (exoPlayer != null) {
                    if (exoPlayer.isPlayingAd())
                        return -1;
                    if (exoPlayer.isCurrentMediaItemDynamic()) {
                        // live
                        if (windowLive == null) {
                            windowLive = new Timeline.Window();
                        }
                        if (periodLive == null) {
                            periodLive = new Timeline.Period();
                        }
                        if (!exoPlayer.getCurrentTimeline().isEmpty()) {
                            long positionInPeriod = exoPlayer.getCurrentPosition(); // in period
                            long position = positionInPeriod;
                            Timeline.Period p = exoPlayer.getCurrentTimeline().getPeriod(exoPlayer.getCurrentPeriodIndex(), periodLive);
                            long positionInWindows = p.getPositionInWindowMs();
                            position -= positionInWindows;
                            return position;
                        }
                    } else {
                        // vod
                        long position = exoPlayer.getCurrentPosition();
                        return position;
                    }
                }
                return -1;
            }
        };
        AdsTracking.getInstance().setParamsTracking(trackingParams);
    }

    // get current time media
    @SuppressLint("SetTextI18n")
    private void updatePosition() {
        if (exoPlayer == null)
            return;
        if (exoPlayer.isPlayingAd())
            return;
        if (exoPlayer.isCurrentMediaItemDynamic()) {
            // live
            if (windowLive == null) {
                windowLive = new Timeline.Window();
            }
            if (periodLive == null) {
                periodLive = new Timeline.Period();
            }
            if (!exoPlayer.getCurrentTimeline().isEmpty()) {
                long positionInPeriod = exoPlayer.getCurrentPosition(); // in period
                long positionTotal = exoPlayer.getDuration();
                long position = positionInPeriod;
                Timeline.Period p = exoPlayer.getCurrentTimeline().getPeriod(exoPlayer.getCurrentPeriodIndex(), periodLive);
                long positionInWindows = p.getPositionInWindowMs();
                position -= positionInWindows;
                positionTotal -= positionInWindows;
                ((TextView) findViewById(R.id.info_id)).setText((position / 1000) + "/" + (positionTotal / 1000));
                Handler handler = new Handler();
                handler.postDelayed(() -> updatePosition(), 1000);
            }
        }
    }

    private Player.Listener listener = new Player.Listener() {

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == ExoPlayer.STATE_READY) {
                AdsTracking.getInstance().startTracking();
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            AdsTracking.getInstance().stopTracking();
        }
    };

    @Override
    protected void onDestroy() {
        AdsTracking.getInstance().destroy();
        super.onDestroy();
    }

}