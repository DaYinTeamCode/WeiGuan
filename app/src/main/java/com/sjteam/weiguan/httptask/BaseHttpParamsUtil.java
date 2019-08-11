package com.sjteam.weiguan.httptask;

import com.androidex.util.DeviceUtil;
import com.androidex.util.TextUtil;
import com.ex.android.http.params.HttpTaskParams;
import com.sjteam.weiguan.constants.CommonConstant;
import com.sjteam.weiguan.page.login.prefs.AccountPrefs;

/**
 * 网络帮助类
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 3:39 PM
 */
public class BaseHttpParamsUtil implements HttpApi, CommonConstant {

    private static String sApiDomain = API_ONLINE_DOMAIN;

    /**
     * 设置api服务器地址
     *
     * @param apiDomain
     */
    public static void setApiDomain(String apiDomain) {

        BaseHttpParamsUtil.sApiDomain = apiDomain;
    }

    /**
     * 返回当前api服务器地址
     *
     * @return
     */
    public static String getApiDomain() {

        return sApiDomain;
    }

    public static HttpTaskParams getBaseGetHttpTaskParams(String url) {

        HttpTaskParams ht = HttpTaskParams.newGet(sApiDomain, url);
        setCommonParams(ht);
        return ht;
    }

    public static HttpTaskParams getBaseGetHttpTaskParams(String url, int page, int pageSize) {

        HttpTaskParams ht = getBaseGetHttpTaskParams(url);
        ht.addParam("pageNum", String.valueOf(page));
        ht.addParam("pageSize", String.valueOf(pageSize));
        return ht;
    }

    public static HttpTaskParams getBasePostHttpTaskParams(String url) {

        HttpTaskParams ht = HttpTaskParams.newPost(sApiDomain, url);
        setCommonParams(ht);
        return ht;
    }

    public static HttpTaskParams getBasePostHttpTaskParamsWithoutToken(String url) {

        HttpTaskParams ht = HttpTaskParams.newPost(sApiDomain, url);
        setCommonParams(ht);
        return ht;
    }

    public static HttpTaskParams getBasePostHttpTaskParams(String url, int page, int pageSize) {

        HttpTaskParams ht = HttpTaskParams.newPost(sApiDomain, url);
        setCommonParams(ht);
        ht.addParam("pageNum", String.valueOf(page));
        ht.addParam("pageSize", String.valueOf(pageSize));
        return ht;
    }

    protected static void setCommonParams(HttpTaskParams params) {

        params.addHeader("Content-Type", "application/json");
        addHeaderDeviceId(params, DeviceUtil.getDeviceId());
        addHeaderToken(params, AccountPrefs.getInstance().getAccountToekn());

        //键值对
        params.addParam("app_version", APP_VERSION_NAME);
        params.addParam("os_version", OS_VERSION);
        params.addParam("imei", IMEI);
        params.addParam("imei_2", IMEI2);
        params.addParam("android_id", ANDROID_ID);
        params.addParam("imsi", IMSI);
        params.addParam("device_id", CommonConstant.DEVICE_ID);
        params.addParam("device_info", DEVICE_MODEL);
        params.addParam("device_brand", DEVICE_BRAND);
        params.addParam("client_channel", CommonConstant.APK_CHANNEL_NAME);
        params.addParam("app_installtime", APP_INSTALL_TIME);
        params.addParam("network", NETWORK_TYPE);
        params.addParam("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.addParam("sw", SCREEN_WIDTH);
        params.addParam("sh", SCREEN_HEIGHT);
    }


    /**
     * header添加token
     *
     * @param params
     * @param token
     */
    public static void addHeaderToken(HttpTaskParams params, String token) {

        if (params != null) {

            if (!TextUtil.isEmptyTrim(token)) {

                params.addHeader("token", token);
            }
        }
    }

    /**
     * header添加deviceId
     *
     * @param params
     * @param deviceId
     */
    public static void addHeaderDeviceId(HttpTaskParams params, String deviceId) {

        if (params != null) {

            if (!TextUtil.isEmptyTrim(deviceId)) {

                params.addHeader("deviceId", deviceId);
            }
        }
    }
}
