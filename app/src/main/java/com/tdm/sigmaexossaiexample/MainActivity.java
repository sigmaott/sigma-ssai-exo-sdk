package com.tdm.sigmaexossaiexample;


import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Metadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.extractor.metadata.id3.Id3Frame;
import androidx.media3.extractor.metadata.id3.TextInformationFrame;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.media3.common.C;

import com.tdm.adstracking.AdsTracking;
import com.tdm.adstracking.core.SigmaError;
import com.tdm.adstracking.core.listener.ResponseInitListener;
import com.tdm.adstracking.define.LogLevel;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Player.Listener {
    private final String TAG = "MainActivity=>>";
    ExoPlayer player;
    PlayerView playerView;
    public String sourceUrl = "https://stream-cdn.sigmadrm.com/manifest/channel-test/masterhls-ts-4s.m3u8";
    Spinner spinnerSource = null;
    EditText editTextAdsEndpoint = null;
    Button reloadButton = null;
    private Context mainContext = null;
    Player.Listener playerListener = null;
    private String[] streamUrls;
    private String[] drmLicenseUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdsTracking.getInstance().startServer();

        AdsTracking.getInstance().setManifestTimeout(300); // Set manifest timeout to 100ms for testing purposes
//        AdsTracking.getInstance().setUseNonce(true);

        mainContext = this;

        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.player_view_id);
        spinnerSource = findViewById(R.id.source_hls);
        editTextAdsEndpoint = findViewById(R.id.ads_endpoint);
        reloadButton = findViewById(R.id.reload_player);

        // Setup spinner with stream sources
        setupSourceSpinner();
        
        editTextAdsEndpoint.setText("da914c58-5c6e-41b7-93b7-0597c4a983ee");

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

    private void setupSourceSpinner() {
        // Get arrays from resources
        String[] streamSources = getResources().getStringArray(R.array.stream_sources);
        streamUrls = getResources().getStringArray(R.array.stream_urls);
        drmLicenseUrls = getResources().getStringArray(R.array.drm_license_urls);
        
        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, streamSources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Set adapter to spinner
        spinnerSource.setAdapter(adapter);
        
        // Set default selection (HLS 4s Segments)
        spinnerSource.setSelection(1);
    }

    private void initAdsTracking() {
        // Get selected URL from spinner
        int selectedPosition = spinnerSource.getSelectedItemPosition();
        sourceUrl = streamUrls[selectedPosition];
        
        String adsEndpoint = editTextAdsEndpoint.getText().toString().trim();
        
        // Set ads endpoint
        AdsTracking.getInstance().setAdsEndpoint(sourceUrl, adsEndpoint);

        //if you want to set custom data
        String customDataString = "{\"key1\": \"value1\"}";
        AdsTracking.getInstance().setCustomData(sourceUrl, customDataString);


        AdsTracking.getInstance().init(
                this,
                playerView,
                sourceUrl,
                new ResponseInitListener() {
                    @Override
                    public void onInitSuccess(String url) {
                        ProgressDialogManager.getInstance().hideLoading();
                        configPlayer(url);
                    }

                    @Override
                    public void onInitFailed(String url, SigmaError sigmaError) {
                        Toast.makeText(mainContext, sigmaError.getDescription(), Toast.LENGTH_SHORT).show();
                        ProgressDialogManager.getInstance().hideLoading();
                    }
                });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void configPlayer(String url) {
        // Get selected position to check if DRM is needed
        int selectedPosition = spinnerSource.getSelectedItemPosition();
        String licenseUrl = drmLicenseUrls[selectedPosition];
        
        // Build player normally
        player = new ExoPlayer.Builder(this).build();
        AdsTracking.getInstance().initPlayer(player);
        playerView.setPlayer(player);
        
        MediaItem.Builder mediaItemBuilder = new MediaItem.Builder().setUri(Uri.parse(url));
        
        // Add DRM configuration to MediaItem if needed
        if (!licenseUrl.isEmpty()) {
            MediaItem.DrmConfiguration drmConfiguration = new MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(licenseUrl)
                    .build();
            mediaItemBuilder.setDrmConfiguration(drmConfiguration);
            Log.d(TAG, "DRM Configuration added to MediaItem with license: " + licenseUrl);
        }
        
        MediaItem mediaItem = mediaItemBuilder.build();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        if (playerListener != null) {
            player.removeListener(playerListener);
        }

        playerListener = new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.d(TAG,"_onPlaybackStateChanged=>: " + String.valueOf(playbackState));
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG,"_onPlayerError=>: " + error.getMessage());
                ProgressDialogManager.getInstance().hideLoading();
            }
        };
        player.addListener(playerListener);
    }

    private void handleReloadPlayer() {
        ProgressDialogManager.getInstance().showLoading();

        // URL will be updated in initAdsTracking from spinner selection

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