package com.sjteam.weiguan.page.splash;

import android.os.Bundle;
import android.view.View;

import com.androidex.util.CompatUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.jzyd.lib.activity.JzydFragmentActivity;
import com.sjteam.weiguan.MainActivity;
import com.sjteam.weiguan.page.splash.fragment.SplashFragment;

/**
 * 启动也
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/30 7:33 PM
 */
public class SplashActivity extends JzydFragmentActivity implements
        ISplashPage.SplashPageListener {

    private boolean mFirstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setCurPageSlidebackSupport(false);
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .autoDarkModeEnable(true)
                .fitsSystemWindows(true)
                .keyboardEnable(false)
                .init();
        onBeforeSetContentFragment();
        setContentFragmentAndLaunchTask();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

    }

    private void setContentFragmentAndLaunchTask() {

        setContentFragmentByLaunchTime();
    }

    private void onBeforeSetContentFragment() {

//        hideNavitionBar();
    }

    /**
     * 隐藏底部导航栏
     */
    private void hideNavitionBar() {

        if (CompatUtil.isGreatThanOrEqualToIcsVersion()) {

            int uiFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(uiFlag);
        }
    }

    private void setContentFragmentByLaunchTime() {

        SplashFragment fra = SplashFragment.newInstance(this, !mFirstLaunch);
        fra.setSplashPageListener(this);
        setContentFragment(fra);
    }

    /**
     * 跳转activity
     */
    @Override
    public void onForwardMainActivity(String adUrl, boolean isAd) {

        MainActivity.startActivityForIndex(this);
        finish();
    }
}
