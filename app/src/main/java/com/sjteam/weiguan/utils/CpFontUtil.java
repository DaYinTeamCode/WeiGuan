package com.sjteam.weiguan.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


/**
 * 省钱快报app字体工具类
 */
public class CpFontUtil {

    private static Typeface mFont;
    private static Typeface mDinFont;
    private static Typeface mHeadFont;
    private static Typeface mImpactFont;

    /**
     * 设置文本自定义字体
     *
     * @param tv
     */
    public static void setFont(TextView tv) {

        if (tv == null) {
            return;
        }

        Typeface font = getFont();
        if (font != null) {

            tv.setTypeface(font);
            tv.setIncludeFontPadding(false);
        }
    }

    /**
     * 设置din数值字体
     *
     * @param tv
     */
    public static void setDinFont(TextView tv) {

        if (tv == null) {
            return;
        }

        Typeface font = getDinFont(tv.getContext());
        if (font != null) {

            tv.setTypeface(font);
            tv.setIncludeFontPadding(false);
        }
    }

    /**
     * 设置head数值字体
     *
     * @param tv
     */
    public static void setHeadFont(TextView tv) {

        if (tv == null) {
            return;
        }

        Typeface font = getHeadFont(tv.getContext());
        if (font != null) {

            tv.setTypeface(font);
            tv.setIncludeFontPadding(false);
        }
    }

    /**
     * 设置Impact金额字体
     *
     * @param tv
     */
    public static void setImpactFont(TextView tv) {

        if (tv == null) {
            return;
        }

        Typeface font = getImpactFont(tv.getContext());
        if (font != null) {

            tv.setTypeface(font);
            tv.setIncludeFontPadding(false);
        }
    }

    /**
     * 获取默认通用的自定义字体
     *
     * @return
     */
    public static Typeface getFont() {

        if (mFont == null) {
            mFont = Typeface.DEFAULT;
        }

        return mFont;
    }

    /**
     * 获取Din数字字体
     *
     * @return
     */
    public static Typeface getDinFont(Context context) {

        if (mDinFont == null) {
            mDinFont = createDINFont(context);
        }

        return mDinFont;
    }

    /**
     * 获取head数字字体
     *
     * @return
     */
    public static Typeface getHeadFont(Context context) {

        if (mHeadFont == null) {
            mHeadFont = createHeadFont(context);
        }

        return mHeadFont;
    }

    /**
     * 获取Impact金额字体
     *
     * @return
     */
    public static Typeface getImpactFont(Context context) {

        if (mImpactFont == null) {
            mImpactFont = createImpactFont(context);
        }

        return mImpactFont;
    }

    /**
     * 释放字体
     */
    public static void release() {

        mFont = null;
        mDinFont = null;
        mHeadFont = null;
    }

    /**
     * 加载返回DINCondensedBold数字字体
     *
     * @param context
     */
    private static Typeface createDINFont(Context context) {

        return FontUtil.createFromAsset(context, "fonts/DINCondensedBold.ttf");
    }

    /**
     * 加载返回Headline数字字体
     *
     * @param context
     */
    private static Typeface createHeadFont(Context context) {

        return FontUtil.createFromAsset(context, "fonts/HeadlineA.ttf");
    }

    /**
     * 加载返回Impact数字字体
     *
     * @param context
     */
    private static Typeface createImpactFont(Context context) {

        return FontUtil.createFromAsset(context, "fonts/Impact.ttf");
    }
}
