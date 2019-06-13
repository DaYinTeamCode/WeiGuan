package com.sjteam.weiguan.page.video;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.androidex.activity.ExFragment;
import com.androidex.adapter.ExFragmentPagerAdapter;
import com.sjteam.weiguan.page.video.discover.DiscoverVideoFragment;
import com.sjteam.weiguan.page.video.follow.FollowVideoFragment;

/**
 * 视频模块Adapter
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/13 3:39 PM
 */
public class VideoPageAdapter extends ExFragmentPagerAdapter<String> {

    public VideoPageAdapter(Context context, FragmentManager fmtMgr) {

        super(context, fmtMgr);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = DiscoverVideoFragment.newInstance(getContext());
                break;
            default:
                fragment = FollowVideoFragment.newInstance(getContext());
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return getDataItem(position);
    }

    public ExFragment getFragmentByPosition(ViewPager viewPager, int position) {

        return (ExFragment) instantiateItem(viewPager, position);
    }
}