package com.sjteam.weiguan.page.login.prefs;

import android.content.SharedPreferences;

import com.androidex.prefs.ExSharedPrefs;
import com.androidex.util.TextUtil;
import com.sjteam.weiguan.app.WgApp;
import com.sjteam.weiguan.page.login.bean.WxBind;

/**
 * 账号相关存储
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/26 8:56 PM
 */
public class AccountPrefs {

    private final String KEY_ACCESS_TOKEN = "token";
    private final String KEY_WECHAT_UNION_ID = "wechat_union_id";
    private final String KEY_WECHAT_NICKNAME = "wechat_nickname";
    private final String KEY_WECHAT_AVATAR = "wechat_avatar";
    private static AccountPrefs mInstance;
    private ExSharedPrefs mExSharedPrefs;

    private AccountPrefs() {

        mExSharedPrefs = new ExSharedPrefs(WgApp.getContext(), "account");
    }

    public static AccountPrefs getInstance() {

        if (mInstance == null)
            mInstance = new AccountPrefs();

        return mInstance;
    }

    private void release() {

    }

    public static void releaseInstance() {

        if (mInstance != null) {

            mInstance.release();
            mInstance = null;
        }
    }

    /**
     * 登出操作
     */
    public void logOut() {

        saveWechatInfo(TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY);
    }

    /***
     *  保存微信信息
     *
     * @param token
     * @param unionId
     * @param nickName
     * @param avatar
     */
    public void saveWechatInfo(String token, String unionId, String nickName, String avatar) {

        saveWechatInfoToPrefs(token, unionId, nickName, avatar);
    }

    /**
     * 存储微信相关信息
     *
     * @param unionId
     * @param nickName
     * @param avatar
     */
    private void saveWechatInfoToPrefs(String token, String unionId, String nickName, String avatar) {

        SharedPreferences.Editor editor = mExSharedPrefs.edit();
        editor.putString(KEY_WECHAT_UNION_ID, unionId);
        editor.putString(KEY_WECHAT_NICKNAME, nickName);
        editor.putString(KEY_WECHAT_AVATAR, avatar);
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.commit();
    }

    /***
     *  获取用户信息
     *
     * @return
     */
    public WxBind getUserInfo() {

        WxBind wxBind = new WxBind();
        if (!isLogin()) {

            return wxBind;
        }

        wxBind.setUnionId(mExSharedPrefs.getString(KEY_WECHAT_UNION_ID, TextUtil.TEXT_EMPTY));
        wxBind.setNickName(mExSharedPrefs.getString(KEY_WECHAT_NICKNAME, TextUtil.TEXT_EMPTY));
        wxBind.setHeadImageUrl(mExSharedPrefs.getString(KEY_WECHAT_AVATAR, TextUtil.TEXT_EMPTY));
        wxBind.setToken(mExSharedPrefs.getString(KEY_ACCESS_TOKEN, TextUtil.TEXT_EMPTY));
        return wxBind;
    }

    /***
     *   判断用户是否登录
     *
     * @return
     */
    public boolean isLogin() {

        return !TextUtil.isEmpty(mExSharedPrefs.getString(KEY_ACCESS_TOKEN, TextUtil.TEXT_EMPTY));
    }

    /***
     *  获取登录Token
     *
     * @return
     */
    public String getAccountToekn() {

        return mExSharedPrefs.getString(KEY_ACCESS_TOKEN, TextUtil.TEXT_EMPTY);
    }
}
