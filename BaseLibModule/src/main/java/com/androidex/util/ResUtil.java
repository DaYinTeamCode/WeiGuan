package com.androidex.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.androidex.context.ExApplication;

/**
 * 资源工具类
 * Created by yihaibin on 16/10/20.
 */
public class ResUtil {

    public static Drawable getDrawable(Context context, int drawableResId){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getDrawable(drawableResId);
        else
            return context.getResources().getDrawable(drawableResId);//api21 开始被弃用
    }
}
