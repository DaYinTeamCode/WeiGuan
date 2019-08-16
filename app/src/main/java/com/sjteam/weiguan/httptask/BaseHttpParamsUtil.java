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

        params.addHeader("Content-Type", "application/json;charset=UTF-8");
        params.addHeader("Acceot-Encoding", "gzip");
        params.addHeader("User-Agent", "okhttp/3.10.0.1");
        addHeaderDeviceId(params, DeviceUtil.getDeviceId());
        addHeaderToken(params, AccountPrefs.getInstance().getAccountToekn());

        //键值对
        params.addHeader("appVersion", APP_VERSION_NAME);
        params.addHeader("osVersion", OS_VERSION);
        params.addHeader("imei", IMEI);
        params.addHeader("imei2", IMEI2);
        params.addHeader("androidId", ANDROID_ID);
        params.addHeader("imsi", IMSI);
        params.addHeader("deviceInfo", DEVICE_MODEL);
        params.addHeader("deviceBrand", DEVICE_BRAND);
        params.addHeader("clientChannel", CommonConstant.APK_CHANNEL_NAME);
        params.addHeader("appInstalltime", APP_INSTALL_TIME);
        params.addHeader("network", NETWORK_TYPE);
        params.addHeader("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.addHeader("sw", SCREEN_WIDTH);
        params.addHeader("sh", SCREEN_HEIGHT);
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
