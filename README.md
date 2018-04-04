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
     compile 'com.pngfi:rangeseekbar:${LATEST_VERSION}'
  }
```
## Usage
```

 <com.pngfi.rangeseekbar.RangeSeekBar
                    android:id="@+id/seekBar"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:rb_gap="1"
                    app:rb_max="20000"
                    app:rb_min="0"
                    app:rb_thumb="@drawable/ic_thumb"
                    app:rb_progressBackground="#E9E9E9"
                    app:rb_progressBackgroundHeight="6dp"
                    app:rb_progressColor="@color/color_accent"
                    app:rb_progressHeight="6dp"
                    app:rb_seekToTouch="true"
                    app:rb_shadowColor="@color/default_shadow_color"
                    app:rb_shadowOffsetX="0dp"
                    app:rb_shadowOffsetY="2dp"
                    app:rb_shadowRadius="5dp"
                    app:rb_stepCount="200" />

```

listening the progress changes
```
 seekBar.setOnSeekBarChangeListener(new RangeSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser) {
                tv.setText((int) lesserProgress+" to "+(int)largerProgress);
            }
        });
```

change the progress
```
 seekbar.setProgress(lesser,larger);
```
get the current progress
```
 float[] progress=seekbar.getProgress();
 float lesserProgress=progress[0];
 float largerProgress=progress[1];
 
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

