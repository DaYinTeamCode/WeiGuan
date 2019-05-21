package com.androidex.util;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 版本兼容工具类
 * Created by yihaibin on 15/10/25.
 */
public class CompatUtil {

    /**
     * 4.0版本系列
     *
     * @return
     */
    public static boolean isIcsVersion() {

        return Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * 是否大于等于冰激淋版本 4.0
     *
     * @return
     */
    public static boolean isGreatThanOrEqualToIcsVersion() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 是否大于等于4.4 版本
     *
     * @return
     */
    public static boolean isGreatThanOrEqualToKitkatVersion() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 是否大于等于棒棒糖5.0
     *
     * @return
     */
    public static boolean isGreatThanOrEqualToLollipop() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否大于棒棒糖5.0
     *
     * @return
     */
    public static boolean isGreatToLollipop() {

        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否大于等于M
     *
     * @return
     */
    public static boolean isGreatThanOrEqualToM() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isLessThanLollipop() {

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 判断当前版本是否大于等于4.0
     *
     * @return
     */
    public static boolean isIcsOrLater() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 判断View是否渲染完成
     *
     * @param view
     * @return
     */
    public static boolean isInLayout(View view) {

        if (view == null)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            return view.isInLayout();
        else
            return false;
    }
}
