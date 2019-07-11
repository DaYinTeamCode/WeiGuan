package com.androidex.util;

import android.net.Uri;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * url链接工具类
 * Created by yihaibin on 16/8/7.
 */
public class UrlUtil {

    /**
     * 在url后追加参数
     *
     * @param url
     * @param key
     * @param value
     * @return
     */
    public static String appendParam(String url, String key, String value) {

        url = TextUtil.filterNull(url);
        key = TextUtil.filterNull(key);
        value = TextUtil.filterNull(value);

        if (url.contains("?")) {

            url = url + "&" + key + "=" + value;
        } else {

            url = url + "?" + key + "=" + value;
        }

        return url;
    }

    /**
     * 在url后追加参数
     *
     * @param url
     * @param key
     * @param value
     * @return
     */
    public static String appendParamIfNotExists(String url, String key, String value) {

        return appendParam(url, key, value, true);
    }

    /**
     * 在url后追加encode参数
     *
     * @param url
     * @param key
     * @param value
     * @return
     */
    public static String appendParam(String url, String key, String value, boolean checkExists) {

        url = TextUtil.filterNull(url);
        if (checkExists && url.contains(key + "=")) {
            return url;
        }

        key = TextUtil.filterNull(key);
        value = TextUtil.filterNull(value);

        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(url.contains("?") ? '&' : '?');
        sb.append(key);
        sb.append('=');
        sb.append(value);
        return sb.toString();
    }

    /**
     * url encode
     *
     * @param value
     * @return
     */
    public static String encode(String value) {

        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * url decode
     *
     * @param value
     * @return
     */
    public static String decode(String value) {

        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取url最后一个路径
     *
     * @param url
     * @return
     */
    public static String getLastSegment(String url) {

        try {

            Uri uri = Uri.parse(url);
            return uri.getLastPathSegment();

        } catch (Exception e) {

            return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * 替换Host域名
     *
     * @param url
     * @param newHost
     * @return
     */
    public static String replaceHost(String url, String newHost) {

        if (TextUtil.isEmpty(url) || TextUtil.isEmpty(newHost))
            return url;

        try {

            String urlHost = getAllHost(url);
            return url.replace(urlHost, newHost);
        } catch (Exception e) {

            return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * 判断Url链接的Host
     *
     * @param linkUrl
     * @param host
     * @return
     */
    public static boolean isSameHost(String linkUrl, String host) {

        try {

            String urlHost = getAllHost(linkUrl);
            if (TextUtil.isEmpty(urlHost))
                return false;

            return urlHost.startsWith(host);
        } catch (Exception e) {

            return false;
        }
    }

    public static String getAllHost(String url) {

        try {

            Uri uri = Uri.parse(url);
            return String.format("%s://%s", uri.getScheme(), uri.getHost());
        } catch (Exception ex) {

            return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * get请求url中是否包含key参数
     *
     * @param url 请求url
     * @param key 参数名
     * @return 如果不存参数名，或者参数值为空 返回true 否则为false
     */
    public static boolean containsGetParam(String url, String key) {

        try {

            Uri uri = Uri.parse(url);
            return !TextUtil.isEmpty(uri.getQueryParameter(key));

        } catch (Exception e) {

            return false;
        }
    }

    /***
     *  获取Host
     * @param url
     * @return
     */
    public static String getPath(String url) {

        try {

            Uri uri = Uri.parse(url);

            return String.format("%s://%s%s", uri.getScheme(), uri.getHost(), uri.getPath());
        } catch (Exception ex) {

            return TextUtil.TEXT_EMPTY;
        }
    }
}
