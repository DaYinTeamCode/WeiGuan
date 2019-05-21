package com.androidex.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.androidex.context.ExApplication;

/**
 * 资源工具类
 * Created by yihaibin on 2017/11/24.
 */

public class ExResUtil {

    /**
     * 获取颜色资源对应的颜色值
     *
     * @param colorResId
     * @return
     */
    public static int getColorValue(int colorResId) {

        if (colorResId == 0)
            return 0;

        if (CompatUtil.isGreatThanOrEqualToM())
            return ExApplication.getContext().getColor(colorResId);
        else
            return ExApplication.getContext().getResources().getColor(colorResId);
    }

    public static Drawable getDrawable(int drawableResId){

        if (drawableResId == 0)//默认给个透明drawable
            return new ColorDrawable(Color.TRANSPARENT);

        if(CompatUtil.isGreatToLollipop())
            return ExApplication.getContext().getResources().getDrawable(drawableResId, ExApplication.getContext().getTheme());
        else
            return ExApplication.getContext().getResources().getDrawable(drawableResId);
    }
}
