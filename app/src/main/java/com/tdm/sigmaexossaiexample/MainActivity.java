package com.tdm.sigmaexossaiexample;


import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tdm.adstracking.AdsTracking;
import com.tdm.adstracking.FullLog;
import com.tdm.adstracking.core.listener.ResponseInitListener;

public class MainActivity extends AppCompatActivity implements Player.Listener {
    private final String TAG = "MainActivity";
    ExoPlayer player;
    PlayerView playerView;
    public String sourceUrl = "https://cdn-lrm-test.sigma.video/manifest/origin04/scte35-av4s-clear/master.m3u8";
    private String adsEndpoint = "68cc9206-2d25-4de6-b2a9-116369a86e94";
    EditText editTextSource = null;
    EditText editTextAdsEndpoint = null;
    Button reloadButton = null;
    private Context mainContext = null;
    Player.Listener playerListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdsTracking.getInstance().startServer();

        mainContext = this;

        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.player_view_id);
        editTextSource = findViewById(R.id.source_hls);
        editTextAdsEndpoint = findViewById(R.id.ads_endpoint);
        reloadButton = findViewById(R.id.reload_player);

        editTextSource.setText(sourceUrl);
        editTextAdsEndpoint.setText(adsEndpoint);

        // Initialize the ProgressDialogManager to fake loading
        ProgressDialogManager.getInstance().init(this);

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReloadPlayer();
            }
        });
        playerView.post(new Runnable() {
            @Override
            public void run() {

                initAdsTracking();

            }
        });

    }

    private void initAdsTracking() {
        AdsTracking.getInstance().init(
                this,
                playerView,
                sourceUrl,
                adsEndpoint,
                new ResponseInitListener() {
                    @Override
                    public void onInitSuccess(String url) {
                        ProgressDialogManager.getInstance().hideLoading();
                        configPlayer(url);
                    }

                    @Override
                    public void onInitFailed(String url, String msg) {
                        Toast.makeText(mainContext, msg, Toast.LENGTH_SHORT).show();
                        ProgressDialogManager.getInstance().hideLoading();
                    }
                });
    }

    private void configPlayer(String url) {
        player = new ExoPlayer.Builder(this).build();
        AdsTracking.getInstance().initPlayer(player);
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        if (playerListener != null) {
            player.removeListener(playerListener);
        }

        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.d(TAG + "_onPlaybackStateChanged=>: ", String.valueOf(playbackState));
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG + "_onPlayerError=>: ", error.getMessage());
                ProgressDialogManager.getInstance().hideLoading();
            }
        };
        player.addListener(playerListener);
    }

    private void handleReloadPlayer() {
        ProgressDialogManager.getInstance().showLoading();

        sourceUrl = editTextSource.getText().toString();
        adsEndpoint = editTextAdsEndpoint.getText().toString();

        player.stop();
        player.release();
        AdsTracking.getInstance().destroy();

        //time out to fake load content
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            initAdsTracking();
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        AdsTracking.getInstance().destroy();
        player.removeListener(playerListener);
        super.onDestroy();
    }

}