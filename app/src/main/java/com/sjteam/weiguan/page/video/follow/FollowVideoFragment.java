package com.sjteam.weiguan.page.video.follow;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.androidex.util.LogMgr;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpHttpFrameRvFragment;

/**
 * 关注视频Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class FollowVideoFragment extends CpHttpFrameRvFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_main_follow);
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
    public void onResume() {

        super.onResume();
        getTitleView().setBackgroundResource(R.color.app_white);
    }

    @Override
    protected void onSupportShowToUserChanged(boolean isShowToUser, int from) {

        super.onSupportShowToUserChanged(isShowToUser, from);

        if (LogMgr.isDebug()) {

            LogMgr.i("FollowVideoFragment", "onSupportShowToUserChanged" + isShowToUser);
        }

        if (isShowToUser) {

            getTitleView().setBackgroundResource(R.color.app_white);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

        getTitleView().setBackgroundResource(R.color.app_white);
    }

    @Override
    protected void initContentView() {

    }

    public static FollowVideoFragment newInstance(Context context) {

        return (FollowVideoFragment) Fragment.instantiate(context, FollowVideoFragment.class.getName());
    }
}
