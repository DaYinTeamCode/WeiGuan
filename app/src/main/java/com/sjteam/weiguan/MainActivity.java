package com.sjteam.weiguan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.androidex.activity.ExFragmentActivity;

public class MainActivity extends ExFragmentActivity {

    public static final int EXTRA_VALUE_LAUNCHER_TAB_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

        addTitleMiddleTextView("首页");
    }

    @Override
    protected void initContentView() {

    }

    public static void startActivityForIndex(Activity activity) {

        startActivity(activity, EXTRA_VALUE_LAUNCHER_TAB_INDEX, false, 0);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.push_exit_stop);
    }

    private static void startActivity(Context context, int launcherTab, boolean newActivityTask, int tabIndex) {

        Intent intent = new Intent();

        if (newActivityTask)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }
}
