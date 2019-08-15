package com.sjteam.weiguan.page.video;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.CollectionUtil;
import com.androidex.util.DensityUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.widget.rv.lisn.item.OnExRvItemViewClickListener;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.jzyd.lib.refresh.sqkbswipe.SqkbSwipeRefreshLayout;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpHttpFrameXrvFragment;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoListResult;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoResult;
import com.sjteam.weiguan.page.feeds.discover.utils.FeedsVideoHttpUtils;
import com.sjteam.weiguan.page.video.adapter.MainVideoAdapter;
import com.sjteam.weiguan.page.video.decoration.VideoDcCardGridDecoration;
import com.sjteam.weiguan.page.video.detail.VideoDetailActivity;
import com.sjteam.weiguan.stat.StatRecyclerViewNewAttacher;
import com.sjteam.weiguan.utils.CpResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainVideoFragment extends CpHttpFrameXrvFragment<FeedsVideoListResult> implements SqkbSwipeRefreshLayout.OnRefreshListener
        , StatRecyclerViewNewAttacher.DataItemListener, OnExRvItemViewClickListener {

    private boolean mIsPullRefresh;
    private MainVideoAdapter mMainVideoAdapter;
    private GridLayoutManager mRvGridLayoutMgr;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentSwipeRefreshRecyclerView();
        setPageLimit(10);
        executeFrameImpl();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

        TextView textView = addTitleMiddleTextView("小视频");
        textView.setTextColor(0XFF0C87F5);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        getTitleView().setBackgroundResource(R.color.app_white);
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

        getSwipeView().setProgressViewEndTarget(true, DensityUtil.dip2px(40) + getTitleViewHeight());
        setDisabledImageResId(R.mipmap.ic_page_tip_data_empty);
        setDisabledTextResId(R.string.common_data_none);

        mMainVideoAdapter = new MainVideoAdapter();
        mMainVideoAdapter.setCardWidth(VideoDcCardGridDecoration.ITEM_WIDTH);
        mMainVideoAdapter.setOnExRvItemViewClickListener(this);
        mRvGridLayoutMgr = new GridLayoutManager(getContext(), 2);
        getRecyclerView().setLayoutManager(mRvGridLayoutMgr);
        getRecyclerView().addItemDecoration(new VideoDcCardGridDecoration());
        StatRecyclerViewNewAttacher statRecyclerViewNewAttacher = new StatRecyclerViewNewAttacher(getRecyclerView());
        statRecyclerViewNewAttacher.setDataItemListener(this);
        getRecyclerView().addOnChildAttachStateChangeListener(statRecyclerViewNewAttacher);
        getRecyclerView().setAdapter(mMainVideoAdapter);

        setLoadMoreNoDataTipAttr("已经到底了",
                R.mipmap.ic_brand_index_list_item_title_pop_left,
                R.mipmap.ic_brand_index_list_item_title_pop_right);
        /** 修改内容的头部底部，将标题栏距离和底部tab距离留出来 */
        setContentViewMargin(CpResUtil.getTitleBarHeight() + StatusBarManager.getInstance().getStatusbarHeight(getActivity())
                , CpResUtil.getMainTabHeight());
    }

    /***
     *  修改内容的头部底部，将标题栏距离和底部tab距离留出来
     *
     * @param top
     * @param btm
     */
    protected void setContentViewMargin(int top, int btm) {

        if (getSwipeView() != null) {

            ViewGroup.MarginLayoutParams vgmlp = (ViewGroup.MarginLayoutParams) getSwipeView().getLayoutParams();
            vgmlp.topMargin = top;
            vgmlp.bottomMargin = btm;
            getSwipeView().requestLayout();
        }
    }

    /*------------------------------------ 网络相关请求 --------------------------------------------*/

    protected void executeFrameImpl() {

        executeFrameRefresh();
    }

    protected void executePullImpl() {

        executeFrameRefresh();
    }

    protected void executeFailedRetryImpl() {

        executeFrameRefresh();
    }

    @Override
    protected HttpFrameParams getPageHttpParams(int nextPageStartIndex, int pageLimit) {

        return new HttpFrameParams(FeedsVideoHttpUtils.getFeedsVideoParams(nextPageStartIndex, pageLimit)
                , FeedsVideoListResult.class);
    }

    /***
     *   刷新Loading
     */
    @Override
    protected void switchLoadingOnFrameRefresh() {

        if (mIsPullRefresh) {

            switchContent();
        } else {

            super.switchLoadingOnFrameCache();
        }
    }

    @Override
    protected void switchFailedOnFrameRefresh(int failedCode, String msg) {

        if (mIsPullRefresh) {

            if (getSwipeView() != null) {

                getSwipeView().setRefreshing(false);
            }
        } else {

            super.switchFailedOnFrameRefresh(failedCode, msg);
        }
    }

    /*-------------------------------下拉刷新相关回调-----------------------------*/

    @Override
    protected void onTipViewClick() {

        if (DeviceUtil.isNetworkDisable()) {

            showToast(R.string.toast_network_none);
        } else {

            executeFailedRetryImpl();
        }
    }

    /*-------------------------------下拉刷新相关回调-----------------------------*/

    @Override
    public void onRefresh() {

        if (DeviceUtil.isNetworkEnable()) {

            mIsPullRefresh = true;
            executePullImpl();
        } else {

            showToast(R.string.toast_network_none);
            if (getSwipeView() != null) {

                getSwipeView().setRefreshing(false);
            }
        }
    }

    @Override
    public void onRefreshCompleted() {

        mIsPullRefresh = false;
    }

    @Override
    protected boolean invalidateContent(FeedsVideoListResult result) {

        if (getSwipeView() != null) {

            getSwipeView().setRefreshing(false);
        }

        if (result == null) {

            return false;
        }

        if (CollectionUtil.isEmpty(result.getList())) {

            return false;
        }
        super.invalidateContent(result);
        return true;
    }

    @Override
    protected void onLoadMoreFailed(int failedCode, String msg) {

        super.onLoadMoreFailed(failedCode, msg);
    }

    /***
     * 刷新列表数据
     *
     * @param result
     * @return
     */
    @Override
    protected List<?> invalidateContentGetList(FeedsVideoListResult result) {

        return result != null ? result.getList() : null;
    }

    @Override
    protected void showContent() {

        super.showContent();
    }

    @Override
    public void onRecyclerViewDataItemStatShow(int dataPos) {

    }

    @Override
    public void onExRvItemViewClick(View view, int dataPos) {

        Object object = mMainVideoAdapter.getDataItem(dataPos);

        if (object instanceof FeedsVideoResult) {

            List<FeedsVideoResult> feedsVideoResults = (ArrayList) mMainVideoAdapter.getData();
            VideoDetailActivity.startActivity(getActivity(), feedsVideoResults, dataPos);
        }
    }

    /**
     * @param context
     * @return
     */
    public static MainVideoFragment newInstance(Context context) {

        return (MainVideoFragment) Fragment.instantiate(context, MainVideoFragment.class.getName());
    }
}
