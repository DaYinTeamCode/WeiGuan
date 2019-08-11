package com.sjteam.weiguan.manager.permisssion.deviceid;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.androidex.util.TextUtil;


/**
 * 设备标识信息工具类
 */
public class IdentityUtil {

    /**
     * 优先获取imei号
     * imei号为空获取android id
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {

        try {
            String deviceId = TextUtil.filterNull(getIMEI(context));
            if (!TextUtil.isEmptyTrim(deviceId)) {
                return deviceId;
            }

            deviceId = TextUtil.filterNull(getAndroidId(context));
            if (!TextUtil.isEmptyTrim(deviceId)) {
                return deviceId;
            }
            return TextUtil.TEXT_EMPTY;
        } catch (Exception e) {
            return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {

        try {

            ContentResolver resolver = context.getContentResolver();
            String androidId = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID);
            return TextUtil.filterNull(androidId);

        } catch (Throwable t) {

        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取序列号
     *
     * @return
     */
    public static String getSN() {

        try {

            return Build.SERIAL;

        } catch (Throwable t) {

        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取设备 卡1 IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {

        return getIMEI(context, 0, true);
    }

    /**
     * 获取设备 卡2 IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI2(Context context) {

        return getIMEI(context, 1, false);
    }

    /**
     * 获取设备IMEI号
     *
     * @param index                卡槽索引，从0开始
     * @param useDefaultApiIfEmpty 如果拿不到IMEI，使用默认api获取imei号
     * @return
     */
    private static String getIMEI(Context context, int index, boolean useDefaultApiIfEmpty) {

        String imei = TextUtil.TEXT_EMPTY;
        try {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //26 api 开始，android 公开了这个函数
                imei = telephonyManager.getImei(index);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //21(5.0) - 25(7.1) getImei函数是隐藏的,但是在一些机型上也可以正常调用该函数:
                //4儿子可以正常调用该函数，但是返回为空
                //oppo手机可以正常调用该函数，也有返回值
                //为防止在一些机型上崩溃，还是try一下
                try {

                    imei = telephonyManager.getImei(index);

                } catch (Throwable t) {

                }
            }

            //默认取deviceId
            if (useDefaultApiIfEmpty && TextUtil.isEmpty(imei)) {

                imei = telephonyManager.getDeviceId();
            }

        } catch (Throwable e) {

        }

        //在一些设备上无imei号返回的是0，而不是空串
        //这里认为只要imei号大于1的长度，就认为是合法的
        if (TextUtil.size(imei) > 1)
            return imei;
        else
            return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取当前sim卡运营商imsi号
     *
     * @return
     */
    public static String getIMSI(Context context) {

        try {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = telephonyManager.getSubscriberId();
            return TextUtil.filterNull(imsi);

        } catch (Exception e) {

        }

        return TextUtil.TEXT_EMPTY;
    }
}
