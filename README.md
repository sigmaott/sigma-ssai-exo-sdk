# SSAITracking SDK Integration Guide

 **Version** : 1.0.5

**Organization** : Thủ Đô Multimedia

## Table of Contents

1. [Introduction](#1-introduction)
2. [Scope](#2-scope)
3. [System Requirements](#3-system-requirements)
4. [App Requirements](#4-app-requirements)
5. [Installation](#5-installation)
6. [Usage](#6-usage)
   - [SDK Initialization](#61-sdk-initialization)
   - [Listening for ResponseInitListener](#62-listening-for-responseinitlistener)
   - [Prepare and Play the Media Source](#63-prepare-and-play-the-media-source)
   - [Clean Up Resources](#64-clean-up-resources)
7. [Important Notes](#8-important-notes)
8. [Conclusion](#8-conclusion)
9. [References](#9-references)

## 1. Introduction

This document provides a guide for integrating and using the SSAITracking SDK for Android applications, specifically using Media3. It includes detailed information on installation, SDK initialization, and handling necessary callbacks.

## 2. Scope

This document applies to iOS developers who want to integrate the SSAITracking SDK into their applications, including requesting IDFA access as per App Tracking Transparency requirements.

## 3. System Requirements

* **Operating System** : Android 5.0 and above
* **Device** : Physical device required

## 4. App Requirements

- **Android minimum SDK**: 24
- **Android target SDK:** 34
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

2. **Allowed to use HTTP (unencrypted protocol) to communicate with the local server:**
   In `rootProject/app/src/main/AndroidManifest.xml` add the following:

   ```
   <manifest ...>
       <uses-permission android:name="android.permission.INTERNET" />
       <application
           ...
           android:usesCleartextTraffic="true"
           ...>
           ...
       </application>
   </manifest>
   ```
3. **Add Library Media3 player (skip if you're already using a different media3 version)**:
   In your **app/build.gradle** file, include the following dependencies:
4. **Add Sigma SSAI plugin** :
   In your **app/build.gradle** file, include the following dependencies:

```swift
dependencies {
    ...
    implementation 'com.sigma.ssai:sigma-ssai-media3-cspm:1.0.5'
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

* **Initialize the SDK with the required parameters** :

```swift
AdsTracking.getInstance().init(
        this, 
        playerView, 
        sourceUrl,
        new ResponseInitListener() {
            @Override
            public void onInitFailed(String url, String msg) {
                //generate source failed
            }
            @Override
            public void onInitSuccess(String modifiUrl) {
                 //generate source success
            }
        }
);
```

### Parameter Definitions

* `this `: Reference to the current Activity.
* `playerView `: The view where the video player will be displayed.
* `sourceUrl `: String url source for the main content.

### 6.2 Listening for ResponseInitListener

* **Success Callback** :
  Called `onInitSuccess` when initialization succeeds; returns modified source for tracking
* **Failure Callback** :
  Called `onInitFailed` when initialization fails; returns original source url.

### 6.3 Init Player to listen event in sdk

* **Call `initPlayer()` and pass initialized player**
  **Important note:**
  The Player passed to the AdsTracking.getInstance().initPlayer(player) function must always be a direct instance of ExoPlayer. Avoid using instances created through MediaController or any other wrapping mechanism, to ensure AdsTracking works properly with events and player state.

```
import androidx.media3.common.Player;
...

Player player = new ExoPlayer.Builder(this).build();
AdsTracking.getInstance().initPlayer(player);
```

* **Play the Media Source**

Use the modifi source returned by the `onInitSuccess`(or originalsource returned by the `onInitFailed`) callback to initialize and play the media.
Here's an example using Media3:

```swift
PlayerView playerView;

...

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

## 7. Important Notes

Always remember to call `setPlayer()` on the SDK after initializing the ExoPlayer or replacing the current item. This ensures that the SDK correctly recognizes the active video player and can effectively manage ad tracking. If you need to change the `adsEndpoint` or `adsParams`, it is essential to reinitialize the SDK. This ensures that the new endpoint and new params is properly configured and used for tracking.

## 8. Conclusion

By following the steps outlined above, you can successfully integrate and utilize the SSAITracking SDK within your application. Ensure that you handle both success and failure callbacks to provide a seamless user experience.

## 9. References

SSAITracking demo link: [Demo](https://github.com/sigmaott/sigma-ssai-exo-sdk)
