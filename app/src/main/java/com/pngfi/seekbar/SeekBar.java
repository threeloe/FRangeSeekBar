package com.pngfi.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pngfi on 2018/1/6.
 */

public class SeekBar extends View {


    //the max value of SeekBar
    private int max;
    //the min value of SeekBar
    private int min;
    //the current value of lesser thumb
    private int minProgress;
    //the current value of larger thumb
    private int maxProgress;


    //the resId of thumb
    private int thumb;



    private int lineHeight;
    private int lineWidth;


    


    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
