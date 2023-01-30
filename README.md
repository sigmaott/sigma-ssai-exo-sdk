# sigma-ssai-exo-sdk
### Requirement 

```
minSdk 21
```

### Import SDK

Download [sigma-dai-exo-ssai-sdk](https://github.com/truongnguyen1804/sigma-ssai-exo-sdk/blob/main/sigma-dai-exo-ssai/sigma-dai-exo-ssai.aar) to your project and configure it as a library.

#### Example

##### Step 1:

There are two ways to install the sdk:

###### Option 1: Initialize session in SDK (Init session)

After initializing the session in the SDK, the SDK returns a SourceUrl --> configPlayer(url)

```
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
```

###### Option 2: SourceUrl and TrackingUrl are available

```
AdsTracking.getInstance().setUrlTracking(urlTracking);
configPlayer(urlSource);
```



##### Step 2:

###### Provide SDK timing from player via AdsTracking.TrackingParams

Initialize an AdsTracking.TrackingParams trackingParams interface:

```
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
```

Where `onTimeUpdate()` needs to return the player's time

##### Step 3: 

Listen to player's ready to play event

```
@Override
public void onPlaybackStateChanged(int playbackState) {
    if (playbackState == ExoPlayer.STATE_READY) {
        AdsTracking.getInstance().startTracking();
    }

}
```

Start tracking by calling `AdsTracking.getInstance().startTracking()` when the player's playback event is ready.



##### Note:

Call `stopTracking()` when the player has an error

```
@Override
public void onPlayerError(PlaybackException error) {
    AdsTracking.getInstance().stopTracking();
}
```

Call `destroy()` when the activity is destroyed

```
@Override
protected void onDestroy() {
    AdsTracking.getInstance().destroy();
    super.onDestroy();
}
```
