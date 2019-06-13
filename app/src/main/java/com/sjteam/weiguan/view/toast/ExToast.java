package com.sjteam.weiguan.view.toast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androidex.util.DensityUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.ViewUtil;
import com.ex.umeng.UmengAgent;
import com.sjteam.weiguan.app.WgApp;
import com.sjteam.weiguan.view.CpTextView;

/**
 * 自定义toast
 *
 * @author : pzwwei
 * @crate : 2016 - 11 - 18 上午11:04
 */

public class ExToast extends Toast {

    private static ExToast mToast;
    private CpTextView mTvText;

    private ExToast() {

        super(WgApp.getContext());
        initContentView(WgApp.getContext());
    }

    private void initContentView(Context context) {

        mTvText = new CpTextView(context);
        mTvText.setTextColor(Color.WHITE);
        mTvText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mTvText.setGravity(Gravity.CENTER);

        FrameLayout frameLayout = new FrameLayout(context);
        ViewUtil.setViewBackground(frameLayout, getDrawable(DensityUtil.dip2px(8), 0xB2000000));

        FrameLayout.LayoutParams params = VglpUtil.getFllpWW();
        params.leftMargin = DensityUtil.dip2px(20f);
        params.rightMargin = DensityUtil.dip2px(20f);
        params.topMargin = DensityUtil.dip2px(12f);
        params.bottomMargin = DensityUtil.dip2px(12f);
        frameLayout.addView(mTvText, params);

        setView(frameLayout);
    }

    @Override
    public void setText(CharSequence s) {

        mTvText.setText(s);
    }

    @Override
    public void setText(int resId) {

        mTvText.setText(resId);
    }

    public void setTextSize(int unit, int textSize) {

        mTvText.setTextSize(unit, textSize);
    }

    public void setTextColor(int textColor) {

        mTvText.setTextColor(textColor);
    }

    /**
     * 获取指定圆角,及颜色的drawable
     *
     * @param radius
     * @param color
     * @return
     */
    public static GradientDrawable getDrawable(int radius, int color) {

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(radius);
        gd.setColor(color);
        return gd;
    }

    public static ExToast makeText(CharSequence text) {

        if (mToast != null)
            mToast.cancel();

        mToast = new ExToast();
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        return mToast;
    }

    /***
     *   创建Toast 对象
     *
     * @param text
     * @param gravity
     * @param yOffset
     * @return
     */
    public static ExToast makeText(CharSequence text, int gravity, int yOffset) {

        if (mToast != null)
            mToast.cancel();

        mToast = new ExToast();
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.setGravity(gravity, 0, yOffset);
        return mToast;
    }

    public static void release() {

        mToast = null;
    }

    @Override
    public void show() {

        try {
            super.show();
        } catch (Exception e) {
            UmengAgent.reportError(WgApp.getContext(), e);
        }
    }
}
