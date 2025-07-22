# SSAITracking SDK Integration Guide

 **Version** : 1.1.0

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
   - [Api support](#65-api-support)
7. [Important Notes](#7-important-notes)
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

- **Android minimum SDK**: 21
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
    implementation 'com.sigma.ssai:sigma-ssai-media3-cspm:1.1.0'
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
            public void onInitFailed(String url, SigmaError sigmaError) {
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
Called `onInitFailed` when initialization fails; returns original source url and SigmaError object including an error code and description

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

### 6.5 Api Support

* public void setManifestTimeout(int manifestTimeout)
Description:
Sets the timeout duration (in milliseconds) for manifest requests from the proxy to the origin server or CDN.
This controls how long the proxy should wait for a response before considering the request as failed.
Parameters:
`manifestTimeout`: Timeout value in milliseconds.

* public void setAdsEndpoint(String url, String adsEndpoint)
Description:
Configures the ad decisioning endpoint to be used when processing ad-related metadata requests for a specific stream.
This allows dynamic routing of ad requests to the correct ad server per content stream.
Parameters:
`url`: The origin stream URL (e.g., HLS or DASH).
`adsEndpoint`: The endpoint URL to be used for fetching ad metadata.
Note: Call before calling init adsTracking

* public void setCustomData(String url, String customDataJsonStr)
Description:
Configures custom ad parameters when sending an ad info request to the ad server.
The data must be in JSON object format, where values can be of type string, number, or boolean.
The configuration is applied to the specific request URL.
Parameters:
`url`: The origin manifest URL.
`customDataJsonStr`: JSON-formatted string containing ad parameters.
Note: Call before calling init adsTracking

* public void generateUrl(String url, ResponseInitListener resInitListener)
Description:
Generates a fully processed URL with SSAI tracking and ad metadata embedded.
This is typically called before initializing media playback to ensure all necessary ad tracking information is attached.
The result is returned via a callback listener.
Parameters:
`url`: The origin stream URL.
`resInitListener`: Callback listener that receives the final modified URL or error.

* public void setUseNonce(boolean useNonce)
Description:
Whether to use nonce for google ads manager or not
Parameters:
`useNonce`: boolean options, default false

## 7. Important Notes

Always remember to call `setPlayer()` on the SDK after initializing the ExoPlayer or replacing the current item. This ensures that the SDK correctly recognizes the active video player and can effectively manage ad tracking.

## 8. Conclusion

By following the steps outlined above, you can successfully integrate and utilize the SSAITracking SDK within your application. Ensure that you handle both success and failure callbacks to provide a seamless user experience.

## 9. References

SSAITracking demo link: [Demo](https://github.com/sigmaott/sigma-ssai-exo-sdk)