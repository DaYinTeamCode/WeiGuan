package com.sjteam.weiguan.page.splash.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.androidex.util.TextUtil;
import com.jzyd.lib.activity.JzydFragment;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.splash.ISplashPage;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/30 7:03 PM
 */
public class SplashFragment extends JzydFragment implements ISplashPage {

    private final int HT_ADVERT = 1;

    private MainLauncherRunnable mMainActivityLaunchRunnable;
    private Handler mHandler = new Handler();
    private SplashPageListener mLisn;
//    private ImageView mIvChannel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.page_launcher_splash_fra);
//        mIvChannel = (ImageView) findViewById(R.id.ivChannel);
        startWaitRunnable();
    }

    @Override
    protected void initData() {

        mMainActivityLaunchRunnable = new MainLauncherRunnable();
    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

    }

    /**
     * 1秒之后判断广告是否可用，可用显示广告
     * 不可用则再等1秒进入主页
     */
    private void startWaitRunnable() {

        boolean needAd = getArgumentBoolean("needAd");
        mHandler.postDelayed(mSplashRunnable, 1000);
    }

    private class MainLauncherRunnable implements Runnable {

        @Override
        public void run() {

            run(TextUtil.TEXT_EMPTY, false);
        }

        public void run(String advertUrl, boolean isAd) {

            removeDelayRunnable();
            if (!isFinishing())
                callbackSplashPageForward(advertUrl, isAd);
        }
    }

    /**
     * 移除延迟消息
     */
    private void removeDelayRunnable() {

        mHandler.removeCallbacks(mAdRunnable);
        mHandler.removeCallbacks(mSplashRunnable);
    }

    private Runnable mSplashRunnable = new Runnable() {

        @Override
        public void run() {

            if (!isFinishing())
                mMainActivityLaunchRunnable.run();
        }
    };

    private Runnable mAdRunnable = new Runnable() {

        @Override
        public void run() {

            if (isFinishing())
                return;

            int delay = 1000;
            mHandler.postDelayed(mSplashRunnable, delay);
        }
    };


    /**
     * 回调监听器跳转主界面
     */
    private void callbackSplashPageForward(String adUrl, boolean isAd) {

        if (mLisn != null)
            mLisn.onForwardMainActivity(adUrl, isAd);
    }


    //***************** ISplashPage 接口实现 *****************


    @Override
    public void setSplashPageListener(ISplashPage.SplashPageListener listener) {

        mLisn = listener;
    }

    public static SplashFragment newInstance(Context context, boolean needAd) {

        Bundle bundle = new Bundle();
        bundle.putBoolean("needAd", needAd);
        return (SplashFragment) Fragment.instantiate(context, SplashFragment.class.getName(), bundle);
    }
}
