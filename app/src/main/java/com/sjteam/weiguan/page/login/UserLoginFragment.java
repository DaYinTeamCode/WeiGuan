package com.sjteam.weiguan.page.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.app.AppConfig;
import com.sjteam.weiguan.page.aframe.HttpFrameFragment;
import com.sjteam.weiguan.wxapi.WXEventListner;
import com.sjteam.weiguan.wxapi.manager.WXManagerHandler;
import com.sjteam.weiguan.wxapi.manager.WeChatUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

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

    /***
     *   微信授权登录
     */
    @OnClick(R.id.tvLogin)
    public void onLogin() {

        // 微信回调
        WXManagerHandler.getInstance().register(new WXEventListner() {

            @Override
            public void onResp(BaseResp baseResp) {

                if (baseResp instanceof SendAuth.Resp) {

                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    if (resp != null && !TextUtil.isEmpty(resp.code)) {

                        // 微信授权成功回调
                        LogMgr.i("UserLoginFragment", resp.code + " " + resp.country);
                    }
                }
            }

            @Override
            public void onReq(BaseReq baseReq) {

            }
        });
        /*** 微信授权 */
        WeChatUtils.authWeChat(getActivity(), AppConfig.wxKey);
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
