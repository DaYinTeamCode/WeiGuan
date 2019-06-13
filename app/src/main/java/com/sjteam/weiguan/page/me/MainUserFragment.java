package com.sjteam.weiguan.page.me;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;

/**
 * 用户中心Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainUserFragment extends HttpFrameFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_main_user);
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

    }

    @Override
    protected void initTitleView() {

        addTitleMiddleTextView("首页");
    }

    @Override
    protected void initContentView() {

    }

    public static MainUserFragment newInstance(Context context) {

        return (MainUserFragment) Fragment.instantiate(context, MainUserFragment.class.getName());
    }

}