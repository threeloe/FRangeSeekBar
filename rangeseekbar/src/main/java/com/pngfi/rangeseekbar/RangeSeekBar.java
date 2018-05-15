package com.pngfi.rangeseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
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


    private int mProgressBackground;

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

    private boolean mIsDragging;


    public RangeSeekBar(Context context) {
        this(context, null);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar, defStyleAttr, 0);
        mProgressHeight = ta.getDimension(R.styleable.RangeSeekBar_rb_progressHeight, dp2px(DEFAULT_LINE_HEIGHT));
        mProgressBackground = ta.getColor(R.styleable.RangeSeekBar_rb_progressBackground, Color.GRAY);
        mProgressColor = ta.getColor(R.styleable.RangeSeekBar_rb_progressColor, Color.YELLOW);
        mProgressBackgroundHeight = ta.getDimension(R.styleable.RangeSeekBar_rb_progressBackgroundHeight, dp2px(DEFAULT_LINE_HEIGHT));
        Drawable drawable = ta.hasValue(R.styleable.RangeSeekBar_rb_thumb) ? ta.getDrawable(R.styleable.RangeSeekBar_rb_thumb) : getResources().getDrawable(R.drawable.default_thumb);
        float min = ta.getFloat(R.styleable.RangeSeekBar_rb_min, 0);
        float max = ta.getFloat(R.styleable.RangeSeekBar_rb_max, 100);
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        mLesserThumb = new Thumb(getContext(), drawable, min, max);
        mLargerThumb = new Thumb(getContext(), drawable.mutate().getConstantState().newDrawable(), min, max);
        mLesserThumb.setOnProgressChangeListener(this);
        mLargerThumb.setOnProgressChangeListener(this);

        int shadowRadius = (int) ta.getDimension(R.styleable.RangeSeekBar_rb_shadowRadius, 0);
        int shadowColor = ta.getColor(R.styleable.RangeSeekBar_rb_shadowColor, Color.TRANSPARENT);
        int shadowOffsetX = (int) ta.getDimension(R.styleable.RangeSeekBar_rb_shadowOffsetX, 0);
        int shadowOffsetY = ta.getDimensionPixelOffset(R.styleable.RangeSeekBar_rb_shadowOffsetY, 0);
        mLesserThumb.setShadow(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);
        mLargerThumb.setShadow(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);

        int stepCount = ta.getInt(R.styleable.RangeSeekBar_rb_stepCount, 0);
        mLesserThumb.setStepCount(stepCount);
        mLargerThumb.setStepCount(stepCount);
        mSeekToTouch = ta.getBoolean(R.styleable.RangeSeekBar_rb_seekToTouch, true);
        mGap = ta.getInt(R.styleable.RangeSeekBar_rb_gap, 0);
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
        mCenterY = (getPaddingTop() + (getHeight() - getPaddingBottom())) / 2;
        mProgressLine = new RectF(mLesserThumb.getWidth() / 2 - getPaddingLeft(), mCenterY - mProgressBackgroundHeight / 2, getWidth() - mLesserThumb.getWidth() / 2 - getPaddingRight(), mCenterY + mProgressBackgroundHeight / 2);

        mLesserThumb.setRect((int) mProgressLine.left, mCenterY - mLesserThumb.getThumbDrawable().getIntrinsicHeight() / 2, (int) mProgressLine.right - (int) mProgressLine.left - mLesserThumb.getThumbDrawable().getIntrinsicWidth());
        mLargerThumb.setRect((int) mProgressLine.left + mLesserThumb.getThumbDrawable().getIntrinsicHeight(), mCenterY - mLargerThumb.getThumbDrawable().getIntrinsicHeight() / 2, (int) mProgressLine.right - (int) mProgressLine.left - mLesserThumb.getThumbDrawable().getIntrinsicWidth());
        //用户没有设置过进度，初始化
        if (mLargerThumb.getCurrentStep() < mLesserThumb.getCurrentStep() + mGap) {
            mLargerThumb.setCurrentStep(mLesserThumb.getCurrentStep() + mGap, false, false);
        }
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
     * set progress of the lesser thumb and larger thumb,the progress value will be replaced by
     * the proximate step value.
     *
     * @param lesserProgress the progress of lessThumb
     * @param largerProgress the progress of largerThumb
     */
    public void setProgress(float lesserProgress, float largerProgress) {
        if (lesserProgress > largerProgress) {
            throw new IllegalArgumentException("lesserProgress must be less than largerProgress");
        }
        mLesserThumb.setProgress(lesserProgress);
        mLargerThumb.setProgress(largerProgress);
        postInvalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //prevent sliding conflict
                getParent().requestDisallowInterceptTouchEvent(true);
                mTouchDownX = event.getX();
                if (mLesserThumb.contains(event.getX(), event.getY())) {
                    mIsDragging = true;
                    mSlidingThumb = mLesserThumb;
                } else if (mLargerThumb.contains(event.getX(), event.getY())) {
                    mIsDragging = true;
                    mSlidingThumb = mLargerThumb;
                } else {
                    mIsDragging = false;
                }
                if (mIsDragging) {
                    mSlidingThumb.onPressed(true);
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    if (mSlidingThumb == mLesserThumb) {
                        int lessStep = mLesserThumb.calculateStep(event.getX());
                        if (lessStep > mLargerThumb.getCurrentStep() - mGap) {
                            mSlidingThumb = mLargerThumb;
                            //prevent sliding too fast
                            mLesserThumb.setCurrentStep(mLargerThumb.getCurrentStep() - mGap, true, false);
                        } else {
                            mLesserThumb.onPressed(true);
                            mLargerThumb.onPressed(false);
                            mSlidingThumb.setCurrentStep(lessStep, true, false);
                        }
                    } else if (mSlidingThumb == mLargerThumb) {
                        int largerStep = mLargerThumb.calculateStep(event.getX());
                        if (largerStep < mLesserThumb.getCurrentStep() + mGap) {
                            mSlidingThumb = mLesserThumb;
                            mLargerThumb.setCurrentStep(mLesserThumb.getCurrentStep() + mGap, true, false);
                        } else {
                            mLargerThumb.onPressed(true);
                            mLesserThumb.onPressed(false);
                            mSlidingThumb.setCurrentStep(largerStep, true, false);
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - mTouchDownX) < mScaledTouchSlop && !mIsDragging && mSeekToTouch) {
                    seekToTouch(event.getX());
                }
                if (mIsDragging) {
                    mSlidingThumb.onPressed(false);
                    invalidate();
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
            int lessStep = mLesserThumb.calculateStep(touchUpX);
            lessStep = Math.min(lessStep, mLargerThumb.getCurrentStep() - mGap);
            mLesserThumb.setCurrentStep(lessStep, true, true);
        } else {
            int largerStep = mLargerThumb.calculateStep(touchUpX);
            largerStep = Math.max(largerStep, mLesserThumb.getCurrentStep() + mGap);
            mLargerThumb.setCurrentStep(largerStep, true, true);
        }
        invalidate();
    }


    /**
     * @return current progress
     * the lesserProgress in index 0
     * the largerProgress in index 1
     */
    public float[] getProgress() {
        float[] progreses = new float[2];
        progreses[0] = mLesserThumb.getProgress();
        progreses[1] = mLargerThumb.getProgress();
        return progreses;
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
        mLesserThumb.setCurrentStep(rs.lesserStep, false, false);
        mLargerThumb.setCurrentStep(rs.largerStep, false, false);
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
