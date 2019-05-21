package com.androidex.widget.rv.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.util.TextUtil;
import com.androidex.util.ViewUtil;
import com.androidex.view.progress.ProgressWheel;
import com.androidex.widget.rv.hf.ExRvItemViewHolderFooter;

/**
 * Created by yihaibin on 2017/5/21.
 */

public class ExRvLoadMoreView implements ExRvItemViewHolderFooter.ILoadMorer {

    private View mContentView;
    private TextView mTvLoadText;
    private ProgressWheel mPwLoading;
    private TextView mTvNoDataText;
    private boolean mNoDataTipMode;
    private String mFailedText;

    public ExRvLoadMoreView(Context context) {

        mContentView = LayoutInflater.from(context).inflate(R.layout.ex_rv_footer_load_more, null);
        mTvLoadText = mContentView.findViewById(R.id.tvLoadText);
        mPwLoading = mContentView.findViewById(R.id.pwLoading);
        mPwLoading.setBarColor(Color.RED);
        mTvNoDataText = mContentView.findViewById(R.id.tvNoDataText);
        mFailedText = "加载失败, 点击重试...";
        switchDisable();
    }

    public TextView getLoadTextView() {

        return mTvLoadText;
    }

    public ProgressWheel getProgressView() {

        return mPwLoading;
    }

    public TextView getNoDataTextView() {

        return mTvNoDataText;
    }

    public void setFailedText(String text) {

        mFailedText = text;
    }

    public void setNoDataAttr(String text, int drawableLeft, int drawableTop, int drawableRight, int drawableBtm) {

        mTvNoDataText.setText(text);
        ViewUtil.setCompoundDrawable(mTvNoDataText, drawableLeft, drawableTop, drawableRight, drawableBtm);
        mNoDataTipMode = true;
    }

    @Override
    public View getContentView() {

        return mContentView;
    }

    @Override
    public void switchLoading() {

        mTvLoadText.setText("正在加载...");
        ViewUtil.showView(mPwLoading);
        ViewUtil.showView(mContentView);
        ViewUtil.hideView(mTvNoDataText);
    }

    @Override
    public void switchStop() {

        mTvLoadText.setText(TextUtil.TEXT_EMPTY);
        ViewUtil.hideView(mPwLoading);
        ViewUtil.hideView(mTvNoDataText);
        ViewUtil.showView(mContentView);
    }

    @Override
    public void switchFailed() {

        mTvLoadText.setText(mFailedText);
        ViewUtil.hideView(mPwLoading);
        ViewUtil.hideView(mTvNoDataText);
        ViewUtil.showView(mContentView);
    }

    @Override
    public void switchDisable() {

        switchStop();
        if (mNoDataTipMode) {

            ViewUtil.showView(mTvNoDataText);
            ViewUtil.showView(mContentView);
        } else {

            ViewUtil.goneView(mContentView);
        }
    }
}
