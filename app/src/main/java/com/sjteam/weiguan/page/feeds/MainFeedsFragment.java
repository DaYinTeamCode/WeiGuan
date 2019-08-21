package com.sjteam.weiguan.page.feeds;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.androidex.util.DensityUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.VglpUtil;
import com.androidex.view.pager.indicator.TabStripIndicator;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;
import com.sjteam.weiguan.page.feeds.follow.FollowVideoFragment;
import com.sjteam.weiguan.page.main.event.VideoCateChangedEvent;
import com.sjteam.weiguan.page.feeds.adapter.VideoPageAdapter;
import com.sjteam.weiguan.page.feeds.discover.DiscoverVideoFragment;
import com.sjteam.weiguan.syncer.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * 视频Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainFeedsFragment extends HttpFrameFragment implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainFeedsFragment.class.getName();
    public static final int FROM_MAIN_FEEDS = 200;

    /*** Tab Indicator 指示器*/
    private TabStripIndicator mTabTipStripIndicator;
    private ViewPager mViewPager;
    private VideoPageAdapter mVideoPageAdapter;
    private int mCurPostiton;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_main_video);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    protected void onSupportShowToUserChanged(boolean isShowToUser, int from) {

        super.onSupportShowToUserChanged(isShowToUser, from);

        Fragment fragment = mVideoPageAdapter.getItem(mCurPostiton);
        if (fragment instanceof DiscoverVideoFragment) {

            DiscoverVideoFragment discoverVideoFragment = (DiscoverVideoFragment) fragment;
            discoverVideoFragment.onSupportShowToUserChanged(isShowToUser, FROM_MAIN_FEEDS);
        }else if(fragment instanceof FollowVideoFragment){

            FollowVideoFragment discoverVideoFragment = (FollowVideoFragment) fragment;
            discoverVideoFragment.onSupportShowToUserChanged(isShowToUser, FROM_MAIN_FEEDS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (hidden) {

            if (LogMgr.isDebug()) {

                LogMgr.i(TAG, "onHiddenChanged isHiddenToUser");
            }
        } else {


            if (LogMgr.isDebug()) {

                LogMgr.i(TAG, "onHiddenChanged isShowToUser");
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    protected HttpFrameParams getHttpParamsOnFrameExecute(Object... params) {

        return null;
    }

    @Override
    protected boolean invalidateContent(Object result) {

        return true;
    }

    @Override
    protected void initData() {

        mVideoPageAdapter = new VideoPageAdapter(getActivity(), getActivity().getSupportFragmentManager());
        mVideoPageAdapter.setData(getTabData());
    }

    @Override
    protected void initTitleView() {

        mTabTipStripIndicator = new TabStripIndicator(getActivity());
        mTabTipStripIndicator.setShouldExpand(true);
        mTabTipStripIndicator.setColorTabTextSelected(getResources().getColor(R.color.cp_tab_text_selected));
        mTabTipStripIndicator.setColorTabTextDefault(getResources().getColor(R.color.cp_tab_text_def));
        mTabTipStripIndicator.setTextSize(DensityUtil.dip2px(15));
        mTabTipStripIndicator.setIndicatorHeight(DensityUtil.dip2px(3));
        mTabTipStripIndicator.setIndicatorRoundRect(true);
        mTabTipStripIndicator.setTextBold(true);
        mTabTipStripIndicator.setIndicatorMarginBottom(DensityUtil.dip2px(6));
        mTabTipStripIndicator.setTabPaddingLeftRight(DensityUtil.dip2px(20));
        mTabTipStripIndicator.setUnderlineHoriPadding(DensityUtil.dip2px(20));
        addTitleMiddleView(mTabTipStripIndicator, VglpUtil.getLllpWM());
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

        mViewPager = (ViewPager) this.findViewById(R.id.vp);
        mViewPager.setAdapter(mVideoPageAdapter);
        mViewPager.addOnPageChangeListener(this);
        /*** 默认发现页面*/
        mViewPager.setCurrentItem(0);
        mTabTipStripIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        EventBusUtils.post(new VideoCateChangedEvent(position));
        mCurPostiton = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /***
     *  返回分类当前位置
     *
     * @return
     */
    public int getCurPostion() {

        return mCurPostiton;
    }

    /***
     * 获取指示器数据
     *
     * @return
     */
    private List<String> getTabData() {

        List<String> data = new ArrayList<>();
        data.add("发现");
        data.add("关注");
        return data;
    }

    public static MainFeedsFragment newInstance(Context context) {

        return (MainFeedsFragment) Fragment.instantiate(context, MainFeedsFragment.class.getName());
    }
}
