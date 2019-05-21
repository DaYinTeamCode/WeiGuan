package com.androidex.zshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import com.androidex.R;
import com.androidex.util.DeviceUtil;
import com.androidex.util.ImageUtil;
import com.androidex.util.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareWeixin {

    private static String WX_SCOPE = "snsapi_userinfo";
    private static String WX_STATE = "wechat_sdk_sqkb";

    /**
     * 分享给微信好友
     */
    public static void shareWeixinFriend(Context context, String appKey, String iconUrl, String url, String title, String content) {

        sendMessageToWx(context, appKey, SendMessageToWX.Req.WXSceneSession, iconUrl, url, title, content);
    }

    /**
     * 分享至微信朋友圈
     */
    public static void shareWeixinQuan(Context context, String appKey, String iconUrl, String url, String title, String content) {

        sendMessageToWx(context, appKey, SendMessageToWX.Req.WXSceneTimeline, iconUrl, url, title, content);
    }

    /**
     * 分享图片给好友
     */
    public static void shareImageToFriend(Context context, String appKey, String imagePath) {

        shareWeixinImage(context, appKey, imagePath, SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享图片至朋友圈
     */
    public static void shareImageToTimeline(Context context, String appKey, String imagePath) {

        shareWeixinImage(context, appKey, imagePath, SendMessageToWX.Req.WXSceneTimeline);
    }

    private static void sendMessageToWx(final Context context, final String appKey, final int scene, final String iconUrl, final String url, final String title, final String content) {

        if (url == null)
            return;

        new AsyncTask<String, String, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {

                try {

                    Bitmap thumbBmp = ImageUtil.loadBitmap(Uri.parse(iconUrl), 150, 150);
                    Bitmap scaleBmp = ThumbnailUtils.extractThumbnail(thumbBmp, 150, 150);

                    if (thumbBmp != null && thumbBmp != scaleBmp)
                        ImageUtil.recycleBitmap(thumbBmp);

                    return scaleBmp;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap scaleBmp) {

                sendImgMessageToWx(context, appKey, scene, scaleBmp, url, title, content);
            }

        }.execute();
    }

    private static void sendImgMessageToWx(final Context context, String appKey, int scene, Bitmap bitmap, String url, String title, String content) {

        if (title == null)
            title = "";

        if (content == null)
            content = "";

        if (!DeviceUtil.hasApp("com.tencent.mm")) {

            ToastUtil.showToast(R.string.toast_share_weixin_not_install);
            return;
        }

        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, appKey, true);
        wxapi.registerApp(appKey);

        if (scene == SendMessageToWX.Req.WXSceneTimeline) {//朋友圈

            if (wxapi.getWXAppSupportAPI() < 0x21020001) {

                ToastUtil.showToast(R.string.toast_share_weixin_version_lower);
                return;
            }

            try {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = content;

                msg.thumbData = ImageUtil.bmpToByteArray(bitmap, 90, true); // 设置缩略图
                if (bitmap != null)
                    bitmap.recycle();

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = scene;

                // 调用api接口发送数据到微信
                wxapi.sendReq(req);

            } catch (Exception e) {

                e.printStackTrace();
            }

        } else {

            try {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = content;

                msg.thumbData = ImageUtil.bmpToByteArray(bitmap, 90, true); // 设置缩略图
                bitmap.recycle();
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = scene;

                // 调用api接口发送数据到微信
                wxapi.sendReq(req);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    private static void shareWeixinImage(Context context, String appKey, String imagePath, int secen) {

        if (!DeviceUtil.hasApp("com.tencent.mm")) {

            ToastUtil.showToast(R.string.toast_share_weixin_not_install);
            return;
        }

        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, appKey, true);
        wxapi.registerApp(appKey);

        if (wxapi.getWXAppSupportAPI() < 0x21020001) {

            ToastUtil.showToast(R.string.toast_share_weixin_version_lower);
            return;
        }

        try {

            WXImageObject imageObj = new WXImageObject();
            imageObj.imagePath = imagePath;

            WXMediaMessage msg = new WXMediaMessage(imageObj);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("image");
            req.message = msg;

            req.scene = secen;

            // 调用api接口发送数据到微信
            wxapi.sendReq(req);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static String buildTransaction(final String type) {

        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

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
