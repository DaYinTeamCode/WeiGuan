package com.sjteam.weiguan.scheme;

import android.app.Activity;
import android.net.Uri;

import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.sjteam.weiguan.page.web.activity.BrowserActivity;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:16 PM
 */
public class CpActSchemeLaunchUtil implements SchemeConstants {


//    /**
//     * 跳转微信授权
//     *
//     * @param activity
//     * @param oper
//     * @param page
//     */
//    private static void startActivityByWxAuth(final Activity activity, final Oper oper, final PingbackPage page, boolean fromTempAccountCheck) {
//
//        CpDialogUtil.showWxAuthTipDialog(activity, CpApp.getOnLineConfigMgr().getWxAuthTipTitle()
//                , CpApp.getOnLineConfigMgr().getWxAuthTipDescription(), new CpWxAuthTipDialog.Listener() {
//                    @Override
//                    public void onWechatBindSuccess() {
//
//                        startActivityByOper(activity, oper, page, fromTempAccountCheck);
//                    }
//                });
//    }

//    /**
//     * 小米消息推送
//     *
//     * @param activity
//     * @param msg
//     */
//    public static boolean startActivity(Activity activity, BasePushMessage msg, PingbackPage page) {
//
//        if (activity == null || msg == null)
//            return false;
//
//        return startActivityBySchemeUrl(activity, msg.getSchema(), msg.getTitle(), true, TextUtil.TEXT_EMPTY, page, null);
//    }

    /**
     * 打开url，如果本地拦截不了则用浏览器打开
     *
     * @param activity
     * @param url
     * @return
     */
    public static boolean startActivityBySchemeUrl(Activity activity, String url) {

        return startActivityBySchemeUrl(activity, url, TextUtil.TEXT_EMPTY, true, TextUtil.TEXT_EMPTY, null);
    }

    /**
     * 打开url
     *
     * @param activity
     * @param url
     * @param defBrowserOpen 如果原生拦截不了 true:则用浏览器打开 false:不响应
     * @return
     */
    public static boolean startActivityBySchemeUrl(Activity activity, String url, boolean defBrowserOpen) {

        return startActivityBySchemeUrl(activity, url, TextUtil.TEXT_EMPTY, defBrowserOpen, TextUtil.TEXT_EMPTY, null);
    }

    /**
     * 打开url
     *
     * @param activity
     * @param url
     * @param defBrowserOpen
     * @param lisn
     * @return
     */
    public static boolean startActivityBySchemeUrl(Activity activity, String url, boolean defBrowserOpen, CpActSchemeUrlListener lisn) {

        return startActivityBySchemeUrl(activity, url, TextUtil.TEXT_EMPTY, defBrowserOpen, TextUtil.TEXT_EMPTY, lisn);
    }

    /**
     * 拦截并打开需要处理的 scheme url
     *
     * @param activity
     * @param url
     * @return
     */
    private static boolean startActivityBySchemeUrl(Activity activity, String url, String title, boolean defBrowserOpen, String apiTraceId, CpActSchemeUrlListener lisn) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "startActivityBySchemeUrl url = " + url);

        if (TextUtil.isEmpty(url))
            return false;

        try {

            //检查是否需要拼接scheme
            url = appendSchemeIfNeed(TextUtil.filterTrim(url));

            //转换uri
            Uri uri = Uri.parse(url);
            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "startActivityBySchemeUrl scheme=" + uri.getScheme() + ", host=" + uri.getHost() + ", path=" + uri.getPath() + ", lastsegment=" + uri.getLastPathSegment());

            //检查是否是需要拦截的host
            String host = TextUtil.filterNull(uri.getHost());
            if (checkHost(host)) {

                //解析scheme, path, lastPathSegment
                String lastPathSegment = uri.getLastPathSegment();
                String path = parsePath(uri.getPath(), lastPathSegment);
                if (dispatchSchemeUrlStartActivity(activity, uri, url, path, lastPathSegment, title, apiTraceId, lisn)) {

                    return true;

                }

            } else {

                    return startBrowserActivityIfNeed(activity, url, defBrowserOpen);//外站链接默认都打开
            }

        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.e(simpleTag(), "parse scheme url error = " + e.getMessage());
        }

        return false;
    }

    /**
     * 做http协议判断, 使用app浏览器打开url
     *
     * @param activity
     * @param url
     * @param defOpen
     * @return
     */
    private static boolean startBrowserActivityIfNeed(final Activity activity, final String url, final boolean defOpen) {

        //因为app支持了sqkb://协议，所以要加http协议的判断
        if (isHttpScheme(url) && defOpen) {

            BrowserActivity.startActivity(activity, url);
            return true;
        } else {

            return false;
        }
    }

    private static boolean dispatchSchemeUrlStartActivity(final Activity activity, Uri uri, String url, String path, final String lastPathSegment, String title, String apiTraceId, CpActSchemeUrlListener lisn) {

        return checkSchemeUrlStartActivity(activity, uri, url, path, lastPathSegment, title, lisn);
    }

    private static boolean checkSchemeUrlStartActivity(final Activity activity, Uri uri, String url, String path, final String lastPathSegment, String title, CpActSchemeUrlListener lisn) {

        if (lisn != null && lisn.onCheckSchemeUrlStartActivity(activity, uri, url, path, lastPathSegment, title))
            return true;

            return false;


    }

    /**
     * 如果url没有scheme则给url补上scheme
     *
     * @param url
     * @return
     */
    private static String appendSchemeIfNeed(String url) {

        if (TextUtil.isEmpty(url))
            return TextUtil.TEXT_EMPTY;

        if (!url.startsWith(SCHEME_HTTP) &&
                !url.startsWith(SCHEME_HTTPS) &&
                !url.startsWith(SCHEME_BANTANG) &&
                !url.startsWith(SCHEME_SQKB)) {//2.7.0追加

            url = SCHEME_HTTP + url;
        }

        return url;
    }

    /**
     * 解析路径
     *
     * @param path
     * @return
     */
    public static String parsePath(String path, String lastPathSegment) {

        if (TextUtil.isEmpty(path))
            return TextUtil.TEXT_EMPTY;

        //如果最后一位不是‘/’ 则 追加'/'
        if (path.charAt(path.length() - 1) != '/')
            path = path + "/";

        //如果最后一位是正数数字，则截掉 eg: /topic/123/ -> /topic/
        if (TextUtil.isNumeric(lastPathSegment))
            path = path.replace(lastPathSegment + "/", TextUtil.TEXT_EMPTY);

        return path;
    }

    /**
     * 判断是否是http协议
     *
     * @param url
     * @return
     */
    private static boolean isHttpScheme(String url) {

        url = TextUtil.filterNull(url);
        return url.startsWith(SCHEME_HTTP) ||
                url.startsWith(SCHEME_HTTPS);
    }


    /**
     * 检查是否是需要拦截的scheme host
     *
     * @param host
     * @return
     */
    public static boolean checkHost(String host) {

        return host.contains(HOST_BANTANG) ||
                host.contains(HOST_SQKB_51) ||
                host.contains(HOST_SQKB);
    }

    /**
     * 解析原生页面拦截路径
     *
     * @param url
     * @return
     */
    private static String parseSqkbSchemePath(String url) {

        if (TextUtil.isEmpty(url))
            return TextUtil.TEXT_EMPTY;

        try {

            //转换uri
            Uri uri = Uri.parse(url);
            String host = TextUtil.filterNull(uri.getHost());
            if (checkHost(host)) {

                //解析scheme, path, lastPathSegment
                String lastPathSegment = uri.getLastPathSegment();
                return parsePath(uri.getPath(), lastPathSegment);
            }
        } catch (Exception e) {

        }
        return TextUtil.TEXT_EMPTY;
    }

    public static interface CpActSchemeUrlListener {

        boolean onCheckSchemeUrlStartActivity(Activity activity, Uri uri, String url, String path, String lastPathSegment, String title);
    }

    private static String simpleTag() {

        return CpActSchemeLaunchUtil.class.getSimpleName();
    }
}
