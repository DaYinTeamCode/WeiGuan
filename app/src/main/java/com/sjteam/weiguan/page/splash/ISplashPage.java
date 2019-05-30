package com.sjteam.weiguan.page.splash;


public interface ISplashPage {

    void setSplashPageListener(SplashPageListener listener);

    interface SplashPageListener {

        void onForwardMainActivity(String adUrl, boolean isAd);
    }
}
