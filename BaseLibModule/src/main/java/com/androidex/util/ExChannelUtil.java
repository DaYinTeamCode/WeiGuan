package com.androidex.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 渠道工具类
 * Created by yihaibin on 16/8/3.
 */
public class ExChannelUtil {

    private static String mDefaultChannel = TextUtil.TEXT_EMPTY;
    private static String mAppChannel = TextUtil.TEXT_EMPTY;

    /**
     * 设置默认渠道
     * @param defaultChannel
     */
    public static void setDefaultChannel(String defaultChannel){

        mDefaultChannel = TextUtil.filterNull(defaultChannel);
    }

    /**
     * 获取当前应用的渠道号
     * @return
     */
    public static String getChannel(Context context){

        if(!TextUtil.isEmpty(mAppChannel))
            return mAppChannel;

        String channel = getChannelFromApk(context, "cnl");
        if(TextUtil.isEmpty(channel)){

            return mDefaultChannel;
        }else{

            mAppChannel = channel;
            return mAppChannel;
        }
    }

    private static String getChannelFromApk(Context context, String channelKey) {

        String channel = TextUtil.TEXT_EMPTY;

        ZipFile zipfile = null;
        try {

            ApplicationInfo appinfo = context.getApplicationInfo();
            String sourceDir = appinfo.sourceDir;
            String key = "META-INF/" + channelKey;
            String ret = TextUtil.TEXT_EMPTY;

            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {

                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {

                    ret = TextUtil.filterNull(entryName);
                    break;
                }
            }

            String[] split = ret.split("_");
            if (split != null && split.length >= 2) {

                channel = ret.substring(split[0].length() + 1);
            }

        } catch (Throwable t) {

            if(LogMgr.isDebug())
                t.printStackTrace();

        } finally {

            try{
                if(zipfile != null)
                    zipfile.close();
            }catch(Exception e){

            }
        }

        return channel;
    }
}
