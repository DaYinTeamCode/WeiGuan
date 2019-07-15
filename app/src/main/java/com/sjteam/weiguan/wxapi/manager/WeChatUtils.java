package com.sjteam.weiguan.wxapi.manager;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信帮助类
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/9 4:08 PM
 */
public class WeChatUtils {

    private static String WX_SCOPE = "snsapi_userinfo";
    private static String WX_STATE = "wechat_sdk_weiguan";

    /**
     * 微信授权.
     *
     * @param context
     */
    public static void authWeChat(Context context, String key) {

        /**
         * 微信授权不可接受Context = null，否则会 NPE
         */
        if (context == null) {

            return;
        }

        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, key, false);
        wxApi.registerApp(key);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = WX_SCOPE;
        req.state = WX_STATE;
        wxApi.sendReq(req);
    }
}
