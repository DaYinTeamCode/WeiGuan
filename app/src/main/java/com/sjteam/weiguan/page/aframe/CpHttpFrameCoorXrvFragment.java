package com.sjteam.weiguan.page.aframe;

import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.androidex.util.CollectionUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.VglpUtil;
import com.androidex.widget.rv.adapter.ExRvAdapterBase;
import com.androidex.widget.rv.hf.ExRvItemViewHolderFooter;
import com.androidex.widget.rv.view.ExRecyclerView;
import com.androidex.widget.rv.view.ExRvLoadMoreView;
import com.ex.android.http.task.HttpTask;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.jzyd.lib.httptask.JzydJsonListener;
import com.jzyd.lib.httptask.ExResponse;
import com.jzyd.lib.refresh.sqkbswipe.SqkbSwipeRecyclerViewScrollUpCallback;
import com.jzyd.lib.refresh.sqkbswipe.SqkbSwipeRefreshLayout;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.constants.ColorConstants;
import com.sjteam.weiguan.utils.CpFontUtil;

import java.util.List;

/**
 * 该类继承自AppHttpFrameLvFragment，提供使用XListView来填充布局
 * 支持SwipeRefresh，默认开启 SwipeRefresh和loadMoreRefresh
 *
 * @param <T>
 * @author yhb
 */
public abstract class CpHttpFrameCoorXrvFragment<T>
        extends CpHttpCoordinatorRvFragment
        implements ExRvItemViewHolderFooter.ILoadMoreListener,
        SqkbSwipeRefreshLayout.OnRefreshListener, SqkbSwipeRefreshLayout.OnRefreshCompletedListener {

    private SqkbSwipeRefreshLayout mSwipeView;

    private HttpTask mPullRefreshHttpTask;
    private HttpTask mLoadMoreHttpTask;
    private int mPageLimit = 10;
    private boolean mPullRefreshEnable = true;
    private boolean mLoadMoreEnable = true;
    private boolean mLoadMoreIsStrictMode; //是否是严格的loadmore:加载更多返回0条数据的时候，才表示没有加载更多
    private int mPageStartIndex = 0;
    private int mCurrentPageIndex = 0;
    private boolean mPullRefreshNeedSaveCache;
    private boolean mCacheRefreshNeedPullAction = false;

    /*刷新任务是否完成*/
    private OnTaskFinish onTaskFinish;

    protected void setContentRecyclerView() {
        initSwipAndRecycleView(false);
//        setContentView(initSwipAndRecycleView(false));
    }

    protected void setContentSwipeRefreshRecyclerView() {
        initSwipAndRecycleView(false);
//        setContentView(initSwipAndRecycleView(false));
    }

    protected View initSwipAndRecycleView() {

        return initSwipAndRecycleView(true);
    }

    protected View initSwipAndRecycleView(boolean needSwipeView) {

        //创建 load more view
        ExRvLoadMoreView loadMoreView = new ExRvLoadMoreView(getContext());
        CpFontUtil.setFont(loadMoreView.getLoadTextView());
        CpFontUtil.setFont(loadMoreView.getNoDataTextView());
        loadMoreView.getProgressView().setBarColor(ColorConstants.PROGRESS_MAIN);

        ExRecyclerView erv = onCreateRecyclerView();
        erv.setLoadMorer(loadMoreView, this);
        if (erv.getItemAnimator() instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) erv.getItemAnimator()).setSupportsChangeAnimations(false);

        if (needSwipeView) {

            mSwipeView = new SqkbSwipeRefreshLayout(getActivity());
            mSwipeView.setEnabled(false);
            mSwipeView.setOnRefreshListener(this);
            mSwipeView.setOnRefreshCompletedListener(this);
            mSwipeView.setColorSchemeColors(ColorConstants.PROGRESS_MAIN);
            mSwipeView.setOnChildScrollUpCallback(new SqkbSwipeRecyclerViewScrollUpCallback());
            mSwipeView.addView(erv, VglpUtil.getVglpMM());
            return mSwipeView;
        }

        return erv;
    }

    public SqkbSwipeRefreshLayout getSwipeView() {

        return mSwipeView;
    }

    protected void setLoadMoreNoDataTipAttr(String tipText, int drawableLeft, int drawableRight) {

        setLoadMoreNoDataTipAttr(tipText, drawableLeft, 0, drawableRight, 0);
    }

    protected void setLoadMoreNoDataTipAttr(String tipText, int drawableLeft, int drawableRight, int drawableBtm) {

        setLoadMoreNoDataTipAttr(tipText, drawableLeft, 0, drawableRight, drawableBtm);
    }

    protected void setLoadMoreNoDataTipAttr(String tipText, int drawableLeft, int drawableTop, int drawableRight, int drawableBtm) {

        ExRvItemViewHolderFooter.ILoadMorer loadMorer = null;
        if (getRecyclerView() != null)
            loadMorer = getRecyclerView().getLoadMorer();

        if (loadMorer != null && loadMorer instanceof ExRvLoadMoreView) {

            ExRvLoadMoreView ermv = (ExRvLoadMoreView) loadMorer;
            ermv.setNoDataAttr(tipText, drawableLeft, drawableTop, drawableRight, drawableBtm);
        }
    }

    protected void performSwipeRefresh() {

        if (mSwipeView != null)
            mSwipeView.performRefresh(true);
    }

    @Override
    public void onRefresh() {

        if (DeviceUtil.isNetworkEnable()) {

            startPullRefresh();
        } else {

            showToast(R.string.toast_network_none);
            if (mSwipeView != null)
                mSwipeView.setRefreshing(false);
        }
    }

    @Override
    public void onRefreshCompleted() {

        //nothing
    }

    @Override
    public boolean onLoadMore(boolean fromMan) {

        if (DeviceUtil.isNetworkEnable()) {

            startLoadMoreRefresh();
            return true;
        } else {

            if (fromMan)
                showToast(R.string.toast_network_none);

            return false;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        abortPullRefresh();
        abortLoadMore();
    }

    /**
     * 设置分页大小, 默认为10
     *
     * @param pageLimit 分页数
     */
    protected void setPageLimit(int pageLimit) {

        mPageLimit = pageLimit;
    }

    /**
     * @return 获取分页大小
     */
    protected int getPageLimit() {

        return mPageLimit;
    }

    /**
     * 设置页码的起始索引，可以是页码也可以是id,默认为1
     *
     * @param pageStartIndex 页码启始索引
     */
    protected void setPageStartIndex(int pageStartIndex) {

        mPageStartIndex = pageStartIndex;
    }

    /**
     * 获取页码起始索引
     *
     * @return
     */
    protected int getPageStartIndex() {

        return mPageStartIndex;
    }

    /**
     * 设置当前页码索引
     *
     * @param index
     */
    protected void setCurrentPageIndex(int index) {

        mCurrentPageIndex = index;
    }

    /**
     * 获取当前页码索引
     *
     * @return
     */
    public int getCurrentPageIndex() {

        return mCurrentPageIndex;
    }

    /**
     * 设置缓存刷新时是否需要下拉的动作
     *
     * @param need
     */
    public void setCacheRefreshNeedPullAction(boolean need) {

        this.mCacheRefreshNeedPullAction = need;
    }

    public void setLoadMoreStrictMode(boolean isStrictMode) {

        mLoadMoreIsStrictMode = isStrictMode;
    }

    /*************************************************** Frame网络请求 *************************************************/

    /**
     * 重写有缓存的网络请求逻辑,此时下拉刷新是需要保存缓存的
     */
    @Override
    protected boolean executeFrameCache(Object... params) {

        mPullRefreshNeedSaveCache = true;
        return super.executeFrameCache(params);
    }

    @Override
    protected boolean executeFrameCacheAndRefresh(Object... params) {

        mPullRefreshNeedSaveCache = true;
        return super.executeFrameCacheAndRefresh(params);
    }

    @Override
    protected boolean executeFrameRefreshAndCache(Object... params) {

        mPullRefreshNeedSaveCache = true;
        return super.executeFrameRefreshAndCache(params);
    }

    /**
     * 重写ListView的invalidateContent方法
     * 实现XListView的逻辑
     * 该回调是子类通过executeFrameXXX方法在页面中第一次加载XListView数据
     */
    @Override
    protected boolean invalidateContent(Object result) {
        List<?> list = invalidateContentGetList(result);
        invalidateContentRefreshRecyclerView(list, false);
        return !CollectionUtil.isEmpty(list);
    }

    /***************************************************下拉刷新部分*************************************************/

    /**
     * 回调getXListViewHttpParams获取请求参数
     */
    public void forcePullRefresh() {

        //暂不实现
    }

    /**
     * 缓存加载成功后的缓存更新。如果有缓存且有网络，拦截掉做自己的更新操作
     */
    @Override
    protected boolean onInterceptCacheRefreshStart(boolean cacheEnable) {

        if (mCacheRefreshNeedPullAction) {

            //有缓存且有网络，拦截，做自己的更新操作 //.isSwipeRefreshEnable()
            if (cacheEnable && DeviceUtil.isNetworkEnable() &&
                    mSwipeView != null && mSwipeView.isEnabled()) {

                mSwipeView.performRefresh(true);
                return true;
            }
        }

        return false;
    }

    protected void setPullRefreshEnable(boolean enable) {

        if (!enable)
            abortPullRefresh();

        if (mSwipeView != null)
            mSwipeView.setEnabled(enable);

        mPullRefreshEnable = enable;

        //暂不实现
    }

    protected boolean isPullRefreshEnable() {

        return mPullRefreshEnable;
    }

    public void startPullRefresh() {

        if (isFinishing())
            return;

        abortLoadMore();//中断加载更多，如果在运行的话

        //mCurrentPageIndex = mPageStartIndex;//重置起始页?隐藏的惊天大bug???
        //executePullRefreshHttpTask(mCurrentPageIndex, mPageLimit);
        //修复惊天dabug?
        executePullRefreshHttpTask(mPageStartIndex, mPageLimit);
    }

    protected void executePullRefreshHttpTask(int pageIndex, int pageLimit) {

        HttpFrameParams hfp = getPageHttpParams(pageIndex, pageLimit);
        mPullRefreshHttpTask = new HttpTask(hfp.params);
        mPullRefreshHttpTask.setListener(new PullRefreshHttpTaskListener(hfp.clazz));
        mPullRefreshHttpTask.execute();
    }

    protected boolean isPullRefreshing() {

        return mPullRefreshHttpTask != null && mPullRefreshHttpTask.isRunning();
    }

    protected void abortPullRefresh() {

        if (isPullRefreshing()) {

            mPullRefreshHttpTask.abort();
            mPullRefreshHttpTask = null;
            if (mSwipeView != null)
                mSwipeView.setRefreshing(false);
        }
    }

    /**
     * 下拉刷新网络请求监听
     *
     * @author yhb
     */
    private final class PullRefreshHttpTaskListener extends JzydJsonListener<T> {

        public PullRefreshHttpTaskListener(Class<?> clazz) {

            super(clazz);
        }

        @Override
        public void onTaskPre() {

            if (!isFinishing())
                onPullRefreshPre();
        }

        @Override
        public void onTaskResultDoInBackground(T t) {

            onPullRefreshResultDoInBackground(t);
        }

        @Override
        public void onTaskSuccess(ExResponse<T> resp) {

            onPullRefreshTaskSuccess(resp);
            super.onTaskSuccess(resp);
        }

        @Override
        public void onTaskResult(T result) {

            if (!isFinishing()) {

                onPullRefreshResult(result);

                if (mSwipeView != null)
                    mSwipeView.setRefreshing(false);

                callBackFinishRefreshTask();
            }
            mPullRefreshHttpTask = null;
        }

        @Override
        public boolean onTaskSaveCache(ExResponse<T> resp) {

            if (isFinishing())
                return false;

            if (mPullRefreshNeedSaveCache)

                return onFrameSaveCache(resp);
            else {

                return super.onTaskSaveCache(resp);
            }
        }

        @Override
        public void onTaskFailed(int failedCode, String msg) {

            if (!isFinishing()) {

                onPullRefreshFailed(failedCode, msg);

                if (mSwipeView != null)
                    mSwipeView.setRefreshing(false);

                callBackFinishRefreshTask();
            }
            mPullRefreshHttpTask = null;
        }

        @Override
        public void onTaskAbort() {

            //nothing to do
        }

        private void callBackFinishRefreshTask() {

            if (onTaskFinish != null) {

                onTaskFinish.onTaskFinish();
            }
        }
    }

    protected void onPullRefreshPre() {

    }

    protected void onPullRefreshResultDoInBackground(T t) {

    }

    protected void onPullRefreshTaskSuccess(ExResponse<T> resp) {

    }

    protected void onPullRefreshResult(T result) {

        if (invalidateContent(result)) {

            //nothing to do，这两行代码其实一般情况下可以不要
            hideContentDisable();
            showContent();
        } else {

            hideContent();
            showContentDisable();
        }
    }

    protected void onPullRefreshFailed(int failedCode, String msg) {

        showToast(R.string.toast_network_error_try);
    }

    /***************************************************下拉刷新部分*************************************************/
    /**
     * 设置是否可以加载更多，子类还请通过该方法来实现，
     * 不要在子类中获取XListView对象来进行设置
     *
     * @param enable
     */
    protected void setLoadMoreEnable(boolean enable) {

        if (!enable) {

            abortLoadMore();
            getRecyclerView().setLoadMoreEnable(enable);
        }

        //卡片布局loadmore，暂时注释，详见最底下
        //setLoadMoreFooterViewEnable(enable);

        mLoadMoreEnable = enable;
    }

    protected boolean isLoadMoreEnable() {

        return mLoadMoreEnable;
    }

    protected boolean isLoadMoreStrictMode() {

        return mLoadMoreIsStrictMode;
    }

    protected void startLoadMoreRefresh() {

        startLoadMoreRefresh(true);
    }

    protected void startLoadMoreRefresh(boolean abortPullRefresh) {

        if (isFinishing())
            return;

        if (abortPullRefresh)
            abortPullRefresh();

        abortLoadMore();

        HttpFrameParams hfp = getPageHttpParams(mCurrentPageIndex + 1, mPageLimit);
        mLoadMoreHttpTask = new HttpTask(hfp.params);
        mLoadMoreHttpTask.setListener(getLoadMoreHttpTaskListener(hfp));
        mLoadMoreHttpTask.execute();
    }

    protected JzydJsonListener<?> getLoadMoreHttpTaskListener(HttpFrameParams hfp) {

        return new LoadMoreHttpTaskListener(hfp.clazz);
    }

    protected boolean isLoadMoreing() {

        return mLoadMoreHttpTask != null && mLoadMoreHttpTask.isRunning();
    }

    protected void abortLoadMore() {

        if (isLoadMoreing()) {

            mLoadMoreHttpTask.abort();
            mLoadMoreHttpTask = null;
            getRecyclerView().stopLoadMore();
        }
    }

    private final class LoadMoreHttpTaskListener extends JzydJsonListener<T> {

        public LoadMoreHttpTaskListener(Class<?> clazz) {

            super(clazz);
        }

        @Override
        public void onTaskPre() {

            if (!isFinishing())
                onLoadMorePre();
        }

        @Override
        public void onTaskResultDoInBackground(T t) {

            onLoadMoreResultDoInBackground(t);
        }

        @Override
        public void onTaskSuccess(ExResponse<T> resp) {

            onLoadMoreTaskSuccess(resp);
            super.onTaskSuccess(resp);
        }

        @Override
        public void onTaskResult(T result) {

            if (!isFinishing()) {

                onLoadMoreResult(result);
            }
            mLoadMoreHttpTask = null;
        }

        @Override
        public void onTaskFailed(int failedCode, String msg) {

            if (!isFinishing()) {

                onLoadMoreFailed(failedCode, msg);
                getRecyclerView().stopLoadMoreFail();
            }
            mLoadMoreHttpTask = null;
        }

    }

    protected void onLoadMorePre() {

    }

    protected void onLoadMoreResultDoInBackground(T t) {

    }

    protected void onLoadMoreTaskSuccess(ExResponse<T> resp) {

    }

    protected void onLoadMoreResult(T result) {

        invalidateContentRefreshRecyclerView(invalidateContentGetList(result), true);
        getRecyclerView().stopLoadMore();
    }

    protected void onLoadMoreFailed(int failedCode, String msg) {

        //nothing to do
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void invalidateContentRefreshRecyclerView(List<?> result, boolean isAddAll) {

        ExRvAdapterBase adapter = getRecyclerViewAdapter();
        if (isAddAll) {

            int rangeStart = adapter.getDataItemCount() + (adapter.hasHeader() ? 1 : 0);
            int rangeEnd = CollectionUtil.size(result) + (adapter.hasFooter() ? 1 : 0);
            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "footer invalidate load more add all size = " + CollectionUtil.size(result) + ", rangeStart=" + rangeStart + ", rangeEnd=" + rangeEnd);

            adapter.addDataAll(result);
            invalidateContentRefreshRecyclerViewDataSetChangedBeforeNotify();

            //追加数据为空，或小于分页数，关闭加载更多
            getRecyclerView().setLoadMoreEnable(onRefreshRecyclerViewCheckLoadMoreEnable(result));
            adapter.notifyItemRangeChanged(rangeStart, rangeEnd);

            mCurrentPageIndex += 1;

        } else {

            int size = CollectionUtil.size(result);
            adapter.setData(result);
            invalidateContentRefreshRecyclerViewDataSetChangedBeforeNotify();

            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "invalidate reset size = " + size);

            //设置数据，集合为空，或小于分页数，关闭加载更多
            if (size == 0) {

                if (mSwipeView != null) {

                    if (adapter.getItemCount() > 0)
                        mSwipeView.setEnabled(mPullRefreshEnable);
                    else
                        mSwipeView.setEnabled(false);
                }

                getRecyclerView().setLoadMoreEnable(false);
            } else {

                if (mSwipeView != null)
                    mSwipeView.setEnabled(mPullRefreshEnable);

                if (mLoadMoreEnable)
                    getRecyclerView().setLoadMoreEnable(mLoadMoreIsStrictMode ? size > 0 : size >= mPageLimit);
                else
                    getRecyclerView().setLoadMoreEnable(false);
            }

            adapter.notifyDataSetChanged();

            mCurrentPageIndex = mPageStartIndex;
        }

        //卡片布局loadmore，暂时注释，详见最底下
        //setLoadMoreFooterViewEnable(mXlv.isPullLoadEnable());
    }

    protected boolean onRefreshRecyclerViewCheckLoadMoreEnable(List<?> result) {

        int size = CollectionUtil.size(result);
        return mLoadMoreIsStrictMode ? size > 0 : size >= mPageLimit;
    }

    protected void invalidateContentRefreshRecyclerViewDataSetChangedBeforeNotify() {

    }

    /**
     * 默认实现
     *
     * @param params
     * @return
     */
    @Override
    protected HttpFrameParams getHttpParamsOnFrameExecute(Object... params) {

        return getPageHttpParams(getPageStartIndex(), getPageLimit());
    }

    /**
     * nextPageStartIndex == currentPageIndex 肯定是SwipeRefresh刷新
     * 其他则是加载更多获取网络请求参数
     *
     * @param nextPageStartIndex
     * @param pageLimit
     * @return
     */
    protected abstract HttpFrameParams getPageHttpParams(int nextPageStartIndex, int pageLimit);

    public void setOnTaskFinish(OnTaskFinish onTaskFinish) {
        this.onTaskFinish = onTaskFinish;
    }

    public interface OnTaskFinish {

        public void onTaskFinish();
    }
}
