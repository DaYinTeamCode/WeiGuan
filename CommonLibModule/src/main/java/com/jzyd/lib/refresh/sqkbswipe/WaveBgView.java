package com.jzyd.lib.refresh.sqkbswipe;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


import com.jzyd.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/11/10.
 */

public class WaveBgView extends View {
    /**
     * View宽度
     */
    private int mViewWidth;

    /**
     * View高度
     */
    private int mViewHeight;

    private int mBgViewWidth;

    private int mBgViewHeight;

    /**
     * 水位线
     */
    private float mLevelLine;

    /**
     * 波浪起伏幅度
     */
    private float mWaveHeight = 70;

    /**
     * 波长
     */
    private float mWaveWidth = 50;

    /**
     * 被隐藏的最左边的波形
     */
    private float mLeftSide;
    /**
     * 移动的距离
     */
    private float mMoveLen;

    /**
     * 水波平移速度
     */
    public float mSpeed = 3f;

    /**
     * 二阶贝塞尔曲线
     */
    private List<Point> mPointsList;

    private Paint mPaint;
    private boolean isMeasured = false;
    private int progress = 0;
    /**
     * 波纹路径
     */
    private Path mWavePath;

    private boolean refreshing = false;

    private ValueAnimator mWavePathValueAnimator;

    private static float scale;

    private void slideWave(int speed) {
        // 记录平移总位移
        mSpeed = speed;
        mMoveLen += mSpeed;
        mLeftSide += mSpeed;
        // 波形平移
        for (int i = 0; i < mPointsList.size(); i++) {
            mPointsList.get(i).setX(mPointsList.get(i).getX() + mSpeed);
            switch (i % 4) {
                case 0:
                case 2:
                    mPointsList.get(i).setY(mLevelLine);
                    break;
                case 1:
                    mPointsList.get(i).setY(mLevelLine + mWaveHeight);
                    break;
                case 3:
                    mPointsList.get(i).setY(mLevelLine - mWaveHeight);
                    break;
            }
        }
        if (mMoveLen >= mWaveWidth) {
            // 波形平移超过一个完整波形后复位
            mMoveLen = 0;
            resetPoints();
        }
        invalidate();
    }

    /**
     * 所有点的x坐标都还原到初始状态，也就是一个周期前的状态
     */
    private void resetPoints() {
        mLeftSide = -mWaveWidth;
        for (int i = 0; i < mPointsList.size(); i++) {
            mPointsList.get(i).setX(i * mWaveWidth / 4 - mWaveWidth);
        }
    }

    public void onRefresh() {  // 开始刷新
        refreshing = true;
        start();
    }

    public void onRefreshCallBack() { // 回调刷新

        refreshing = false;
        stop();
    }

    public WaveBgView(Context context) {

        this(context, null);
    }

    public WaveBgView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public WaveBgView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        if (context != null) {

            scale = context.getResources().getDisplayMetrics().density;
        }
        initDate();
    }

    private void initDate() {
        mPointsList = new ArrayList<Point>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#FF0C87F5"));
        mWavePath = new Path();
    }

    @SuppressLint("WrongConstant")
    public void start() {
        if (mWavePathValueAnimator != null)
            mWavePathValueAnimator.cancel();
        mWavePathValueAnimator = ValueAnimator.ofFloat(8, 9);
        mWavePathValueAnimator.setInterpolator(new DecelerateInterpolator(1.0f));
        mWavePathValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress((int) ((value - 8) * 100));
                slideWave((int) value);
            }
        });
        mWavePathValueAnimator.setDuration(1800);
        mWavePathValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mWavePathValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mWavePathValueAnimator.start();
    }

    public void stop() {
        if (mWavePathValueAnimator != null)
            mWavePathValueAnimator.cancel();
        progress = 0;
        mMoveLen = 0;
        resetPoints();
        mLevelLine = mViewWidth;
        invalidate();
    }

    /**
     * 设置进度
     */
    public void setProgress(int chargePercent) {
        if (chargePercent < 0 || chargePercent > 100) {
            progress = 100;
        } else {
            // 水位百分比,progress越大水位线越低
            progress = 100 - (chargePercent);
        }
        mLevelLine = mViewHeight;
        mLevelLine = mLevelLine * progress / 100;

        if (mLevelLine < 0)
            mLevelLine = 0;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBgViewHeight = getMeasuredHeight();
        mBgViewWidth = getMeasuredWidth();
        if (!isMeasured) {
            mViewHeight = mBgViewHeight;
            mViewWidth = mBgViewWidth;
            isMeasured = true;
            // 水位线从最底下开始上升
            mLevelLine = mViewHeight;
            //计算水位线
            mLevelLine = mLevelLine * progress / 100;
            if (mLevelLine < 0)
                mLevelLine = 0;
            // 根据View宽度计算波形峰值
            mWaveHeight = mViewWidth / 8f;
            // 波长等于四倍View宽度也就是View中只能看到四分之一个波形，这样可以使起伏更明显
            mWaveWidth = mViewWidth;
            // 左边隐藏的距离预留一个波形
            mLeftSide = -mWaveWidth;
            // 这里计算在可见的View宽度中能容纳几个波形，注意n上取整
            int n = (int) Math.round(mViewWidth / mWaveWidth + 0.5);
            // n个波形需要4n+1个点，但是我们要预留一个波形在左边隐藏区域，所以需要4n+5个点
            for (int i = 0; i < (4 * n + 5); i++) {
                // 从P0开始初始化到P4n+4，总共4n+5个点
                float x = i * mWaveWidth / 4 - mWaveWidth;
                float y = 0;
                switch (i % 4) {
                    case 0:
                    case 2:
                        // 零点位于水位线上
                        y = mLevelLine;
                        break;
                    case 1:
                        // 往下波动的控制点
                        y = mLevelLine + mWaveHeight;
                        break;
                    case 3:
                        // 往上波动的控制点
                        y = mLevelLine - mWaveHeight;
                        break;
                }
                mPointsList.add(new Point(x, y));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {   // 画背景

        canvas.drawBitmap(createImage(), (int) (-4 * scale + 0.5f), (int) (-3 * scale + 0.5f), null);
    }

    private Bitmap createImage() {
        Bitmap bmp = Bitmap.createBitmap(mBgViewWidth, mBgViewHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bmp);
        mWavePath.reset();
        int i = 0;
        mWavePath.moveTo(mPointsList.get(0).getX(), mPointsList.get(0).getY());

        for (; i < mPointsList.size() - 2; i = i + 2) {
            float x1 = mPointsList.get(i + 1).getX();
            float y1 = mPointsList.get(i + 1).getY();
            float x2 = mPointsList.get(i + 2).getX();
            float y2 = mPointsList.get(i + 2).getY();
            mWavePath.quadTo(x1, y1, x2, y2);
        }
        mWavePath.lineTo(mPointsList.get(i).getX(), mViewHeight);
        mWavePath.lineTo(mLeftSide, mViewHeight);
        mWavePath.close();

        // mPaint的Style是FILL，会填充整个Path区域
        if (refreshing)
            canvas.drawPath(mWavePath, mPaint);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        Bitmap bgDrawableId = getBitmapByBitmap(R.drawable.ic_refresh_hreader_icon, mBgViewWidth, mBgViewHeight);
        canvas.drawBitmap(bgDrawableId, 0, 0, paint);  // 画背景图片
        paint.setXfermode(null);
        return bmp;
    }

    /**
     * 加载BitMap
     *
     * @param drawableId
     * @param width
     * @param height
     * @return
     */
    private Bitmap getBitmapByBitmap(int drawableId, float width, float height) {
        int simpleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), drawableId, options);
        while ((options.outWidth / simpleSize > width) || (options.outHeight / simpleSize > height)) {
            simpleSize *= 2;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = simpleSize;
        return BitmapFactory.decodeResource(getResources(), drawableId);
    }

    class Point {    //  创建坐标点
        private float x;

        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
            Log.d("WaveViewDemo", "( " + x + " , " + y + " )");
        }
    }
}
