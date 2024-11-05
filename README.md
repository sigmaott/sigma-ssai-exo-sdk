# SSAITracking SDK Integration Guide

 **Version** : 1.0.0

**Organization** : Thủ Đô Multimedia

## Table of Contents

1. Introduction
2. Scope
3. System Requirements
4. Requirements
5. Installation
6. Usage
   - SDK Initialization
   - Listening for ResponseInitListener
   - Prepare and Play the Media Source
   - Clean Up Resources
7. Conclusion

## 1. Introduction

This document provides a guide for integrating and using the SSAITracking SDK for Android applications, specifically using Media3. It includes detailed information on installation, SDK initialization, and handling necessary callbacks.

## 2. Scope

This document applies to iOS developers who want to integrate the SSAITracking SDK into their applications, including requesting IDFA access as per App Tracking Transparency requirements.

## 3. System Requirements

* **Operating System** : Android 5.0 and above
* **Device** : Physical device required

## 4. Requirements

- **Minimum SDK**: 21
- **Player Library**: Media3

## 5. Installation

1. **Add Repository Sigma** :
   In your `rootProject/build.gradle` file, add the following repositories:

```swift
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url "https://maven.sigma.video"
        }
    }
}
```

2. **Add Library Media3 player (skip if you're already using a different media3 version)**:
   In your **app/build.gradle** file, include the following dependencies:
3. **Add Sigma SSAI plugin** :
   In your **app/build.gradle** file, include the following dependencies:

```swift
dependencies {
    ...
    implementation 'com.sigma.ssai:sigma-ssai-media3-cspm:1.0.0'
    ...
}
```

## 6. Usage

### 6.1 SDK Initialization

* **Import the SDK** :

```swift
import com.tdm.adstracking.AdsTracking;
```

* **Call the start function when your application launches** :

```swift
AdsTracking.getInstance().startServer();
```

```

```

* **Initialize the SDK with the required parameters** :

```swift
AdsTracking.getInstance().init(
        context, 
        playerView, 
        sourceUrl,
        adsUrl,
        new ResponseInitListener() {
            @Override
            public void onInitFailed(String url, String msg) {
                //logic here
            }
            @Override
            public void onInitSuccess(String modifiUrl) {
                 //logic here
            }
        }
);
```

### Parameter Definitions

* `context `: A reference to the current instance of your class, which must conform to the `SigmaSSAIInterface` protocol to handle callbacks.
* `playerView `: The view where the video player will be displayed.
* `sourceUrl `: string url source for the main content.
* `adsUrl `: string url source for the ads.

### 6.2 Listening for ResponseInitListener

* **Success Callback** :
  Called `onInitSuccess` when initialization succeeds; returns modified source for tracking
* **Failure Callback** :
  Called `onInitFailed` when initialization fails; returns original source url.

### 6.3 Prepare and Play the Media Source

Use the modifi source returned by the `onInitSuccess`(or originalsource returned by the `onInitFailed`) callback to initialize and play the media. Here's an example using Media3:

```swift
 
ExoPlayer player;
PlayerView playerView;

...
player = new Player.Builder(this).build();
playerView.setPlayer(player);
MediaItem mediaItem = MediaItem.fromUri(Uri.parse(modifiUrl));
player.setMediaItem(mediaItem);
player.prepare();
player.setPlayWhenReady(true);
```

### 6.4 Clean Up Resources

To prevent memory leaks, call the `destroy() `method when the activity or player is destroyed. This ensures that the ad tracking resources are cleaned up properly.

```groovy
@Override
protected void onDestroy() {
    AdsTracking.getInstance().destroy();
    super.onDestroy();
}
```

## 7. Conclusion

By following the steps outlined above, you can successfully integrate and utilize the SSAITracking SDK within your application. Ensure that you handle both success and failure callbacks to provide a seamless user experience.
