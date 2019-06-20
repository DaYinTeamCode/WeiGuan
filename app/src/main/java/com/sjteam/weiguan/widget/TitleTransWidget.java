package com.sjteam.weiguan.widget;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import com.androidex.plugin.ExViewWidget;
import com.androidex.util.ViewUtil;
import com.androidex.view.ExDecorView;
import com.sjteam.weiguan.R;

/**
 * 透明标题组件
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/13 11:14 PM
 */
public class TitleTransWidget extends ExViewWidget implements ValueAnimator.AnimatorUpdateListener {

    private View mTitleView, mTitleMidView;
    private LayerDrawable mTitleBgDrawable;
    private boolean mTitleNeedShadow;
    private int mBgDrawableAlpha = 255;
    private ValueAnimator mAnimator;
    private OnAlphaChangedListener mAlphaLisn;

    public TitleTransWidget(Activity activity, ExDecorView contentView, boolean titleNeedShadow) {

        super(activity, contentView.getTitleView(), contentView.getTitleMiddleView(), titleNeedShadow);
    }

    @Override
    protected void onInitView(View contentView, Object... args) {

        mTitleView = contentView;
        mTitleMidView = (View) args[0];
        mTitleNeedShadow = (boolean) args[1];
        mTitleBgDrawable = getTitleBgDrawable(contentView.getContext());
        ViewUtil.setViewBackground(mTitleView, mTitleBgDrawable);
    }

    private LayerDrawable getTitleBgDrawable(Context context) {

        //阴影图片
        Drawable layer0 = new ColorDrawable(0x00000000);
        //正常的标题栏背景图片
        Drawable layer1 = context.getResources().getDrawable(R.drawable.bg_title_bar);//new ColorDrawable(Color.WHITE);//activity.getResources().getDrawable(android.R.color.white);
        // Umeng BUG    ---> NullPointerException
        if (layer1 == null)
            layer1 = new ColorDrawable(Color.WHITE);
        return new LayerDrawable(new Drawable[]{layer0, layer1});
    }

    public void setTitleBgRGB(String color) throws Exception {

        //阴影图片
        Drawable layer0 = new ColorDrawable(0x00000000);
        //正常的标题栏背景图片
        Drawable layer1 = new ColorDrawable(Color.parseColor("#" + color));//new ColorDrawable(Color.WHITE);//activity.getResources().getDrawable(android.R.color.white);
        setTitleViewBg(layer0, layer1);
    }


    public void setTitleDefaultBgRGB(String color) {
        //阴影图片
        Drawable layer0 = new ColorDrawable(0x00000000);
        //正常的标题栏背景图片
        Drawable layer1 = new ColorDrawable(Color.parseColor(color));//new ColorDrawable(Color.WHITE);//activity.getResources().getDrawable(android.R.color.white);
        setTitleViewBg(layer0, layer1);
    }

    public void setTitleViewBg(Drawable layer0, Drawable layer1) {

        mTitleBgDrawable = new LayerDrawable(new Drawable[]{layer0, layer1});
        ViewUtil.setViewBackground(mTitleView, mTitleBgDrawable);
    }


    public void setAlpha(int alpha) {

        setAlpha(alpha, true);
    }

    private void setAlpha(int alpha, boolean checkAnimRunning) {
        if (checkAnimRunning && isAnimtorRunning())
            return;

        if (alpha < 0)
            alpha = 0;

        if (alpha > 255)
            alpha = 255;

        if (mBgDrawableAlpha == alpha)
            return;

        mTitleBgDrawable.getDrawable(0).setAlpha(255 - alpha);
        mTitleBgDrawable.getDrawable(1).setAlpha(alpha);
//        ViewHelper.setAlpha(mTitleMidView, alpha / 255f);
        mTitleMidView.setAlpha(alpha / 255f);

        mBgDrawableAlpha = alpha;

        if (mAlphaLisn != null)
            mAlphaLisn.onTitleBgAlphaChanged(mBgDrawableAlpha);
    }

    public void setAlphaTranByAnim() {

        setAlphaByAnim(mBgDrawableAlpha, 0);
    }

    public int getAlpha() {

        return mBgDrawableAlpha;
    }

    private void setAlphaByAnim(int srcAlpha, int destAlpha) {

        if (mAnimator == null) {

            mAnimator = new ValueAnimator();
            mAnimator.addUpdateListener(this);
        }

        if (mAnimator.isRunning())
            mAnimator.cancel();

        mAnimator.setDuration(300);
        mAnimator.setIntValues(srcAlpha, destAlpha);
        mAnimator.start();
    }

    private void abortAlphaAnimIfRunning() {

        if (isAnimtorRunning())
            mAnimator.cancel();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {

        setAlpha((Integer) valueAnimator.getAnimatedValue(), false);
    }

    public void setOnBgAlphaChangedListener(OnAlphaChangedListener lisn) {

        mAlphaLisn = lisn;
    }

    public boolean isAnimtorRunning() {

        return mAnimator != null && mAnimator.isRunning();
    }

    public void switchTitleMidViewVisible() {

        if (mTitleMidView.getVisibility() == View.VISIBLE) {
            ViewUtil.hideView(mTitleMidView);
            abortAlphaAnimIfRunning();
            setAlpha(255);
        } else {
            ViewUtil.showView(mTitleMidView);
        }
    }

    public interface OnAlphaChangedListener {

        void onTitleBgAlphaChanged(int alpha);
    }
}
