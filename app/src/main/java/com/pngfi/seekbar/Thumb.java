package com.pngfi.seekbar;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Thumb {

    //the left  when the progress is min
    private int left;
    private int top;

    private float min;
    private float max;

    private Drawable thumbDrawable;
    private float progress;

    /**
     * the real slide width of thumb
     * progressWidth=getWidth()-thumb.getIntrinsicWidth()
     */
    private int progressWidth;


    public Thumb(Drawable drawable,float min,float max) {
        this.thumbDrawable = drawable;
        this.min=min;
        this.max=max;
    }


    public void setRect(int left, int top) {
        this.left = left;
        this.top = top;
        setProgress(progress);
    }


    public void setProgress(float progress) {
        this.progress = progress;
        int currentLeft = (int) (left + (progress - min) / (max - min) * progressWidth);
        thumbDrawable.setBounds(currentLeft, top, currentLeft + getWidth(), top + getHeight());
    }


    public void onTouch(int x, int y) {
        if (y == 0) {
            progress = min + currentPercent(x) * (max - min);
            thumbDrawable.setBounds(x - getWidth() / 2, top, x + getWidth() / 2, top + getHeight());
        } else {

        }
    }


    public int getHeight() {
        return thumbDrawable.getIntrinsicHeight();
    }


    public int getWidth() {
        return thumbDrawable.getIntrinsicWidth();
    }


    private float currentPercent(float event) {
        return (event - left) / progressWidth;
    }


    public void draw(Canvas canvas) {
        thumbDrawable.draw(canvas);
    }
}