package com.sjteam.weiguan.page.login.prefs;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidex.prefs.ExSharedPrefs;
import com.androidex.util.NumberUtil;

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
    private Account mAccountCache;

    private AccountPrefs(Context context) {

        mExSharedPrefs = new ExSharedPrefs(context, "account");
    }

    public static AccountPrefs getInstance(Context context) {

        if (mInstance == null)
            mInstance = new AccountPrefs(context);

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

    public void logOut() {

//        saveAccountToPrefs(null);
//        initAccountCache();
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

}
