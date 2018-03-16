[![API](https://img.shields.io/badge/API-11%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![License](http://img.shields.io/badge/License-Apache%202.0-brightgreen.svg?style=flat)](https://opensource.org/licenses/Apache-2.0)

**A flexible range seek bar for Android api 11+,  its thumb supports shadow and multiple drawable.
`star` or `pull request` will be welcomed**
****

## Feature
- smooth sliding experience that can switch from left thumb to right thumb.
- provide a customized shadow effect.
- can seek to the location where user click.

## Screen shot
![demo.gif](https://github.com/pngfi/RangeSeekBar/blob/master/gif/demo.gif)

## Download
The **LATEST_VERSION**: [![Download](https://api.bintray.com/packages/pngfi/maven/rangeseekbar/images/download.svg)](https://bintray.com/pngfi/maven/rangeseekbar/_latestVersion)
```groovy
  dependencies {
     compile 'com.pngfi.rangeseekbar:${LATEST_VERSION}'
  }
```
## Usage
```
   <com.pngfi.rangeseekbar.RangeSeekBar
        android:id="@+id/seek"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
        app:gap="0"
        app:max="100"
        app:min="0"
        app:progressBackground="#E9E9E9"
        app:progressBackgroundHeight="1dp"
        app:progressColor="@color/colorAccent"
        app:progressHeight="5dp"
        app:seekToTouch="true"
        app:shadowColor="#48FF4081"
        app:shadowOffsetX="0dp"
        app:shadowOffsetY="2dp"
        app:shadowRadius="5dp"
        app:stepCount="20" />

```

## Attr
 attr | format | desc
  -------- | ---|---
  rb_max|float|the max value of progress,default 100
  rb_min|float|the min value of progress,default 0
  rb_progressBackground|color|the background color of progress bar
  rb_progressBackgroundHeight|dimension|background height
  rb_progressColor|color|the progress color
  rb_progressHeight|dimension|the height of progress bar
  rb_thumb|drawable|the drawable resource of SeekBar button, support multiple drawable. note:the StateListDrawable only support pressed state.
  rb_stepCount|positive integer|the progress is divided into many `step`s, this is the count of steps, had better assign it.
  rb_gap|positive integer|the minimal step count between two thumbs,default 0.
  rb_seekToTouch|boolean|the thumb will seek to the location where user click if true,default true.
  rb_shadowColor|color|shadow color around thumb
  rb_shadowRadius|dimension|shadow radius
  rb_shadowOffsetX|dimension|horizontal offset of shadow
  rb_shadowOffsetY|dimension|vertical offset  of shadow

