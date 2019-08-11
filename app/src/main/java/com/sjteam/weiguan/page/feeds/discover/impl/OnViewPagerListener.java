package com.sjteam.weiguan.page.feeds.discover.impl;

/**
 * 用于ViewPagerLayoutManager的监听
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/9 9:04 PM
 */
public interface OnViewPagerListener {

    /*初始化完成*/
    void onInitComplete();

    /*释放的监听*/
    void onPageRelease(boolean isNext, int position);

    /*选中的监听以及判断是否滑动到底部*/
    void onPageSelected(int position, boolean isBottom);

}
