package com.sjteam.weiguan.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

/**
 * ===========================================================
 * 作    者：大印（高印） Github地址：https://github.com/GaoYin2016
 * 邮    箱：18810474975@163.com
 * 版    本：
 * 创建日期：2017/12/21 下午3:41
 * 描    述： 权限管理类
 * 修订历史：
 * ===========================================================
 */
public class PermissionUtil {

    /**
     * 针对 targetSdkVersion< 23 版本
     * <p>
     * 检测 某个权限是否授权
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissionForLessVersion_M(Context context, String permission) {

        return PermissionChecker.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 针对targetSdkVersion>=23 版本
     * <p>
     * 检测某个权限是否授权
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissionForGreaterVersion_M(Context context, String permission) {

        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }


}
