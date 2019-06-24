package com.sjteam.weiguan.page.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.androidex.statusbar.StatusBarManager;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;

/**
 * 用户登录页面
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/24 7:31 PM
 */

public class UserLoginActivity extends CpFragmentActivity {

    private Fragment mFragment;
    public static boolean isBindPhoneShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setCurPageSlidebackSupport(false);
        super.onCreate(savedInstanceState);
        isBindPhoneShowing = true;
        setContentLoginFragment();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initStatusBar() {

        StatusBarManager
                .getInstance()
                .initStatusbar(this, R.color.app_white);
    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        isBindPhoneShowing = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    private void setContentLoginFragment() {

        //只保留新登录样式
//        mFragment = UserNewLoginFra.newInstance(this, getIntent().getStringExtra("cash"), getIntent().getStringExtra("title"), (PingbackPage) getIntent().getSerializableExtra("page"));
        setContentFragment(mFragment);

    }

    @Override
    public void finish() {

        super.finish();
        overridePendingTransition(R.anim.push_exit_stop, R.anim.alpha_out);
    }

    private static void startActivity(Activity activity) {

        Intent intent = new Intent(activity, UserLoginActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.push_exit_stop);
    }
}
