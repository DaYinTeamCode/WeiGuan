package com.sjteam.weiguan.page.aframe.viewer;

import com.androidex.util.DeviceUtil;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpHttpFrameXrvFragment;
import com.sjteam.weiguan.page.aframe.presenter.CpHttpFrameXrvPresenter;

public abstract class CpHttpFrameXrvFragmentViewer<T, PRESENTER extends CpHttpFrameXrvPresenter> extends CpHttpFrameXrvFragment<T> {

    private PRESENTER mPresenter;

    @Override
    protected void initDataPre() {
        super.initDataPre();
        onInitPresenter();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        callbackPresenterDetached();
    }

    /**
     * 默认返空，不要求子类强制实现
     *
     * @return
     */
    private void onInitPresenter() {

        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.onAttachedToViewer();
        }
    }

    protected PRESENTER initPresenter() {

        return null;
    }

    /**
     * 回调控制器页面销毁
     */
    private void callbackPresenterDetached() {

        if (mPresenter != null) {

            mPresenter.onDetachedFromViewer();
        }
    }

    protected PRESENTER getPresenter() {

        return mPresenter;
    }

    @Override
    protected HttpFrameParams getPageHttpParams(int nextPageStartIndex, int pageLimit) {
        return null;
    }

    /**
     * 页面加载
     */
    protected void loadPageFrame(Object... params) {

        if (DeviceUtil.isNetworkEnable()) {

            switchLoadingOnFrameRefresh();
            if (getPresenter() != null) {

                getPresenter().loadPageFrame(true, params);
            }
        } else {

            showContentDisable();
        }
    }


    /**
     * 加载更多
     *
     * @param fromMan 加载更多是否人用户点击加载失败回调标识
     * @return false 展示加载失败界面，true 切换至loading 状态界面
     */
    @Override
    public boolean onLoadMore(boolean fromMan) {

        if (DeviceUtil.isNetworkEnable()) {

            if (getPresenter() != null) {

                getPresenter().loadPageMore(fromMan);
                return true;
            } else {

                return false;
            }
        } else {

            if (fromMan) {

                showToast(R.string.toast_network_none);
            }
            return false;
        }
    }

    /**
     * 页面下拉刷新
     */
    @Override
    public void onRefresh() {

        if (DeviceUtil.isNetworkEnable()) {

            if (getPresenter() != null) {

                getPresenter().loadPullRefresh(false);
            }
        } else {

            showToast(R.string.toast_network_none);
            if (getSwipeView() != null)
                getSwipeView().setRefreshing(false);
        }

    }

    /**
     * 页面初始化结果
     */
    public abstract void invalidateFrame(T data);

    /**
     * 页面初始化失败
     */
    public abstract void onLoadFrameFailed(int failedCode, String msg);

    /**
     * 加载更多结果
     *
     * @param data
     */
    public abstract void invalidateLoadMore(T data);

    /**
     * 加载更多失败
     */
    public void onLoadMoreFailed(int failedCode, String msg) {

        if (isFinishing())
            return;

        if (getSwipeView() != null && getSwipeView().isRefreshing())
            getSwipeView().setRefreshing(false);

        getRecyclerView().stopLoadMoreFail();
    }

    /**
     * 下拉刷新结果
     *
     * @param data
     */
    public abstract void invalidatePullRefresh(T data);

    /**
     * 下拉刷新失败
     */
    public abstract void onRefreshFailed(int failedCode, String msg);
}
