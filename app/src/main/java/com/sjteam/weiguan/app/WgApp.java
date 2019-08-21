package com.sjteam.weiguan.app;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.androidex.context.ExApplication;
import com.androidex.imageloader.fresco.FrescoHelper;
import com.androidex.util.StorageUtil;
import com.ex.android.http.task.HttpTask;
import com.ex.android.http.task.HttpTaskClient;
import com.ex.umeng.UmengAgent;
import com.jzyd.lib.httptask.JzydJsonListener;
import com.meituan.android.walle.WalleChannelReader;
import com.sjteam.weiguan.BuildConfig;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.httptask.lisn.CpHttpTaskExeListener;
import com.sjteam.weiguan.httptask.lisn.CpHttpTaskNetworkListener;
import com.sjteam.weiguan.utils.FrescoInitUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

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
     *  低内存针对内存图片进行回收
     */
    @Override
    public void onLowMemory() {

        super.onLowMemory();
        FrescoHelper.clearMemoryCache();
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_LOW
                || level == TRIM_MEMORY_UI_HIDDEN) {

            FrescoHelper.clearMemoryCache();
        }
    }

    /***
     *
     *初始化应用程序
     */
    private void initAppFrame() {

        /*** 初始化图片库 */
        initAsyncImageLoader();
        /*** 初始化网络请求 */
        initHttpTask();
        /*** 初始化更新Apk配置*/
        initUpdateApkConfig();
        /*** 初始化Bugly */
        initBugly();
        /*** 初始化Umeng */
        initUmengSdk();
        /*** 初始化存储数据 */
        initStorage();
    }

    /***
     *  初始化图片库
     */
    private void initAsyncImageLoader() {

        FrescoInitUtil.initFrescoConfig(this);
    }

    /**
     * 初始化网络框架
     */
    private void initHttpTask() {

        try {
            //设置网络请求框架
            HttpTask.setHttpTaskClient(HttpTaskClient.newHttpTaskClient(10,
                    10 * 1000, null));//最大连接数3, 超时时间10s) 去除https
            HttpTask.setCacheDir(getAppCacheSubDir("httptask"));
            HttpTask.setHttpTaskNetworkListener(new CpHttpTaskNetworkListener());
            HttpTask.setDebug(BuildConfig.DEBUG);
            CpHttpTaskExeListener exeLisn = new CpHttpTaskExeListener();
            HttpTask.addHttpTaskExeListener(exeLisn);
            JzydJsonListener.addRespHandler(exeLisn);
        }catch (Exception ex){


        }
    }

    /**
     * 初始化BugLy 上报日志
     */
    private void initBugly() {

        /*** 设置是否为上报进程 */
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setUploadProcess(true);
        /*** 只记录apk channel */
        strategy.setAppChannel(getApkChannelName());
        // 初始化Bugly
        CrashReport.initCrashReport(this, AppConfig.buglyKey, false, strategy);
    }

    /***
     *  初始化Umeng SDK
     */
    private void initUmengSdk() {

        UmengAgent.turnOn(!BuildConfig.DEBUG);
        UmengAgent.setIsDeubg(BuildConfig.DEBUG);
        UmengAgent.startWithConfigure(this
                , AppConfig.umengKey, getApkChannelName());
    }

    /***
     *  初始化更新Apk配置
     *
     */
    private void initUpdateApkConfig() {

        Beta.autoInit = true;
        /**
         * true表示初始化时自动检查升级;
         * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
         */
        Beta.autoCheckUpgrade = true;

        /**
         * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
         */
        Beta.initDelay = 3 * 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源;
         */
        Beta.largeIconId = R.mipmap.ic_launcher;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
         */
        Beta.smallIconId = R.mipmap.ic_launcher;

        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.ic_launcher;
        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;

        Beta.enableHotfix = false;

        Bugly.init(this, AppConfig.buglyKey, BuildConfig.DEBUG);
    }

    /**
     * 初始化存储框架
     */
    private void initStorage() {

        StorageUtil.initAppHomeDir(this);
    }

    /**
     * 获取apk渠道
     *
     * @return
     */
    public static String getApkChannelName() {

        return WalleChannelReader.getChannel(WgApp.getContext(), "dev");
    }
}
