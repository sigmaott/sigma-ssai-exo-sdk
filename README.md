# Sigma DAI Exo SSAI Plugin

**Organization**:  Thủ Đô Multimedia

## Table of Contents

1. **Introduction**
2. **Requirements**
3. **Installation**
4. **Usage**

## Introduction

This document provides a guide for integrating and using the SSAITracking SDK for Android applications, specifically using Media3. It includes detailed information on installation, SDK initialization, and handling necessary callbacks.

## Requirements

- **Minimum SDK**: 21
- **Player Library**: Media3

## Installation

### Step 1: Add Repository

In your **rootProject/build.gradle** file, add the following repositories:

```groovy
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

### Step 2: Add Dependencies

In your **app/build.gradle** file, include the following dependencies:
Add the Media3 core dependencies (skip if you're already using a different media3 version) using

```groovy
dependencies {
    ...
    implementation 'androidx.media3:media3-exoplayer:1.4.1'
    implementation 'androidx.media3:media3-exoplayer-hls:1.4.1'
    implementation 'androidx.media3:media3-ui:1.4.1'
    ...}
```

Add Sigma SSAI plugin

```groovy
dependencies {
    ...
    implementation 'com.sigma.ssai:sigma-ssai-media3-cspm:1.0.0'
    ...
}
```

## Usage

### Step 1: Start Local Server Tracking

To set up local server tracking when the application starts, add the following initialization call in your main Application class or activity's onCreate method:

```groovy
AdsTracking.getInstance().startServer();
```

### Step 2: Initialize Configuration Before Player Starts

Before starting the player, initialize the ad tracking configuration using the AdsTracking instance. The configuration requires a context, the player view, and both the content URL and ads URL.

```groovy
AdsTracking.getInstance().init(
        context, 
        playerView, 
        source,
        ads,
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

- **context**: current context
- **playerView**:  current player view
- **source**: string URL source for the main content
- **ads**: string URL source for the ads
- **onInitSuccess**: Called when initialization succeeds; returns modified source for ads tracking
- **onInitFailed**: Called when initialization fails; returns original source URL

### Step 3: Prepare and Play the Media Source

Use the modifi source returned by the onInitSuccess(or originalsource returned by the onInitFailed) callback to initialize and play the media. Here's an example using Media3:

```groovy
// Create an Player instance
player = new Player.Builder(this).build();
playerView.setPlayer(player);
MediaItem mediaItem = MediaItem.fromUri(Uri.parse(modifiUrl));
player.setMediaItem(mediaItem);
player.prepare();
player.setPlayWhenReady(true);
```

### Step 4: Clean Up Resources

To prevent memory leaks, call the destroy() method when the activity or player is destroyed. This ensures that the ad tracking resources are cleaned up properly.

```groovy
@Override
protected void onDestroy() {
    AdsTracking.getInstance().destroy();
    super.onDestroy();
}
```
