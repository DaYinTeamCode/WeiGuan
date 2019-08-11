package com.sjteam.weiguan.page.feeds.discover;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.androidex.util.CollectionUtil;
import com.androidex.util.DensityUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.jzyd.lib.refresh.sqkbswipe.SqkbSwipeRefreshLayout;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpHttpFrameXrvFragment;
import com.sjteam.weiguan.page.feeds.discover.adapter.VideoDetailAdapter;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoListResult;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoResult;
import com.sjteam.weiguan.page.feeds.discover.impl.OnViewPagerListener;
import com.sjteam.weiguan.page.feeds.discover.impl.ViewPagerLayoutManager;
import com.sjteam.weiguan.page.feeds.discover.utils.FeedsVideoHttpUtils;
import com.sjteam.weiguan.page.feeds.discover.viewholder.VideoDetailViewHolder;
import com.sjteam.weiguan.stat.StatRecyclerViewNewAttacher;
import com.sjteam.weiguan.widget.video.VideoController;

import java.util.List;

/**
 * 发现视频Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class DiscoverVideoFragment extends CpHttpFrameXrvFragment<FeedsVideoListResult>
        implements SqkbSwipeRefreshLayout.OnRefreshListener, StatRecyclerViewNewAttacher.DataItemListener {

    private IjkVideoView mIjkVideoView;
    private VideoController mVideoController;
    private int mCurrentPosition;
    private VideoDetailAdapter mVideoDetailAdapter;
    private boolean mIsPullRefresh;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentSwipeRefreshRecyclerView();
        getExDecorView().setBackgroundColor(0X00000000);
        getExDecorView().setPadding(0, 0, 0, DensityUtil.dip2px(48f));
        setPageLimit(30);
        executeFrameImpl();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (mIjkVideoView != null) {

            mIjkVideoView.pause();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        /*** 设置标题栏为透明状态 */
        getTitleView().setBackgroundResource(R.color.cp_text_transparent);
        if (mIjkVideoView != null) {

            mIjkVideoView.resume();
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mIjkVideoView != null) {

            mIjkVideoView.release();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {

            if (mIjkVideoView != null) {

                mIjkVideoView.pause();
            }
        } else {

            if (mIjkVideoView != null) {

                mIjkVideoView.resume();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (mIjkVideoView != null) {

                mIjkVideoView.resume();
            }
        } else {

            if (mIjkVideoView != null) {

                mIjkVideoView.pause();
            }
        }
    }

    @Override
    public void onSupportShowToUserChanged(boolean isShowToUser, int from) {

        super.onSupportShowToUserChanged(isShowToUser, from);
        if (isShowToUser) {

            if (mIjkVideoView != null) {

                mIjkVideoView.resume();
            }
        } else {

            if (mIjkVideoView != null) {

                mIjkVideoView.pause();
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

        getTitleView().setBackgroundResource(R.color.cp_text_transparent);
    }

    @Override
    protected void initContentView() {

        getSwipeView().setProgressViewEndTarget(true, DensityUtil.dip2px(60) + getTitleViewHeight());
        setDisabledImageResId(R.mipmap.ic_page_tip_data_empty);
        setDisabledTextResId(R.string.common_data_none);

        initIjkVideoWidget();
        initVideoAdapter();
        initRecycleViewWidget();
    }

    /***
     *  初始化播放器组件
     */
    private void initIjkVideoWidget() {

        mIjkVideoView = new IjkVideoView(getActivity());
        mIjkVideoView.setLooping(true);
        mVideoController = new VideoController(getActivity());
        mIjkVideoView.setVideoController(mVideoController);
    }

    /***
     *  初始化视频适配器
     */
    private void initVideoAdapter() {

        mVideoDetailAdapter = new VideoDetailAdapter(getActivity());

    }

    /***
     *  初始化列表组件
     */
    private void initRecycleViewWidget() {

        ViewPagerLayoutManager layoutManager = new ViewPagerLayoutManager(getActivity(), OrientationHelper.VERTICAL);
        getRecyclerView().setLayoutManager(layoutManager);
        StatRecyclerViewNewAttacher statRecyclerViewNewAttacher = new StatRecyclerViewNewAttacher(getRecyclerView());
        statRecyclerViewNewAttacher.setDataItemListener(this);
        getRecyclerView().addOnChildAttachStateChangeListener(statRecyclerViewNewAttacher);
        getRecyclerView().setAdapter(mVideoDetailAdapter);
        layoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

                if (mCurrentPosition == position) {

                    mIjkVideoView.release();
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {

                if (mCurrentPosition == position) {

                    return;
                }
                startPlay(position);
                mCurrentPosition = position;
            }
        });
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

        if (DeviceUtil.isNetworkDisable())
            showToast(R.string.toast_network_none);
        else
            executeFailedRetryImpl();
    }

    /*-------------------------------下拉刷新相关回调-----------------------------*/

    @Override
    public void onRefresh() {

        if (DeviceUtil.isNetworkEnable()) {

            mIsPullRefresh = true;
            executePullImpl();
        } else {

            showToast(R.string.toast_network_none);
            if (getSwipeView() != null)
                getSwipeView().setRefreshing(false);
        }
    }

    @Override
    public void onRefreshCompleted() {

        mIsPullRefresh = false;

        //自动播放第一条
        startPlay(0);
    }

    @Override
    protected boolean invalidateContent(FeedsVideoListResult result) {

        if (getSwipeView() != null) {

            getSwipeView().setRefreshing(false);
        }

        if (result == null)
            return false;

        if (CollectionUtil.isEmpty(result.getList()))
            return false;

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

        if (dataPos == 0) {

            //自动播放第一条
            startPlay(0);
        }
    }

    /***
     *  开始播放视频
     *
     * @param position
     */
    private void startPlay(int position) {

        Object obj = mVideoDetailAdapter.getDataItem(position);

        if (obj instanceof FeedsVideoResult) {

            FeedsVideoResult feedsVideoResult = (FeedsVideoResult) obj;
            View itemView = getRecyclerView().getChildAt(0);
            ExRvItemViewHolderBase childViewHolder = getRecyclerView().getChildViewHolder(itemView);
            if (childViewHolder instanceof VideoDetailViewHolder) {

                VideoDetailViewHolder viewHolder = (VideoDetailViewHolder) childViewHolder;
                FrameLayout frameLayout = viewHolder.itemView.findViewById(R.id.container);

                mVideoController.getThumb().setImageUriByLp(feedsVideoResult.getShowUrls());

                CardView cardView = new CardView(getActivity());
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
                cardView.setBackgroundColor(0X00000000);
                cardView.setRadius(DensityUtil.dip2px(8f));
                frameLayout.addView(cardView, layoutParams);
                ViewParent parent = mIjkVideoView.getParent();
                if (parent instanceof CardView) {

                    ((CardView) parent).removeView(mIjkVideoView);
                }
                cardView.addView(mIjkVideoView);
                mIjkVideoView.setUrl(feedsVideoResult.getOpenUrls());
                mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
                mIjkVideoView.start();
            }
        }
    }

    /***
     *   发现视频
     *
     * @param context
     * @return
     */
    public static DiscoverVideoFragment newInstance(Context context) {

        return (DiscoverVideoFragment) Fragment.instantiate(context, DiscoverVideoFragment.class.getName());
    }
}
