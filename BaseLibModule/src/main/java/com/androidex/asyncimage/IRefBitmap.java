package com.androidex.asyncimage;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

/**
 * 用于缓存图片资源接口
 */
public interface IRefBitmap {

	String getUri();
    int getByteSize();
    void refCountAdd1();
    void refCountMinus1();
    int getRefCount();
    int getMaxNumOfPixels();
    Bitmap getBitmap();
    boolean isRecycled();
}
