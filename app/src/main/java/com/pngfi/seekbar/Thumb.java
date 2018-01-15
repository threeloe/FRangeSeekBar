package com.pngfi.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public class Thumb {

    //the x of drawable left edge when the progress is min
    private int left;
    //the x of drawable left edge when the progress is max
    private int right;

    private int top;
    private int bottom;

    private float min;
    private float max;

    private Drawable thumbDrawable;
    private Float progress;

    /**
     * the real slide width of thumb
     * progressWidth=getWidth()-thumb.getIntrinsicWidth()
     */
    private int progressWidth;


    private OnProgressChangeListener onProgressChangeListener;


    private Context context;


    public Thumb(Context context, Drawable drawable, float min, float max) {
        this.thumbDrawable = drawable;
        this.min = min;
        this.max = max;
        progress = min;
        this.context = context;
    }


    public void setRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        progressWidth = right - left;

    }


    public void setProgress(float progress) {
        if (progress < min || progress > max) {
            throw new IllegalArgumentException("progress must be between min and max ");
        }
        if (!this.progress.equals(progress)) {
            this.progress = progress;
            if (onProgressChangeListener != null)
                onProgressChangeListener.onProgressChanged(this, progress, false);
        }
    }


    public boolean contains(float x, float y) {
        //增大一点触摸范围
        final float extra = context.getResources().getDisplayMetrics().density * 5;
        return x >= thumbDrawable.getBounds().left - extra && x <= thumbDrawable.getBounds().right + extra && y >= top - extra && y <= top + thumbDrawable.getIntrinsicHeight() + extra;
    }


    public void onSlide(Float dx, Float dy) {
        if (dy.equals(0f)) {
            progress = progress + dx / progressWidth * (max - min);
            if (progress > max) {
                progress = max;
            } else if (progress < min) {
                progress = min;
            }
            if (onProgressChangeListener != null)
                onProgressChangeListener.onProgressChanged(this, progress, true);
        } else {

        }
    }


    public float getProgress() {
        return progress;
    }


    public int getHeight() {
        return thumbDrawable.getIntrinsicHeight();
    }


    public int getWidth() {
        return thumbDrawable.getIntrinsicWidth();
    }


    public PointF getCenterPoint() {
        PointF point = new PointF();
        point.x = thumbDrawable.getBounds().left + thumbDrawable.getIntrinsicWidth() / 2;
        point.y = thumbDrawable.getBounds().top + thumbDrawable.getIntrinsicHeight() / 2;
        return point;
    }


    public void draw(Canvas canvas) {
        float percent = (progress - min) / (max - min);
        thumbDrawable.setBounds((int) (left + percent * progressWidth), top, (int) (left + percent * progressWidth + thumbDrawable.getIntrinsicWidth()), top + getHeight());
        thumbDrawable.draw(canvas);
    }


    interface OnProgressChangeListener {
        void onProgressChanged(Thumb thumb, float progress, boolean fromUser);
    }


    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        onProgressChangeListener = listener;
    }

}