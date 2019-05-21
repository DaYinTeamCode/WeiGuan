package com.androidex.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.androidex.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * 日志工具类
 */
public class LogMgr {

    private static String mMainTag = "ExLogMgr";
    private static boolean mIsDebug = false;
    private static boolean mIsMock = false;  //mock开关状态
    private static boolean mIsUseMockData = false;    //on-下载状态 off-上传状态

    public static boolean isDebug() {

        return mIsDebug;
    }

    public static void setDebug(boolean debug) {

        mIsDebug = debug;
    }

    /**
     * mock 开关状态
     */
    public static boolean isMock() {

        return mIsMock;
    }

    public static void mockOn() {

        mIsMock = true;
    }

    public static void mockOff() {

        mIsMock = false;
    }

    /**
     * mock 上传和下载 off上传 on下载
     */
    public static boolean isUseMockData() {

        return mIsUseMockData;
    }

    public static void useMockData() {

        mIsUseMockData = true;
    }

    public static void notUseMockData() {

        mIsUseMockData = false;
    }

    public static void setMainTag(String tagName) {

        mMainTag = tagName;
    }

    public static String getMainTag() {

        return mMainTag;
    }

    public static void v(String log) {

        v(mMainTag, log);
    }

    public static void v(String tag, String log) {

        if (mIsDebug)
            Log.v(tag, log);
    }

    public static void d(String log) {

        d(mMainTag, log);
    }

    public static void d(String tag, String log) {

        if (mIsDebug)
            Log.d(tag, log);
    }

    public static void djson(String tag, Object object) {

        d(tag, "===================================================");
        d(tag, JSON.toJSONString(object, SerializerFeature.SortField, SerializerFeature.PrettyFormat));
    }

    public static void i(String log) {

        i(mMainTag, log);
    }

    public static void i(String tag, String log) {

        if (mIsDebug)
            Log.i(tag, log);
    }

    public static void w(String log) {

        if (mIsDebug)
            w(mMainTag, log);
    }

    public static void w(String tag, String log) {

        if (mIsDebug)
            Log.w(tag, log);
    }

    public static void e(String log) {

        if (mIsDebug)
            e(mMainTag, log);
    }

    public static void e(String tag, String log) {

        if (mIsDebug)
            Log.e(tag, log);
    }

    public static void d(String mMainTag, String log, Throwable throwable) {

        if (mIsDebug)
            Log.d(mMainTag, log, throwable);
    }

    public static void writeThrowableLog(Throwable t, String logFileName) {

        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        FileOutputStream fos = null;
        try {

            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            t.printStackTrace(printStream);
            String log = "---------------------------------\n";

            if (t.getCause() != null && t != t.getCause())
                log += t.getCause().getClass().getName() + ": " + t.getCause().getMessage() + "\n";

            log += new String(baos.toByteArray());

            fos = new FileOutputStream(new File(StorageUtil.getAppLogDir(), logFileName), true);
            fos.write(log.getBytes());

        } catch (Throwable e) {

            e.printStackTrace();
        } finally {

            IOUtil.closeOutStream(printStream);
            IOUtil.closeOutStream(baos);
            IOUtil.closeOutStream(fos);
        }
    }

    public static void writeTextLog(String text, String logFileName) {

        if (TextUtil.isEmpty(text))
            return;

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(new File(StorageUtil.getAppLogDir(), logFileName), true);
            fos.write(text.getBytes());

        } catch (Throwable t) {

            IOUtil.closeOutStream(fos);
        }
    }


    public static void writeThrowableLoxeg(String logInfo, String logFileName) {

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(new File(StorageUtil.getAppLogDir(), logFileName), true);
            logInfo = "---------------------------------\n" + logInfo;
            fos.write(logInfo.getBytes());

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            IOUtil.closeOutStream(fos);
        }
    }
}
