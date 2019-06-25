package com.jzyd.lib.loading;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.androidex.util.ViewUtil;
import com.jzyd.lib.R;


/**
 * 页面loadingview
 * Created by yihaibin on 15/11/11.
 */
public class SqkbPageLoadingView extends View {

    public SqkbPageLoadingView(Context context) {

        super(context);
    }

    public SqkbPageLoadingView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public void show() {

        startAnimDrawable();
        ViewUtil.showView(this);
    }

    public void hide() {

        ViewUtil.hideView(this);
        stopAnimDrawable();
    }

    private void startAnimDrawable() {

        AnimationDrawable drawable = (AnimationDrawable) getBackground();
        if (drawable == null) {

            drawable = (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.anim_page_loading);
            ViewUtil.setViewBackground(this, drawable);
            drawable.start();
        } else {

            if (!drawable.isRunning())
                drawable.start();
        }
    }

    private void stopAnimDrawable() {

        AnimationDrawable drawable = (AnimationDrawable) getBackground();
        if (drawable != null && drawable.isRunning()) {

            drawable.stop();
        }

        ViewUtil.setViewBackground(this, null);
    }
}
