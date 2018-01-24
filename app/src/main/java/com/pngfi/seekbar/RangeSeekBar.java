package com.pngfi.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
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

public class RangeSeekBar extends View implements Thumb.OnProgressChangeListener {


    private static final String TAG = "RangeSeekBar";


    private static final int DEFAULT_LINE_HEIGHT = 5; //dp


    private int mOrientation;

    //the orientation value
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;


    //the lesser thumb
    private Thumb mLesserThumb;

    //the larger thumb
    private Thumb mLargerThumb;


    //the height of progressLine
    private float mProgressHeight;

    //the height of background line
    private float mProgressBackgroundHeight;


    //the num of step between lesserThumb and largerThumb
    private int mGap;

    @ColorInt
    private int mProgressBackground;
    @ColorInt
    private int mProgressColor;

    private int mScaledTouchSlop;

    //the center of y alias
    private int mCenterY;

    private RectF mProgressLine;

    private Paint mPaint;

    private static final float DEFAULT_RADIUS_RATE = 0.5f;

    private OnSeekBarChangeListener onSeekBarChangeListener;


    private Thumb mSlidingThumb;


    //the thumb will move to the touch location if true
    private boolean mSeekToTouch;


    private float mTouchDownX;
    private float mLastTouchX;

    private boolean mIsDragging;


    public RangeSeekBar(Context context) {
        this(context, null);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar, defStyleAttr, 0);
        mProgressHeight = ta.getDimension(R.styleable.RangeSeekBar_progressHeight, dp2px(DEFAULT_LINE_HEIGHT));
        mProgressBackground = ta.getColor(R.styleable.RangeSeekBar_progressBackground, Color.GRAY);
        mProgressColor = ta.getColor(R.styleable.RangeSeekBar_progressColor, Color.YELLOW);
        mProgressBackgroundHeight = ta.getDimension(R.styleable.RangeSeekBar_progressBackgroundHeight, dp2px(DEFAULT_LINE_HEIGHT));
        Drawable drawable = ta.hasValue(R.styleable.RangeSeekBar_thumb) ? ta.getDrawable(R.styleable.RangeSeekBar_thumb) : getResources().getDrawable(R.drawable.ic_thumb);
        float min = ta.getFloat(R.styleable.RangeSeekBar_min, 0);
        float max = ta.getFloat(R.styleable.RangeSeekBar_max, 100);
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        mLesserThumb = new Thumb(getContext(), drawable, min, max);
        mLargerThumb = new Thumb(getContext(), drawable.mutate().getConstantState().newDrawable(), min, max);
        mLesserThumb.setOnProgressChangeListener(this);
        mLargerThumb.setOnProgressChangeListener(this);

        int shadowRadius = (int) ta.getDimension(R.styleable.RangeSeekBar_shadowRadius, 0);
        int shadowColor = ta.getColor(R.styleable.RangeSeekBar_shadowColor, Color.TRANSPARENT);
        int shadowOffsetX = (int) ta.getDimension(R.styleable.RangeSeekBar_shadowOffsetX, 0);
        int shadowOffsetY = ta.getDimensionPixelOffset(R.styleable.RangeSeekBar_shadowOffsetY, 0);
        mLesserThumb.setShadow(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);
        mLargerThumb.setShadow(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);

        int stepCount = ta.getInt(R.styleable.RangeSeekBar_stepCount, 0);
        mLesserThumb.setStepCount(stepCount);
        mLargerThumb.setStepCount(stepCount);
        mSeekToTouch = ta.getBoolean(R.styleable.RangeSeekBar_seekToTouch, false);
        mGap = ta.getInt(R.styleable.RangeSeekBar_gap, 0);
        ta.recycle();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

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
        mProgressLine = new RectF(mLesserThumb.getWidth() / 2, mCenterY - mProgressBackgroundHeight / 2, getWidth() - mLesserThumb.getWidth() / 2, mCenterY + mProgressBackgroundHeight / 2);

        mLesserThumb.setRect((int) mProgressLine.left - mLesserThumb.getThumbDrawable().getIntrinsicWidth() / 2, mCenterY - mLesserThumb.getThumbDrawable().getIntrinsicHeight() / 2, (int) mProgressLine.right - (int) mProgressLine.left - mLesserThumb.getThumbDrawable().getIntrinsicWidth());
        mLargerThumb.setRect((int) mProgressLine.left + mLesserThumb.getThumbDrawable().getIntrinsicHeight() / 2, mCenterY - mLargerThumb.getThumbDrawable().getIntrinsicHeight() / 2, (int) mProgressLine.right - (int) mProgressLine.left - mLesserThumb.getThumbDrawable().getIntrinsicWidth());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mProgressBackground);
        // draw progressBackground
        canvas.drawRoundRect(mProgressLine, DEFAULT_RADIUS_RATE * mProgressHeight, DEFAULT_RADIUS_RATE * mProgressHeight, mPaint);

        //draw progress
        mPaint.setColor(mProgressColor);
        canvas.drawRect(mLesserThumb.getCenterPoint().x, mCenterY - mProgressHeight / 2, mLargerThumb.getCenterPoint().x, mCenterY + mProgressHeight / 2, mPaint);

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
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getX();
                mLastTouchX = event.getX();
                if (mLesserThumb.contains(event.getX(), event.getY())) {
                    mIsDragging = true;
                    mSlidingThumb = mLesserThumb;
                } else if (mLargerThumb.contains(event.getX(), event.getY())) {
                    mIsDragging = true;
                    mSlidingThumb = mLargerThumb;
                } else {
                    mIsDragging = false;
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    float dx = event.getX() - mLastTouchX;
                    mLastTouchX = event.getX();
                    if (mSlidingThumb == mLesserThumb) {
                        int lessStep = mLesserThumb.calculateStep(event.getX(), 0f);
                        if (lessStep > mLargerThumb.getCurrentStep() - mGap && dx > 0) {
                            mSlidingThumb = mLargerThumb;
                        } else {
                            if (lessStep <= mLargerThumb.getCurrentStep() - mGap) {
                                mSlidingThumb.setCurrentStep(lessStep, true);
                            }
                        }
                    } else {
                        int largerStep = mLargerThumb.calculateStep(event.getX(), 0f);
                        if (largerStep < mLesserThumb.getCurrentStep() + mGap && dx < 0) {
                            mSlidingThumb = mLesserThumb;
                        } else {
                            if (largerStep >= mLesserThumb.getCurrentStep() + mGap)
                                mSlidingThumb.setCurrentStep(largerStep, true);
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - mTouchDownX) < mScaledTouchSlop && !mIsDragging && mSeekToTouch) {
                    seekToTouch(event.getX());
                }
                mIsDragging = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


    private void seekToTouch(float touchUpX) {
        float lesserDistance = Math.abs(touchUpX - mLesserThumb.getCenterPoint().x);
        float largerDistance = Math.abs((touchUpX - mLargerThumb.getCenterPoint().x));
        if (lesserDistance < largerDistance) {
            int lessStep = mLesserThumb.calculateStep(touchUpX, 0f);
            lessStep = Math.min(lessStep, mLargerThumb.getCurrentStep() - mGap);
            mLesserThumb.setCurrentStep(lessStep, true);
        } else {
            int largerStep = mLargerThumb.calculateStep(touchUpX, 0f);
            largerStep = Math.max(largerStep, mLesserThumb.getCurrentStep() + mGap);
            mLargerThumb.setCurrentStep(largerStep, true);
        }
        invalidate();
    }


    private float dp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    @Override
    public void onProgressChanged(Thumb thumb, float progress, boolean fromUser) {
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(this, mLesserThumb.getProgress(), mLargerThumb.getProgress(), fromUser);
        }
    }


    public interface OnSeekBarChangeListener {
        void onProgressChanged(RangeSeekBar seekBar, float lesserProgress, float largerProgress, boolean fromUser);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        RangeSeekBarState state = new RangeSeekBarState(super.onSaveInstanceState());
        state.lesserStep = mLesserThumb.getCurrentStep();
        state.largerStep = mLargerThumb.getCurrentStep();
        return state;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        RangeSeekBarState rs = (RangeSeekBarState) state;
        super.onRestoreInstanceState(((RangeSeekBarState) state).getSuperState());
        mLesserThumb.setCurrentStep(rs.lesserStep, false);
        mLargerThumb.setCurrentStep(rs.largerStep, false);
    }


    private class RangeSeekBarState extends BaseSavedState {
        private int lesserStep;
        private int largerStep;

        public RangeSeekBarState(Parcelable superState) {
            super(superState);
        }

        public RangeSeekBarState(Parcel source) {
            super(source);
            lesserStep = source.readInt();
            largerStep = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(lesserStep);
            out.writeInt(largerStep);
        }


    }

}
