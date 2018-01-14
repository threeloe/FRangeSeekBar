package com.pngfi.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by pngfi on 2018/1/6.
 */

public class SeekBar extends View {


    private static final int DEFAULT_LINE_HEIGHT = 5; //dp


    private int mOrientation;

    //the orientation value
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;


    //the mMax value of SeekBar
    private float mMax;
    //the min value of SeekBar
    private float mMin;


    //the current value of lesser thumb
    private float mLesserProgress;
    //the current value of larger thumb
    private float mLargerProgress;


    //the lesser thumb
    private Thumb mLesserThumb;

    //the larger thumb
    private Thumb mLargerThumb;


    private int mStepCount;

    //the height of line
    private float mLineHeight;
    //the width of line
    private float mLineWidth;


    @ColorInt
    private int mLineColor;
    @ColorInt
    private int mLineColorSelected;

    private int mScaledTouchSlop;

    //the center of y alias
    private int mCenterY;

    private RectF mLine;

    private Paint mPaint;

    private static final float DEFAULT_RADIUS_RATE = 0.5f;

    private OnSeekBarChangeListener onSeekBarChangeListener;


    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyleAttr, 0);
        mLineHeight = ta.getDimension(R.styleable.SeekBar_lineHeight, dp2px(DEFAULT_LINE_HEIGHT));
        mLineColor = ta.getColor(R.styleable.SeekBar_lineColor, Color.GRAY);
        mLineColorSelected = ta.getColor(R.styleable.SeekBar_lineColorSelected, Color.parseColor("#FF4081"));
        Drawable drawable = ta.hasValue(R.styleable.SeekBar_thumb) ? ta.getDrawable(R.styleable.SeekBar_thumb) : getResources().getDrawable(R.drawable.ic_thumb);

        mMin = ta.getFloat(R.styleable.SeekBar_min, 0);
        mMax = ta.getFloat(R.styleable.SeekBar_max, 100);
        if (mMax <= mMin) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        mLesserThumb = new Thumb(drawable, mMin, mMax);
        mLargerThumb = new Thumb(drawable.mutate().getConstantState().newDrawable(), mMin, mMax);

        ta.recycle();

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth = mLesserThumb.getWidth();
        int thumbHeight = mLesserThumb.getHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = thumbWidth * 2;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = thumbHeight;
        }
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mLineWidth = getWidth() - getPaddingLeft() - getPaddingRight() - mLesserThumb.getWidth();
        mLine = new RectF(mLesserThumb.getWidth() / 2, mCenterY - mLineHeight / 2, getWidth() - mLesserThumb.getWidth() / 2, mCenterY + mLineHeight / 2);

        mCenterY = getHeight() / 2;
        mLesserThumb.setRect(0, mCenterY - mLesserThumb.getHeight() / 2,);
        mLargerThumb.setRect(mLesserThumb.getWidth(), mCenterY - mLargerThumb.getHeight() / 2);




    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mLineColor);


        // draw line
        canvas.drawRoundRect(mLine, DEFAULT_RADIUS_RATE * mLineHeight, DEFAULT_RADIUS_RATE * mLineHeight, mPaint);

        // draw thumb
        mLesserThumb.draw(canvas);
        mLargerThumb.draw(canvas);

    }


    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        onSeekBarChangeListener = listener;
    }


    /**
     * called after
     *
     * @param lesserProgress
     * @param largerProgress
     */
    public void setProgress(float lesserProgress, float largerProgress) {
        mLargerProgress = largerProgress;
        mLesserProgress = lesserProgress;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(this, lesserProgress, largerProgress, false);
        }
        invalidate();
    }


    private float currentPercent(float progress) {
        return (progress - mMin) / (mMax - mMin);
    }


    private Thumb mSlidingThumb;

    private float mDownX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                boolean result = true;
                if (mLesserThumb.contains(event.getX(), event.getY())) {
                    mSlidingThumb = mLesserThumb;
                } else if (mLargerThumb.contains(event.getX(), event.getY())) {
                    mSlidingThumb = mLargerThumb;
                } else {
                    result = false;
                }
                return result;
            case MotionEvent.ACTION_MOVE:
                float lessProgress = mLesserThumb.getProgress();
                float largerProgress = mLargerThumb.getProgress();
                float dx = event.getX() - mDownX;
                if (mSlidingThumb == mLesserThumb) {
                    if (lessProgress > largerProgress){
                        mSlidingThumb=mLargerThumb;
                    }else {
                        mLesserThumb.onSlide(event.getX(),0);
                    }
                } else {
                    if (largerProgress<lessProgress){
                        mSlidingThumb=mLesserThumb;
                    }else {
                        mLargerThumb.onSlide(event.getX(),0);
                    }
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }


    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser);
    }


    private float dp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }


    private static final String SUPER_STATE = "super_state";

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcel = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, parcel);
        return bundle;

    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superState = bundle.getParcelable(SUPER_STATE);

        super.onRestoreInstanceState(superState);

    }


}
