[![API](https://img.shields.io/badge/API-11%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![License](http://img.shields.io/badge/License-Apache%202.0-brightgreen.svg?style=flat)](https://opensource.org/licenses/Apache-2.0)

**A flexible range seek bar for Android api 11+,  its thumb supports shadow and multiple drawable.
`star` or `pull request` will be welcomed**
****

##Usage
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

##Attr
 attr | format | desc
  -------- | ---|---
  rb_max|float|min, max, progress
  rb_min|float|min, max, progress
  rb_progressBackground|color|all attrs
  rb_progressBackgroundHeight|dimension|all attrs
  rb_progressColor|xml|min, max, progress
  rb_progressHeight||
  rb_thumb|xml, java|all attrs
  rb_stepCount|xml|min, max, progress
  rb_gap|xml, java|all attrs
  rb_seekToTouch|xml|min, max, progress
  rb_shadowRadius|xml, java|all attrs
  rb_shadowOffsetX|xml|min, max, progress
  rb_shadowOffsetY|xml, java|all attrs
  rb_shadowColor|xml|min, max, progress
