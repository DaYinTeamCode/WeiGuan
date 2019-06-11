package com.sjteam.weiguan.app;

import android.content.Context;
import android.os.SystemClock;
import android.support.multidex.MultiDex;

import com.androidex.context.ExApplication;
import com.ex.android.http.task.HttpTask;
import com.ex.android.http.task.HttpTaskClient;
import com.jzyd.lib.httptask.JzydJsonListener;
import com.sjteam.weiguan.BuildConfig;
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
        initHttpTask();
    }

    /***
     *  初始化图片库
     */
    private void initAsyncImageLoader() {

        FrescoInitUtil.initFrescoConfig(this);
    }

    /**
     * 初始化网络框架、图片框架
     */
    private void initHttpTask() {

        //设置网络请求框架 最大连接数10, 超时时间10s) 去除https
        HttpTask.setHttpTaskClient(HttpTaskClient.newHttpTaskClient(10,
                10 * 1000, null));
        HttpTask.setCacheDir(getAppCacheSubDir("httptask"));
    }
}
