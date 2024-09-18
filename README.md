# Sigma DAI exo ssai plugin

### Requiremen

minSdk 21

### Import SDK

In rootProject/build.gradle add:

```java
google()
mavenCentral()
maven {
    url "https://maven.sigma.video"
}
```

In rootProject/app/build.gradle add:

```java
    // install core media3 to demo
    implementation 'androidx.media3:media3-exoplayer:1.3.1'
    implementation 'androidx.media3:media3-ui:1.3.1'

    //install sdk
    implementation 'com.sigma.ssai:sigma-ssai-media3:1.0.6'
```

### Example

##### Step 1:Initialize SDK

init sdk in onCreate of Activity

```java
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
```

###### Note:

SESSION_URL: link sesssion(to get link video and link tracking)

playerView: player View

context: current context

After initializing the session in the SDK, the SDK returns a SourceUrl --> configPlayer(url)

##### Step 2: After init sdk, config Player (funtion configPlayer())

call configPlayer after init exoplayer: AdsTracking.getInstance().initPlayer(player);

###### Note:

Call `destroy()` when the activity is destroyed

```java
    @Override
    protected void onDestroy() {
        AdsTracking.getInstance().destroy();
        super.onDestroy();
    }
```
