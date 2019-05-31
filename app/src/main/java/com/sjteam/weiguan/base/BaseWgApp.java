package com.sjteam.weiguan.base;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import com.androidex.util.DeviceUtil;
import com.androidex.util.TextUtil;

import java.io.File;

/**
 * 基类Base App
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/21 6:41 PM
 */
public class BaseWgApp extends Application {

    private static Context mContext = null;

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = getApplicationContext();
    }

    public boolean checkProcess() {

        String processName = DeviceUtil.getProcessName(android.os.Process.myPid());
        return TextUtil.isEmpty(processName) || processName.equals(getPackageName());
    }

    public static Context getContext() {

        return mContext;
    }

    public static Resources getExResources() {

        return mContext.getResources();
    }

    public static int getDimensionPixelSize(int dimentResId) {

        return mContext.getResources().getDimensionPixelSize(dimentResId);
    }

    public static int getResColor(int colorResId) {

        return mContext.getResources().getColor(colorResId);
    }

    public static ContentResolver getExContentResolver() {

        return mContext.getContentResolver();
    }

    public static File getAppCacheDir() {

        return mContext.getCacheDir();
    }

    public static File getAppCacheSubDir(String subDirName) {

        File subDir = new File(getAppCacheDir(), subDirName);
        if (!subDir.exists())
            subDir.mkdirs();

        return subDir;
    }
}
