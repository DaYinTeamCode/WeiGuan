package com.sjteam.weiguan.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.androidex.context.ExApplication;
import com.sjteam.weiguan.utils.FrescoInitUtil;

/**
 * 应用程序入口
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/31 3:48 PM
 */
public class WgApp extends ExApplication {

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {

        super.onCreate();
        initAppFrame();
    }

    /***
     *
     *初始化应用程序
     */
    private void initAppFrame() {

        initAsyncImageLoader();
    }

    /***
     *  初始化图片库
     */
    private void initAsyncImageLoader() {

        FrescoInitUtil.initFrescoConfig(this);
    }
}
