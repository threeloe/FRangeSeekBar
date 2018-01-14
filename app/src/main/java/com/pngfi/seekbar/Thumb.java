package com.pngfi.seekbar;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Thumb {

    //the x  when the progress is min
    private int left;
    //the x when the progress is max
    private int right;

    private int top;
    private int bottom;

    private float min;
    private float max;

    private Drawable thumbDrawable;
    private float progress;

    /**
     * the real slide width of thumb
     * progressWidth=getWidth()-thumb.getIntrinsicWidth()
     */
    private int progressWidth;


    public Thumb(Drawable drawable, float min, float max) {
        this.thumbDrawable = drawable;
        this.min = min;
        this.max = max;
    }


    public void setRect(int left, int top,int right,int bottom) {
        this.left = left;
        this.top = top;
        this.right=right;
        this.bottom=bottom;
        setProgress(progress);
    }

    public void setProgressWidth(int progressWidth){
        this.progressWidth=progressWidth;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        int currentLeft = (int) (left + (progress - min) / (max - min) * progressWidth);
        thumbDrawable.setBounds(currentLeft, top, currentLeft + getWidth(), top + getHeight());
    }


    public boolean contains(float x, float y) {
        //增大一点触摸范围
        final int extra = 0;
        return x >= thumbDrawable.getBounds().left - extra && x <= thumbDrawable.getBounds().right + extra && y >= top - extra && y <= top + thumbDrawable.getIntrinsicHeight() + extra;
    }


    public void onSlide(float eventX, float eventY) {
        int x= (int) eventX;
        int y= (int) eventY;
        if (y == 0) {
            progress = min + currentPercent(x) * (max - min);
            thumbDrawable.setBounds(x - getWidth() / 2, top, x + getWidth() / 2, top + getHeight());
        } else {

        }
    }


    public float getProgress(){
        return progress;
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