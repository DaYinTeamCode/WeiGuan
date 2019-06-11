package com.sjteam.weiguan.page.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidex.plugin.DelayBackHandler;
import com.androidex.util.ToastUtil;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;
import com.sjteam.weiguan.page.home.MainHomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 首页主框架
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 10:01 PM
 */
public class MainActivity extends CpFragmentActivity implements DelayBackHandler.OnDelayBackListener {

    public static final int EXTRA_VALUE_LAUNCHER_TAB_INDEX = 1;

    @BindView(R.id.flMainFraContainer)
    FrameLayout flMainFraContainer;

    /*** 发布 */
    @BindView(R.id.ivPushlish)
    ImageView ivPushlish;

    /*** Tab组件 */
    @BindView(R.id.ntTab)
    NavigationTabStrip ntTab;

    /*** Content */
    @BindView(R.id.flTabDiv)
    FrameLayout flTabDiv;

    private Unbinder unbinder;
    private DelayBackHandler mDelayBackHandler;
    boolean isAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {

        mDelayBackHandler = new DelayBackHandler();
        mDelayBackHandler.setOnDelayBackListener(this);
    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

        unbinder = ButterKnife.bind(this, getExDecorView());
        initTabView();

    }

    /***
     *  初始化TabView
     */
    private void initTabView() {

        ntTab.setTabIndex(0, true);
        ntTab.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {

//                Toast.makeText(MainActivity.this, "onStartTabSelected标题" + title, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onEndTabSelected(String title, int index) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (isAdd) {

                    isAdd = false;
                    transaction.add(R.id.flMainFraContainer, MainHomeFragment.newInstance(MainActivity.this));
                } else {
                    transaction.show(MainHomeFragment.newInstance(MainActivity.this));
                }

//                if (mSelectedFra != null)
//                    transaction.hide(mSelectedFra);

                transaction.commitAllowingStateLoss();
//                Toast.makeText(MainActivity.this, "onEndTabSelected标题" + title, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (unbinder != null) {

            unbinder.unbind();
        }
    }

    /**
     * back键点击
     */
    @Override
    public void onBackPressed() {

        if (mDelayBackHandler != null) {

            mDelayBackHandler.triggerPreBack();
        }
    }

    /***
     *  退出回调
     * @param preBack
     */
    @Override
    public void onDelayBack(boolean preBack) {

        perBackOrFinish(preBack);
    }

    private void perBackOrFinish(boolean preBack) {

        if (preBack) {

            showToast("再按一次退出应用");
        } else {

            finish();
        }
    }

    @OnClick(R.id.ivPushlish)
    public void ivPushClick() {

        ToastUtil.showToast("点击发布视频");
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
