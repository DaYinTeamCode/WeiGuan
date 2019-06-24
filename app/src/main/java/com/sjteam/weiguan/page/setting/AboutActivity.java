package com.sjteam.weiguan.page.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.AppInfoUtil;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpActivity;
import com.sjteam.weiguan.utils.CpFontUtil;
import com.sjteam.weiguan.view.CpTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import butterknife.Unbinder;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/24 4:49 PM
 */
public class AboutActivity extends CpActivity implements View.OnClickListener {

    private Unbinder mUnbinder;

    @BindView(R.id.tvVersion)
    CpTextView mTvVersion;

    @BindView(R.id.tvLauncher)
    CpTextView mTvLauncher;

    private int mTitleClickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_setting_about_act);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mUnbinder != null)
            mUnbinder.unbind();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected boolean isStatusbarEnabled() {

        return false;
    }

    @Override
    protected void initTitleView() {

        TextView tvTitle = addTitleMiddleTextViewWithBack("关于");
        tvTitle.setOnClickListener(this);
        CpFontUtil.setFont(tvTitle);
        StatusBarManager.getInstance().initStatusbar(this, R.color.app_white);
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

        mUnbinder = ButterKnife.bind(this, getExDecorView());
        mTvVersion.setText(String.format("V%s", AppInfoUtil.getVersionName()));
        getExDecorView().setBackgroundColor(0xFFFFFFFF);
    }

    @OnLongClick(R.id.tvLauncher)
    public boolean onLauncherIconClick() {

        return true;
    }

    @Override
    public void onClick(View v) {

    }

    public static void startActivity(Activity activity) {

        Intent intent = new Intent();
        intent.setClass(activity, AboutActivity.class);
        activity.startActivity(intent);
    }
}
