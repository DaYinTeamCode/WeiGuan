package com.androidex.view.pager;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 该ViewPager没有别的，就新增了ViewPager是否开启或关闭滚动的api
 *
 * @author yhb
 */
public class ExViewPager extends ViewPager {

    private boolean mScrollEnabled = true;
    private boolean mTryOnInterceptTouchEventException;

    public ExViewPager(Context context) {

        super(context);
    }

    public ExViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {//java.lang.IllegalArgumentException pointerIndex out of range

            if (mTryOnInterceptTouchEventException)
                return this.mScrollEnabled && super.onTouchEvent(event);
            else
                return this.mScrollEnabled && super.onTouchEvent(event);

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try {//java.lang.IllegalArgumentException pointerIndex out of range pointerIndex=-1 pointerCount=2

            if (mTryOnInterceptTouchEventException)
                return this.mScrollEnabled && super.onInterceptTouchEvent(event);
            else
                return this.mScrollEnabled && super.onInterceptTouchEvent(event);

        } catch (Exception e) {

            return false;
        }
    }

    public void setScrollEnabled(boolean enabled) {

        mScrollEnabled = enabled;
    }

    public void setTryOnInterceptTouchEventException(boolean tryEnable) {

        mTryOnInterceptTouchEventException = tryEnable;
    }
}
