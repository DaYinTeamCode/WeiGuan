package com.androidex.asyncimage;

import android.graphics.Bitmap;

import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;

/**
 * 缓存asyncImageView 图片资源
 */
public class RefBitmap implements IRefBitmap {

    private String mUri = TextUtil.TEXT_EMPTY;
    private int mRefCount;
    private int mDestW, mDestH;
    private Bitmap mBmp;

    public RefBitmap(String uri, int destW, int destH, Bitmap bmp) {

        mUri = TextUtil.filterNull(uri);
        mBmp = bmp;
        mDestW = destW;
        mDestH = destH;
    }

    @Override
    public String getUri() {

        return this.mUri;
    }

    public int getMaxNumOfPixels() {

        return mDestW * mDestH;
    }

    @Override
    public int getByteSize() {

        return mBmp == null || mBmp.isRecycled() ? 0 : mBmp.getRowBytes() * mBmp.getHeight();
    }

    @Override
    public void refCountAdd1() {

        mRefCount++;
        checkState();
    }

    @Override
    public void refCountMinus1() {

        mRefCount--;
        checkState();
    }

    private void checkState() {

        if(mRefCount > 0 || mBmp == null || mBmp.isRecycled())
            return;

        mBmp.recycle();

        if (LogMgr.isDebug())
            LogMgr.w(getClass().getSimpleName(), "recycle bmp refcount= " +mRefCount + ", uri=" +mUri);
    }

    @Override
    public int getRefCount() {

        return mRefCount;
    }

    @Override
    public int hashCode() {

        return mUri.hashCode() + mDestW * mDestH;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof RefBitmap))
            return false;

        RefBitmap rb = (RefBitmap) o;
        if (mUri.equals(rb.mUri) && mDestW * mDestH == rb.mDestW * rb.mDestH)
            return true;
        else
            return false;
    }

    @Override
    public Bitmap getBitmap() {

        return mBmp;
    }

    @Override
    public boolean isRecycled() {

        return mBmp == null || mBmp.isRecycled();
    }

    @Override
    public String toString() {

        return "refbmp: refCount="+mRefCount+", bmp is recycle="+mBmp.isRecycled()+", uri="+mUri;
    }
}
