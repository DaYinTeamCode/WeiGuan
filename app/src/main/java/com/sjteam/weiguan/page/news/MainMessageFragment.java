package com.sjteam.weiguan.page.news;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;

/**
 * 消息中心Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainMessageFragment extends HttpFrameFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_main_message);
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
    protected void onSupportShowToUserChanged(boolean isShowToUser, int from) {
        super.onSupportShowToUserChanged(isShowToUser, from);

        if (isShowToUser) {

            if (getExDecorView() != null) {

                getTitleView().setBackgroundResource(R.color.cp_page_bg);
                initStatusBar();
            }
        }
    }

    @Override
    protected void initTitleView() {

        TextView textView = addTitleMiddleTextView("全部消息");
        textView.setTextColor(0XFFFFFFFF);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        getTitleView().setBackgroundResource(R.color.cp_page_bg);
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

    }

    public static MainMessageFragment newInstance(Context context) {

        return (MainMessageFragment) Fragment.instantiate(context, MainMessageFragment.class.getName());
    }

}
