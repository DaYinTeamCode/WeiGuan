package com.sjteam.weiguan.page.video.detail;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidex.util.CollectionUtil;
import com.androidex.util.DensityUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.util.ViewUtil;
import com.androidex.widget.rv.lisn.item.OnExRvItemViewClickListener;
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * 视频详情页Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class VideoDetailFragment extends CpHttpFrameXrvFragment<FeedsVideoListResult>
        implements SqkbSwipeRefreshLayout.OnRefreshListener, StatRecyclerViewNewAttacher.DataItemListener, OnExRvItemViewClickListener {

    private static IjkVideoView mIjkVideoView;
    private VideoController mVideoController;
    private int mCurrentPosition;
    private VideoDetailAdapter mVideoDetailAdapter;
    private boolean mIsPullRefresh;
    private static ImageView mIvVideoPlay;
    private FrameLayout mFlController;

    private List<Object> mFeedsVideoResult;
    private int mPostion;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentSwipeRefreshRecyclerView();
        setPageLimit(10);
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
        /*** 设置标题栏为透明状态 */
        getTitleView().setBackgroundResource(R.color.cp_text_transparent);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mIjkVideoView != null) {

            mIjkVideoView.setVideoController(null);
            mIjkVideoView.release();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (mIjkVideoView != null && !ViewUtil.isShow(mIvVideoPlay)) {

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

            if (mIjkVideoView != null && !ViewUtil.isShow(mIvVideoPlay)) {

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

        mPostion = getArgumentInt("postion");
        mFeedsVideoResult = (List<Object>) getArgumentSerializable("feedsVideoResult");
    }

    @Override
    protected void initTitleView() {

        /*** 设置标题栏为透明状态 */
        getTitleView().setBackgroundResource(R.color.cp_text_transparent);
        addTitleLeftImageView(R.mipmap.ic_title_bar_back_white, v -> finishActivity());
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

        getSwipeView().setProgressViewEndTarget(true, DensityUtil.dip2px(60) + getTitleViewHeight());
        setDisabledImageResId(R.mipmap.ic_page_tip_data_empty);
        setDisabledTextResId(R.string.common_data_none);

        initIjkVideoWidget();
        initVideoAdapter();
        initRecycleViewWidget();
        checkVideoResult();
    }

    /***
     *  初始化播放器组件
     */
    private void initIjkVideoWidget() {

        mIjkVideoView = new IjkVideoView(getActivity());
        mIjkVideoView.setLooping(true);
        mIjkVideoView.setPlayOnMobileNetwork(true);
        mVideoController = new VideoController(getActivity());
        mIjkVideoView.setVideoController(mVideoController);
    }

    /***
     *  初始化视频适配器
     */
    private void initVideoAdapter() {

        mVideoDetailAdapter = new VideoDetailAdapter(getActivity());
        mVideoDetailAdapter.setOnExRvItemViewClickListener(this);
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

    /***
     * 校验视频数据
     */
    private void checkVideoResult() {

        if (CollectionUtil.isEmpty(mFeedsVideoResult)) {

            executeFrameImpl();
        } else if (mVideoDetailAdapter != null && getRecyclerView() != null) {

            mVideoDetailAdapter.setData(mFeedsVideoResult);
            getRecyclerView().scrollToPosition(mPostion);
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

        //自动播放第一条
        startPlay(0);
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
    public void onExRvItemViewClick(View view, int dataPos) {

        Object object = mVideoDetailAdapter.getDataItem(dataPos);

    }

    @Override
    public void onRecyclerViewDataItemStatShow(int dataPos) {

//        if (dataPos == 0) {
//
//            //自动播放第一条
//            startPlay(0);
//        }

        startPlay(dataPos);
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
            if (itemView == null) {

                return;
            }
            ExRvItemViewHolderBase childViewHolder = getRecyclerView().getChildViewHolder(itemView);
            if (childViewHolder instanceof VideoDetailViewHolder) {

                VideoDetailViewHolder viewHolder = (VideoDetailViewHolder) childViewHolder;
                FrameLayout frameLayout = viewHolder.itemView.findViewById(R.id.container);
                mVideoController.getThumb().setImageUriByLp(feedsVideoResult.getShowUrls());

                CardView cardView = new CardView(getActivity());
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
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

                mIvVideoPlay = viewHolder.itemView.findViewById(R.id.ivPlay);
                if (ViewUtil.isShow(mIvVideoPlay)) {

                    ViewUtil.hideView(mIvVideoPlay);
                }
                mFlController = viewHolder.itemView.findViewById(R.id.flController);
                onControllerWidget(mFlController);
            }
        }
    }

    /***
     *
     * @param frameLayout
     */
    private void onControllerWidget(FrameLayout frameLayout) {

        if (frameLayout == null) {

            return;
        }

        frameLayout.setOnClickListener(v -> {

            if (mIjkVideoView == null || mIvVideoPlay == null) {
                return;
            }
            if (mIjkVideoView.isPlaying()) {

                mIjkVideoView.pause();
                ViewUtil.showView(mIvVideoPlay);
            } else {

                ViewUtil.hideView(mIvVideoPlay);
                mIjkVideoView.resume();
            }
        });
    }

    /***
     *   视频详情页
     *
     * @param context
     * @return
     */
    public static VideoDetailFragment newInstance(Context context, List<FeedsVideoResult> feedsVideoResults, int postion) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("feedsVideoResult", (Serializable) feedsVideoResults);
        bundle.putInt("postion", postion);
        return (VideoDetailFragment) Fragment.instantiate(context, VideoDetailFragment.class.getName(), bundle);
    }
}
