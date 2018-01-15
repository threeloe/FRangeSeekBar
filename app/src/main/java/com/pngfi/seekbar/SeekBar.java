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

public class SeekBar extends View implements Thumb.OnProgressChangeListener {


    private static final int DEFAULT_LINE_HEIGHT = 5; //dp


    private int mOrientation;

    //the orientation value
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;


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
    private int mProgressBackground;
    @ColorInt
    private int mProgressColor;

    private int mScaledTouchSlop;

    //the center of y alias
    private int mCenterY;

    private RectF mLine;

    private Paint mPaint;

    private static final float DEFAULT_RADIUS_RATE = 0.5f;

    private OnSeekBarChangeListener onSeekBarChangeListener;


    private Thumb mSlidingThumb;

    private float mLastX;


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
        mProgressBackground = ta.getColor(R.styleable.SeekBar_progressBackground, Color.GRAY);
        mProgressColor = ta.getColor(R.styleable.SeekBar_progressColor, Color.parseColor("#FF4081"));
        Drawable drawable = ta.hasValue(R.styleable.SeekBar_thumb) ? ta.getDrawable(R.styleable.SeekBar_thumb) : getResources().getDrawable(R.drawable.ic_thumb);

        float min = ta.getFloat(R.styleable.SeekBar_min, 0);
        float max = ta.getFloat(R.styleable.SeekBar_max, 100);
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        mLesserThumb = new Thumb(getContext(), drawable, min, max);
        mLargerThumb = new Thumb(getContext(), drawable.mutate().getConstantState().newDrawable(), min, max);
        mLesserThumb.setOnProgressChangeListener(this);
        mLargerThumb.setOnProgressChangeListener(this);
        ta.recycle();

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initPaint();
    }


    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
        mCenterY = getHeight() / 2;
        mLineWidth = getWidth() - getPaddingLeft() - getPaddingRight() - mLesserThumb.getWidth();
        mLine = new RectF(mLesserThumb.getWidth() / 2, mCenterY - mLineHeight / 2, getWidth() - mLesserThumb.getWidth() / 2, mCenterY + mLineHeight / 2);
        mLesserThumb.setRect((int) mLine.left - mLesserThumb.getWidth() / 2, mCenterY - mLesserThumb.getHeight() / 2, (int) mLine.right - mLesserThumb.getWidth() - mLargerThumb.getWidth() / 2, mCenterY + mLesserThumb.getHeight() / 2);
        mLargerThumb.setRect((int) mLine.left + mLesserThumb.getWidth() / 2, mCenterY - mLargerThumb.getHeight() / 2, (int) mLine.right - mLargerThumb.getWidth() / 2, mCenterY + mLargerThumb.getHeight() / 2);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mProgressBackground);
        // draw line
        canvas.drawRoundRect(mLine, DEFAULT_RADIUS_RATE * mLineHeight, DEFAULT_RADIUS_RATE * mLineHeight, mPaint);

        //draw progress
        mPaint.setColor(mProgressColor);
        canvas.drawRect(mLesserThumb.getCenterPoint().x, mLine.top, mLargerThumb.getCenterPoint().x, mLine.bottom, mPaint);

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
        if (lesserProgress > largerProgress) {
            throw new IllegalArgumentException("lesserProgress must be less than largerProgress");
        }
        mLesserThumb.setProgress(lesserProgress);
        mLargerThumb.setProgress(largerProgress);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                boolean result = true;
                mLastX = event.getX();
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
                float dx = event.getX() - mLastX;
                mLastX = event.getX();
                if (mSlidingThumb == mLesserThumb) {
                    if (lessProgress >= largerProgress && dx > 0) {
                        mSlidingThumb = mLargerThumb;
                    } else {
                        mSlidingThumb.onSlide(dx, 0f);
                    }
                } else {
                    if (largerProgress <= lessProgress && dx < 0) {
                        mSlidingThumb = mLesserThumb;
                    } else {
                        mSlidingThumb.onSlide(dx, 0f);
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


    @Override
    public void onProgressChanged(Thumb thumb, float progress, boolean fromUser) {
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(this, mLesserThumb.getProgress(), mLargerThumb.getProgress(), fromUser);
        }
    }


}
