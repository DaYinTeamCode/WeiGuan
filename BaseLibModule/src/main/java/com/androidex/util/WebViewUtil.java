package com.androidex.util;

import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * webview工具类
 * Created by yihaibin on 2016/12/2.
 */
public class WebViewUtil {

    /**
     * 设置webview debug 调试
     * @param webView
     */
    public static void setWebContentsDebuggingEnabled(WebView webView){

        if(webView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);
    }

    /**
     * 设置https混合内容模式
     * @param settings
     * @param mixedContentMode
     */
    public static void setMixedContentMode(WebSettings settings, int mixedContentMode){

        if(settings != null && CompatUtil.isGreatThanOrEqualToLollipop()){

            settings.setMixedContentMode(mixedContentMode);
        }
    }
}
