package com.androidex.view.scrolllayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.androidex.R;
import com.androidex.util.LogMgr;
import com.androidex.view.Listview.XListViewHeader;

public class ScrollableLayout extends LinearLayout {

    private final String tag = "scrollableLayout";

    /*跟踪触摸屏事件(flinging事件和其他gestures手势事件)的速率*/
    private VelocityTracker mVelocityTracker;

    /*最小速率*/
    private int mMinimumVelocity;

    /*最大速率*/
    private int mMaximumVelocity;

    /*滚动类*/
    private Scroller mScroller;

    /*判断系统在按下之后移动多少才认定为滑动事件*/
    private int mTouchSlop;

    /*按下点*/
    private float mDownX;
    private float mDownY;

    /*移动上一次所在的点*/
    private float mLastX;
    private float mLastY;

    /*滑动方向*/
    private DIRECTION mDirection;

    /*头部可滚动view*/
    private View mHeadView;
    /*可滚显示的头部视图*/
    private int mHeadHeight;

    /*手机系统api版本*/
    private int sysVersion;

    /* 子控件包含的viewpager */
    private ViewPager childViewPager;
    private boolean flag1, flag2, mIsScrollLayout;
    private int mLastScrollerY;

    /*不拦截*/
    private boolean mDisallowIntercept;

    /* 保留滚动的最小高度 */
    private int desY;
    /*刷新头部视图高度*/
    private int mHeaderViewHeight;

    /*是否为下拉刷新*/
    private boolean isPullRefresh;

    /*扩大头部视图点击*/
    private int mExpandHeight = 0;

    /* 滚动到顶部 */
    private int minY = 0;
    /* scrollable 最大可滚动值 */
    private int maxY = 0;
    private int originMaxY;

    /* 滚动监听接口 */
    private OnScrollListener onScrollListener;

    private int mCurY;
    private boolean isClickHead;
    private boolean isClickHeadExpand;

    /*是否可以超出滚动*/
    private boolean overScrollTop;

    /* 下载刷新 */
    private XListViewHeader mHeaderview;
    private boolean hasHeadView;

    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing;

    private boolean isPullDown;
    private boolean isIgnoreScrollAni;

    private int mDistance;//滚动距离

    //滚动变化
    private OnScrollChange mOnScrollChange;

    public void setEnablePullRefresh(boolean mEnablePullRefresh) {

        this.mEnablePullRefresh = mEnablePullRefresh;
    }

    public void setChildViewPager(ViewPager childViewPager) {
        this.childViewPager = childViewPager;
    }

    public int getDesY() {
        return desY;
    }

    public void setDesY(int desY) {

        this.desY = desY;
    }

    public void setFitMaxY(int maxY) {

        if (maxY < 0)
            maxY = 0;

        this.maxY = maxY;
    }

    public int getMaxY() {

        return this.maxY;
    }

    public int getOriginMaxY() {

        return originMaxY;
    }

    public void setProgressColor(int color) {

        if (mHeaderview != null)
            mHeaderview.getTipLoadingView().setBarColor(color);
    }

    /**
     * 设置下拉刷新头部视图
     */
    private void setCustomHeadview(XListViewHeader customHeadview) {

        if (mHeaderview != null) {
            if (mHeaderview == customHeadview)
                return;
            removeView(mHeaderview);
        }

        mHeaderview = customHeadview;

        RelativeLayout mHeaderViewContent = (RelativeLayout) mHeaderview.findViewById(R.id.xlistview_header_content);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mHeaderViewContent.measure(w, h);
        mHeaderViewHeight = mHeaderViewContent.getMeasuredHeight();

        addViewInLayout(mHeaderview, 0, new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        hasHeadView = true;
    }

    public void setRefreshViewHeaderViewMargin(int marginTop) {

        if (mHeaderview != null) {

            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mHeaderview.getLayoutParams();
            marginLayoutParams.topMargin = marginTop;
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    private ScrollableHelper mHelper;

    public ScrollableHelper getHelper() {
        return mHelper;
    }

    public ScrollableLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {

        mHelper = new ScrollableHelper();
        mScroller = new Scroller(context);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        sysVersion = Build.VERSION.SDK_INT;

        setCustomHeadview(new XListViewHeader(context));

        mHeaderview.setState(XListViewHeader.STATE_NORMAL);
    }

    long calateTime = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (LogMgr.isDebug()) {

            LogMgr.d(" spent time : " + (System.currentTimeMillis() - calateTime));
            calateTime = System.currentTimeMillis();
        }


        float currentX = ev.getX();
        float currentY = ev.getY();

        if (LogMgr.isDebug())
            LogMgr.d(tag, "currentY  :" + currentY);

        float deltaY;
        int shiftX;
        int shiftY;

        shiftX = (int) Math.abs(currentX - mDownX);
        shiftY = (int) Math.abs(currentY - mDownY);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                flag1 = true;
                flag2 = true;
                mDownX = currentX;
                mDownY = currentY;
                mLastX = currentX;
                mLastY = currentY;
                checkIsClickHead((int) currentY, mHeadHeight, getScrollY());
                checkIsClickHeadExpand((int) currentY, mHeadHeight, getScrollY());
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                mScroller.forceFinished(true);

                mDistance = 0;

                break;
            case MotionEvent.ACTION_MOVE:

                if (mDisallowIntercept) {
                    break;
                }
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);
                deltaY = mLastY - currentY;

                if (LogMgr.isDebug())
                    LogMgr.d(tag, "deltaY:" + deltaY);

                if (flag1) {
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        flag1 = false;
                        flag2 = false;
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        flag1 = false;
                        flag2 = true;

                        isPullDown = (currentY - mDownY) > 0 && canPullScrollable(deltaY) && mCurY == minY;

                    }
                }

                if (mEnablePullRefresh && isPullDown && (canPullScrollable(deltaY))) {

                    updateHeaderHeight(deltaY);

                } else {

                    if (flag2 && shiftY > mTouchSlop && shiftY > shiftX && (!isSticked() || mHelper.isTop() || isClickHeadExpand)) {

                        if (childViewPager != null) {

                            childViewPager.requestDisallowInterceptTouchEvent(true);
                        }

                        scrollBy(0, (int) (deltaY));

                        mDistance += deltaY;
                    }
                }
                mLastX = currentX;
                mLastY = currentY;
                break;

            case MotionEvent.ACTION_UP:

                isPullDown = false;
                if (flag2 && shiftY > shiftX && shiftY > mTouchSlop) {

                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    float yVelocity = -mVelocityTracker.getYVelocity();

                    if (Math.abs(yVelocity) > mMinimumVelocity) {
                        mDirection = yVelocity > 0 ? DIRECTION.UP : DIRECTION.DOWN;

                        if (LogMgr.isDebug())
                            LogMgr.d("yVelocity : " + yVelocity + "  sticked : " + isSticked());

                        if (mDirection == DIRECTION.UP && isSticked()) {


                        } else if (!mPullRefreshing) {

                            mScroller.fling(0, getScrollY(), 0, (int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                            mScroller.computeScrollOffset();
                            mLastScrollerY = getScrollY();
                            invalidate();

                        } else if (mPullRefreshing && mHeaderview.getVisiableHeight() == mHeaderViewHeight) {

                            isIgnoreScrollAni = true;
                            mScroller.fling(0, getScrollY(), 0, (int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                            mScroller.computeScrollOffset();
                            mLastScrollerY = getScrollY();
                            invalidate();
                        }
                    }

                    if (mEnablePullRefresh && mHeaderview.getVisiableHeight() > mHeaderViewHeight && !mPullRefreshing) {

                        startRefresh(false);
                    }

                    if (isClickHead || !isSticked()) {

                        if (mIsScrollLayout) {

                            int action = ev.getAction();
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                            boolean dd = super.dispatchTouchEvent(ev);
                            resetHeaderHeight();
                            ev.setAction(action);

                            mIsScrollLayout = false;
                            return dd;
                        }
                    }
                }

                resetHeaderHeight();
                break;
            case MotionEvent.ACTION_CANCEL:

                if (flag2 && isClickHead && (shiftX > mTouchSlop || shiftY > mTouchSlop)) {
                    int action = ev.getAction();
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    boolean dd = super.dispatchTouchEvent(ev);
                    ev.setAction(action);
                    return dd;
                }
                break;
            default:
                break;
        }

        super.dispatchTouchEvent(ev);
        return true;
    }

    private boolean canPullScrollable(float deltaY) {

        return (isClickHeadExpand && deltaY < 0) || mHelper.isTop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        super.onInterceptTouchEvent(ev);

        if (LogMgr.isDebug())
            LogMgr.d(" spent time intercept : " + isPullDown);

        return isPullDown;
    }

    public void requestScrollableLayoutDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (sysVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {

            if (mHeaderview.getVisiableHeight() > 0 && !isIgnoreScrollAni) {

                if (mScrollBack == SCROLLBACK_HEADER || mScrollBack == SCROLLBACK_HEADER_FORECE) {

                    mHeaderview.setVisiableHeight(mScroller.getCurrY());
                    callbackOnPullScroll();
                }

                postInvalidate();

            } else {

                final int currY = mScroller.getCurrY();
                if (mDirection == DIRECTION.UP) {
                    // 手势向上划
                    if (isSticked()) {
                        int distance = mScroller.getFinalY() - currY;
                        int duration = calcDuration(mScroller.getDuration(), mScroller.timePassed());
                        mHelper.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration);
                        mScroller.forceFinished(true);
                        return;
                    } else {
                        scrollTo(0, currY);
                    }
                } else {
                    // 手势向下划
                    if (mHelper.isTop() || isClickHeadExpand) {
                        int deltaY = (currY - mLastScrollerY);
                        int toY = getScrollY() + deltaY;
                        scrollTo(0, toY);
                        if (mCurY <= minY) {
                            mScroller.forceFinished(true);
                            return;
                        }
                    }
                    invalidate();
                }
                mLastScrollerY = currY;
            }
        } else {

            isIgnoreScrollAni = false;
        }
    }

    @Override
    public void scrollBy(int x, int y) {

        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= getCurMaxY()) {

            toY = getCurMaxY();

        } else if (toY <= minY) {

            toY = minY;
        }

        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {

        if (y >= getCurMaxY()) {

            y = getCurMaxY();

        } else if (y <= minY) {

            y = minY;

        } else {

            if (mCurY != y)
                mIsScrollLayout = true;
        }

        mCurY = y;

        callbackOnScroll(y);

        super.scrollTo(x, y);
    }

    public int getCurMaxY() {

        return maxY + mHeaderview.getVisiableHeight();
    }

    private void updateHeaderHeight(float delta) {

        if (!mEnablePullRefresh)
            return;

        delta = delta / OFFSET_RADIO;

        mHeaderview.setVisiableHeight((int) (-delta) + mHeaderview.getVisiableHeight());

        LogMgr.d("updateHeaderHeight : " + mHeaderview.getVisiableHeight());
        callbackOnPullScroll();

        if (mEnablePullRefresh && !mPullRefreshing) {

            if (mHeaderview.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderview.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderview.setState(XListViewHeader.STATE_NORMAL);
            }
        }
    }

    private void callbackOnScroll(int y) {

        if (onScrollListener != null) {
            onScrollListener.onScroll(y, maxY, mDistance);
        }
    }

    private void callbackOnPullScroll() {

        if (onScrollListener != null)
            onScrollListener.onPullScroll(mHeaderview.getVisiableHeight());
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderview.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }

        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }

        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void startRefresh(boolean force) {

        if (!mPullRefreshing && swipeListener != null) {
            mPullRefreshing = true;
            mHeaderview.setState(XListViewHeader.STATE_REFRESHING);
            swipeListener.onSwipeRefresh(force);
        }

    }

    /**
     * 是否正在刷新
     * @return
     */
    public boolean isPullRefreshing() {

        return mPullRefreshing;
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {

        if (!mPullRefreshing)
            return;

        isPullRefresh = false;
        mPullRefreshing = false;
        isIgnoreScrollAni = false;

        resetHeaderHeight();
    }

    private SwipeListener swipeListener;
    private int mScrollBack;
    private final static int SCROLL_DURATION = 300; // scroll back duration
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_HEADER_FORECE = 1;
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public interface SwipeListener {

        public void onSwipeRefresh(boolean force);

    }

    public boolean isSticked() {

        if (LogMgr.isDebug())
            LogMgr.d(tag, "isSticked = " + (mCurY == maxY) + " mCurY : " + mCurY);

        return mCurY == getCurMaxY();
    }

    public XListViewHeader getHeaderView() {

        return mHeaderview;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }


    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void checkIsClickHead(int downY, int headHeight, int scrollY) {
        isClickHead = downY + scrollY <= headHeight;
    }

    private void checkIsClickHeadExpand(int downY, int headHeight, int scrollY) {
        if (mExpandHeight <= 0) {
            isClickHeadExpand = false;
        }
        isClickHeadExpand = downY + scrollY <= headHeight + mExpandHeight;
    }

    private int calcDuration(int duration, int timepass) {
        return duration - timepass;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mHeadView = getChildAt(1);
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);

        originMaxY = mHeadView.getMeasuredHeight() - getDesY();
        maxY = originMaxY;

        mHeadHeight = mHeadView.getMeasuredHeight() - getDesY();

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + maxY, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onFinishInflate() {
        if (mHeadView != null && !mHeadView.isClickable()) {
            mHeadView.setClickable(true);
        }
        super.onFinishInflate();
    }

    public int getScrollLayoutTop() {

        return mHeaderview != null ? mHeaderview.getVisiableHeight() : 0;
    }

    public void setOnScrollChange(OnScrollChange mOnScrollChange) {

        this.mOnScrollChange = mOnScrollChange;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if(mOnScrollChange != null)
            mOnScrollChange.onScrollChange(l, t,oldl, oldt);
    }

    public interface OnScrollChange{

        void onScrollChange(int l, int t, int oldl, int oldt);
    }

    /**
     * 滑动方向
     */
    public enum DIRECTION {

        UP,// 向上划
        DOWN// 向下划
    }

    public interface OnScrollListener {

        void onScroll(int currentY, int maxY, int distance);

        void onPullScroll(int distance);

    }

}
