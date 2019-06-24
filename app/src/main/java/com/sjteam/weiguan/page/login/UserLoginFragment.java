package com.sjteam.weiguan.page.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/24 11:30 PM
 */
public class UserLoginFragment extends HttpFrameFragment {

    private Unbinder unbinder;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.fragment_user_login);
    }

    @Override
    protected void onSupportShowToUserChanged(boolean isShowToUser, int from) {

        super.onSupportShowToUserChanged(isShowToUser, from);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

        unbinder = ButterKnife.bind(this, getExDecorView());
        getExDecorView().setBackgroundColor(0XFFFFFFFF);
    }

    @OnClick(R.id.ivBack)
    public void backClick() {

        finishActivity();
    }

    /*---------------------------------------- 网络监听回调 ----------------------------------------*/

    @Override
    protected HttpFrameParams getHttpParamsOnFrameExecute(Object... params) {

        return null;
    }

    @Override
    protected boolean invalidateContent(Object result) {

        return false;
    }

    /**
     * 实例化 Fra
     *
     * @param context
     * @return
     */
    public static UserLoginFragment newInstance(Context context) {

        Bundle bundle = new Bundle();
        return (UserLoginFragment) Fragment.instantiate(context, UserLoginFragment.class.getName(), bundle);
    }
}
