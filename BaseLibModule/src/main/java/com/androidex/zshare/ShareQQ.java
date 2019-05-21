package com.androidex.zshare;

import android.app.Activity;
import android.os.Bundle;

import com.androidex.util.TextUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * QQ分享组件
 *
 * <h3>1. 版本说明</h3>
 * <ul>
 *      <li>3.1   整理代码规范，添加全局异常处理
 * </ul>
 * <p/>
 *
 * <h3>2. 功能说明</h3>
 * <ul>
 *      <li>1   分享到QQ空间
 *      <li>2   分享到QQ好友
 * </ul>
 *
 * @author pzwwei
 * @version 3.1
 * @since 2014-12-16
 */
public class ShareQQ {

    /**
     * 分享到QQ好友
     *
     * @param activity    分享上下文
     * @param appKey      申请qq分享应用，appkey
     * @param title       分享标题
     * @param info        分享内容
     * @param linkUrl     分享链接
     * @param imgUrl      分享图片
     * @param lisn        分享回调接口
     */
    public static void shareQQ(Activity activity, String appKey, String title, String info, String linkUrl, String imgUrl, boolean imgUrlIsLocal, IUiListener lisn) {

        Tencent tencent = Tencent.createInstance(appKey, activity);
        QQShare qqShare = new QQShare(activity,tencent.getQQToken());

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);// 图+文
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, info);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, linkUrl);
        params.putString(imgUrlIsLocal ? QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL : QzoneShare.SHARE_TO_QQ_IMAGE_URL, TextUtil.filterNull(imgUrl));

        qqShare.shareToQQ(activity, params, lisn);
    }

    public static void shareImageToQQ(Activity activity, String appKey, String appName, String imageLocalUrl, String text, IUiListener lisn) {

        Tencent tencent = Tencent.createInstance(appKey, activity);
        QQShare qqShare = new QQShare(activity, tencent.getQQToken());

        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageLocalUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, TextUtil.filterNull(appName));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, TextUtil.filterNull(text));
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        qqShare.shareToQQ(activity, params, lisn);
    }

    /**
     * 分享到QQ空间
     *
     * @param activity    分享上下文
     * @param appKey      申请qq分享应用，appkey
     * @param title       分享标题
     * @param info        分享内容
     * @param linkUrl     分享链接
     * @param imgUrl      分享图片
     * @param lisn        分享回调接口
     */
	public static void shareQzone(Activity activity, String appKey, String title, String info, String linkUrl, String imgUrl, boolean imgUrlIsLocal, IUiListener lisn) {

		Tencent tencent = Tencent.createInstance(appKey, activity);
		QzoneShare qzoneShare = new QzoneShare(activity, tencent.getQQToken());

		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);// 图+文
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, info);
        params.putString(imgUrlIsLocal?QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL:QzoneShare.SHARE_TO_QQ_IMAGE_URL, TextUtil.filterNull(imgUrl));
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, linkUrl);

		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(imgUrl);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        qzoneShare.shareToQzone(activity, params, lisn);
	}

    /**
     * 分享图片到qq空间
     * @param activity
     * @param appKey
     * @param imageLocalUrl
     * @param lisn
     */
        public static void shareImageToQzone(Activity activity, String appKey, String appName, String imageLocalUrl, String text, IUiListener lisn) {

        Tencent tencent = Tencent.createInstance(appKey, activity);
        QQShare qqShare = new QQShare(activity, tencent.getQQToken());

        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageLocalUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, TextUtil.filterNull(appName));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, TextUtil.filterNull(text));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        qqShare.shareToQQ(activity, params, lisn);
    }
}
