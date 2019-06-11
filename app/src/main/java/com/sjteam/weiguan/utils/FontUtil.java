package com.sjteam.weiguan.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.androidex.util.TextUtil;


/**
 * 字体工具类
 */
public class FontUtil {

    /**
     * 加载asset目录指定Typeface资源
     *
     * @param context
     * @param fontFilePath
     * @return
     */
    public static Typeface createFromAsset(Context context, String fontFilePath) {

        if (context == null || TextUtil.isEmpty(fontFilePath)) {
            return null;
        }

        try {

            return Typeface.createFromAsset(context.getAssets(), fontFilePath);

        } catch (Exception e) {

        }

        return null;
    }
}
