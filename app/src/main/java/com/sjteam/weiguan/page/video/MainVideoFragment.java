package com.sjteam.weiguan.page.video;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.androidex.util.DensityUtil;
import com.androidex.util.VglpUtil;
import com.androidex.view.pager.indicator.TabStripIndicator;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainVideoFragment extends HttpFrameFragment implements ViewPager.OnPageChangeListener {

    /*** Tab Indicator 指示器*/
    private TabStripIndicator mTabTipStripIndicator;
    private ViewPager mViewPager;
    private VideoPageAdapter mVideoPageAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_main_video);
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
        getTitleView().setBackgroundResource(R.color.app_white);
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

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    public static MainVideoFragment newInstance(Context context) {

        return (MainVideoFragment) Fragment.instantiate(context, MainVideoFragment.class.getName());
    }
}