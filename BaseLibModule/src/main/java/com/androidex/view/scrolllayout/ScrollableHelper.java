package com.androidex.view.scrolllayout;

import android.annotation.SuppressLint;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

public class ScrollableHelper {

    private ScrollableContainer mCurrentScrollableCainer;

    private int sysVersion = android.os.Build.VERSION.SDK_INT;

    /**
     * a viewgroup whitch contains ScrollView/ListView/RecycelerView..
     */
    public interface ScrollableContainer {
        /**
         * @return ScrollView/ListView/RecycelerView..'s instance
         */
        View getScrollableView();
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableCainer = scrollableContainer;
    }

    private View getScrollableView() {
        if (mCurrentScrollableCainer == null) {
            return null;
        }
        return mCurrentScrollableCainer.getScrollableView();
    }

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     *
     * @return
     */
    public boolean isTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) {
//            throw new NullPointerException("You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.");
            return true;
        }
        if(scrollableView instanceof WebView){

            if (scrollableView != null) {
                int scrollViewY = scrollableView.getScrollY();
                return scrollViewY <= 0;
            }
            return false;
        }else
        if (scrollableView instanceof AdapterView) {
            return isAdapterViewTop((AdapterView) scrollableView);
        }
        else if (scrollableView instanceof ScrollView) {
            return isScrollViewTop((ScrollView) scrollableView);
        }
        else if (scrollableView instanceof ScrollingView) {

            return !ViewCompat.canScrollVertically(scrollableView, -1);
//            return isScrollViewTop((ScrollingView) scrollableView);
        }
        throw new IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
    }

    private static boolean isAdapterViewTop(AdapterView adapterView) {
        if (adapterView != null) {
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);

            if (childAt == null || (firstVisiblePosition == 0 && childAt != null && childAt.getTop() >= 0)) {

                return true;
            }
        }
        return false;
    }

    private static boolean isScrollViewTop(ScrollView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (sysVersion >= 21) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        }

    }

    public void setScrollableViewEnable(boolean enable) {

        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            absListView.setClickable(enable);
        } else if (scrollableView instanceof ScrollView) {

            ((ScrollView) scrollableView).setClickable(enable);
        }
    }

}
