package com.androidex.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * 圆形drawable
 * Created by yihaibin on 15/10/13.
 */
public class CircleBitmapDrawable extends Drawable {

    private Paint mPaint;
    private int mWidth;

    public CircleBitmapDrawable(Bitmap bitmap){

        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {

//        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, Math.min(canvas.getWidth(), canvas.getHeight()) / 2, mPaint);
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
    }

    @Override
    public int getIntrinsicWidth() {

        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {

        return mWidth;
    }

    @Override
    public void setAlpha(int alpha) {

        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {

        return PixelFormat.TRANSLUCENT;
    }
}
