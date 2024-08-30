# sigma-ssai-exo-sdk

### Requirement

```
minSdk 21
```

### Import SDK

In rootProject/build.gradle add:

```
google()
mavenCentral()
maven {
url "https://maven.sigma.video"
}

In rootProject/app/build.gradle add:
```

implementation 'com.sigma.ssai:sigma-ssai-exo:1.0.0'

#### Example

##### Step 1:Initialize SDK

init sdk in onCreate of Activity

```
 AdsTracking.getInstance().init(
                        mainActivity,
                        playerView,
                        SESSION_URL,
                        new ResponseInitListener() {
                            @Override
                            public void onInitSuccess(String url) {
                                configPlayer();
                            }
                            @Override
                            public void onInitFailed(String url, int code, String msg) {
                                Log.d("onInitFailed:", + code + ':' + msg);

                            }
                        });
```

With:
SESSION_URL: link sesssion(to get link video and link tracking)
playerView: player View
mainActivity: current activity

After initializing the session in the SDK, the SDK returns a SourceUrl --> configPlayer(url)

##### Step 2: After init sdk, config Player (funtion configPlayer())

call configPlayer after init exoplayer

        `````
        AdsTracking.getInstance().initPlayerView(exoPlayer);

##### Note:

Call `destroy()` when the activity is destroyed

```
@Override
protected void onDestroy() {
    AdsTracking.getInstance().destroy();
    super.onDestroy();
}
```
