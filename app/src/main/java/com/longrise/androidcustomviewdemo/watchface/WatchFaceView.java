package com.longrise.androidcustomviewdemo.watchface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.longrise.androidcustomviewdemo.R;
import com.longrise.androidcustomviewdemo.utils.SizeUtils;

import java.util.Calendar;
import java.util.TimeZone;

public class WatchFaceView extends View {
    private int mSecondColor;
    private int mMinuteColor;
    private int mHourColor;
    private int mScaleColor;
    private int mWatchFaceBgId;
    private Bitmap mBgImage = null;
    private Calendar mCalendar;

    private Paint mSecondPaint; // 秒针
    private Paint mMinutePaint; // 分针
    private Paint mHourPaint; // 时针
    private Paint mScalePaint; // 刻度

    private int mWidth;
    private int mHeight;
    private Rect mSrcRect;
    private Rect mDesRect;
    private boolean mIsUpdate = false;
    private final int CENTER_RADIUS = SizeUtils.dp2px(5);

    public WatchFaceView(Context context) {
        this(context, null);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取相关属性
        initAttrs(context, attrs);
        // 获取日历实例
        mCalendar = Calendar.getInstance();
        // 设置时区
        mCalendar.setTimeZone(TimeZone.getDefault());
        // 创建画笔
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WatchFaceView);
        mSecondColor = a.getColor(R.styleable.WatchFaceView_secondColor, getResources().getColor(R.color.secondDefaultColor));
        mMinuteColor = a.getColor(R.styleable.WatchFaceView_minuteColor, getResources().getColor(R.color.minuteDefaultColor));
        mHourColor = a.getColor(R.styleable.WatchFaceView_hourColor, getResources().getColor(R.color.hourDefaultColor));
        mScaleColor = a.getColor(R.styleable.WatchFaceView_scaleColor, getResources().getColor(R.color.scaleDefaultColor));
        mWatchFaceBgId = a.getResourceId(R.styleable.WatchFaceView_watchFaceBg, -1);
        if (mWatchFaceBgId != -1) {
            mBgImage = BitmapFactory.decodeResource(getResources(), mWatchFaceBgId);
        }
        a.recycle();
    }

    private void initPaint() {
        // 秒针
        mSecondPaint = new Paint();
        mSecondPaint.setColor(mSecondColor); // 设置画笔颜色
        mSecondPaint.setStyle(Paint.Style.STROKE); // 画直线
        mSecondPaint.setStrokeWidth(2f); // 设置画笔宽度
        mSecondPaint.setAntiAlias(true); // 设置抗锯齿
        // 分针
        mMinutePaint = new Paint();
        mMinutePaint.setColor(mMinuteColor);
        mMinutePaint.setStyle(Paint.Style.STROKE);
        mMinutePaint.setStrokeWidth(4f);
        mMinutePaint.setAntiAlias(true);
        // 时针
        mHourPaint = new Paint();
        mHourPaint.setColor(mHourColor);
        mHourPaint.setStyle(Paint.Style.STROKE);
        mHourPaint.setStrokeWidth(6f);
        mHourPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourPaint.setAntiAlias(true);
        // 刻度
        mScalePaint = new Paint();
        mScalePaint.setColor(mScaleColor);
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setStrokeWidth(5f);
        mScalePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量自己(没有子view,只能测量自己)
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 减去外边距
        int widthTargetSize = widthSize - getPaddingLeft() - getPaddingRight();
        int heightTargetSize = heightSize - getPaddingTop() - getPaddingBottom();
        // 判断大小，取小的值
        int targetSize = Math.min(widthTargetSize, heightTargetSize);
        setMeasuredDimension(targetSize, targetSize);
        // 初始化Rect
        initRect();
    }

    private void initRect() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mBgImage == null) {
            return;
        }
        // 源坑-->从图片中截取，如果跟图片大小一样，那么就截取图片所有内容
        mSrcRect = new Rect();
        mSrcRect.top = 0;
        mSrcRect.left = 0;
        mSrcRect.right = mBgImage.getWidth();
        mSrcRect.bottom = mBgImage.getHeight();
        // 目标坑-->要填放源内容的地方
        mDesRect = new Rect();
        mDesRect.left = 0;
        mDesRect.top = 0;
        mDesRect.right = mWidth;
        mDesRect.bottom = mHeight;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsUpdate = true;
        post(new Runnable() {
            @Override
            public void run() {
                if (mIsUpdate) {
                    invalidate();
                    postDelayed(this, 1000);
                } else {
                    removeCallbacks(this);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsUpdate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long currentMillis = System.currentTimeMillis();
        mCalendar.setTimeInMillis(currentMillis);

        // 绘制背景
        drawBackground(canvas);

        // 半径
        int radius = (int) (mWidth / 2f);
        // 内环半径
        int innerR = (int) (mWidth / 2 * 0.8f);
        // 外环半径
        int outerR = (int) (mWidth / 2 * 0.9f);

        // 绘制刻度(方法一)
//        drawScale01(canvas, radius, innerR, outerR);
        // 绘制刻度(方法二)
        drawScale02(canvas, radius, innerR, outerR);

        int hourValue = mCalendar.get(Calendar.HOUR);
        int minuteValue = mCalendar.get(Calendar.MINUTE);
        int secondValue = mCalendar.get(Calendar.SECOND);
        // 绘制时针
        drawHourLine(canvas, radius, hourValue, minuteValue);
        // 绘制分针
        drawMinuteLine(canvas, radius, minuteValue);
        // 绘制秒针
        drawSecondLine(canvas, radius, secondValue);
    }

    private void drawHourLine(Canvas canvas, int radius, int hourValue, int minuteValue) {
        int hourRadius = (int) (radius * 0.5f); // 时针长度
        float hourOffsetRotate = minuteValue / 2; // 分钟导致时针的偏转角度
        float hourRotate = hourValue * 30 + hourOffsetRotate; // 时针的偏转角度
        canvas.save();
        canvas.rotate(hourRotate, radius, radius);
        canvas.drawLine(radius, radius - hourRadius, radius, radius - CENTER_RADIUS, mHourPaint);
        canvas.restore();
    }

    private void drawMinuteLine(Canvas canvas, int radius, int minuteValue) {
        int minuteRadius = (int) (radius * 0.7f); // 分针长度
        float minuteRotate = minuteValue * 6f;
        canvas.save();
        canvas.rotate(minuteRotate, radius, radius);
        canvas.drawLine(radius, radius - minuteRadius, radius, radius - CENTER_RADIUS, mMinutePaint);
        canvas.restore();
    }

    private void drawSecondLine(Canvas canvas, int radius, int secondValue) {
        int secondRadius = (int) (radius * 0.8f); // 秒针长度
        float secondRotate = secondValue * 6f;
        canvas.save();
        canvas.rotate(secondRotate, radius, radius);
        canvas.drawLine(radius, radius - secondRadius, radius, radius - CENTER_RADIUS, mSecondPaint);
        canvas.restore();
    }

    /**
     * 绘制刻度的方法二：(使用旋转)
     */
    private void drawScale02(Canvas canvas, int radius, int innerR, int outerR) {
        // 画中间的圆圈
        canvas.drawCircle(radius, radius, CENTER_RADIUS, mScalePaint);

        // 画刻度盘
        canvas.save();
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(radius, radius - outerR, radius, radius - innerR, mScalePaint);
            canvas.rotate(30, radius, radius); // 以圆点为中心，旋转30度
        }
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#000000"));
        if (mBgImage != null) {
            // 显示背景图片
//            canvas.drawBitmap(mBgImage, mSrcRect, mDesRect, mSecondPaint);
        }
    }

    /**
     * 绘制刻度的方法一：(使用三角函数)
     */
    private void drawScale01(Canvas canvas, int radius, int innerR, int outerR) {
        for (int i = 0; i < 12; i++) {
            // 角度
            double th = i * (Math.PI * 2 / 12);
            // 内环
            int innerB = (int) (Math.cos(th) * innerR);
            int innerX = mHeight / 2 - innerB;
            int innerA = (int) (innerR * Math.sin(th));
            int innerY = mWidth / 2 + innerA;
            // 外环
            int outerB = (int) (Math.cos(th) * outerR);
            int outerX = mHeight / 2 - outerB;
            int outerA = (int) (outerR * Math.sin((th)));
            int outerY = mWidth / 2 + outerA;
            canvas.drawLine(innerX, innerY, outerX, outerY, mScalePaint);
        }
    }

}
