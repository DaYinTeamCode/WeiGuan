package com.sjteam.weiguan.page.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidex.plugin.DelayBackHandler;
import com.androidex.util.DensityUtil;
import com.androidex.util.TextUtil;
import com.androidex.util.ToastUtil;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.jzyd.lib.util.MD5Util;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;
import com.sjteam.weiguan.page.home.MainHomeFragment;
import com.sjteam.weiguan.page.me.MainUserFragment;
import com.sjteam.weiguan.page.news.MainMessageFragment;
import com.sjteam.weiguan.page.video.MainVideoFragment;
import com.sjteam.weiguan.view.toast.ExToast;

import java.util.LinkedHashMap;

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

    private DelayBackHandler mDelayBackHandler;

    /*** 当前选中Fragment */
    private Fragment mSelectedFra;
    private int mCurIndex;

    private LinkedHashMap<String, Fragment> mTabFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {

        mDelayBackHandler = new DelayBackHandler();
        mDelayBackHandler.setOnDelayBackListener(this);
        mTabFragments = new LinkedHashMap<>();
    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

        initTabWidget();
    }

    /***
     *  初始化TabView
     */
    private void initTabWidget() {

        mCurIndex = 0;
        initDefaultTabWidget();
        ntTab.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {

                if (mCurIndex != index) {

                    switchFragmentTab(index);
                    mCurIndex = index;
                }
            }

            @Override
            public void onEndTabSelected(String title, int index) {
            }
        });
    }

    @OnClick(R.id.ivPushlish)
    public void ivPushClick() {

        ToastUtil.showToast("点击发布视频");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mTabFragments != null) {

            mTabFragments = null;
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

    /*---------------------------------------- 辅助函数 -------------------------------------------*/

    /***
     *   初始化默认Tab组件
     */
    private void initDefaultTabWidget() {

        ntTab.setTabIndex(mCurIndex, true);
        switchFragmentTab(mCurIndex);
    }

    /***
     *  切换Fragment
     *
     * @param index
     */
    private void switchFragmentTab(int index) {

        boolean isAddFragment = false;
        Fragment fragment = getFragmentFromTag(MD5Util.md5(String.valueOf(index)));

        if (fragment == null) {

            fragment = newFragmentInstance(this, index);
            isAddFragment = true;
        }

        if (fragment != null) {

            switchFragment(fragment, isAddFragment);
            if (isAddFragment) {

                setFragmentToTag(MD5Util.md5(String.valueOf(index)), fragment);
            }
        }
    }

    public static Fragment newFragmentInstance(Context context, int index) {

        if (context == null) {

            return null;
        }
        switch (index) {

            case 0:
                return MainHomeFragment.newInstance(context);
            case 1:
                return MainVideoFragment.newInstance(context);
            case 3:
                return MainMessageFragment.newInstance(context);
            case 4:
                return MainUserFragment.newInstance(context);
            default:
                return null;
        }
    }

    /**
     * 从HasMap 中获取 fragment
     *
     * @param keyView
     * @return
     */
    public Fragment getFragmentFromTag(String keyView) {

        return TextUtil.isEmpty(keyView) ? null : mTabFragments.get(keyView);
    }

    /**
     * 将Fragment设置进HasMap
     *
     * @param keyView
     * @param fragment
     */
    public void setFragmentToTag(String keyView, Fragment fragment) {

        if (!TextUtil.isEmpty(keyView)) {

            mTabFragments.put(keyView, fragment);
        }
    }

    /**
     * 切换fragment事务
     *
     * @param fragment
     * @param isFragmentAdd
     */
    private void switchFragment(Fragment fragment, boolean isFragmentAdd) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (isFragmentAdd) {

            transaction.add(R.id.flMainFraContainer, fragment);
        } else {

            transaction.show(fragment);
        }

        if (mSelectedFra != null) {

            transaction.hide(mSelectedFra);
        }

        transaction.commitAllowingStateLoss();
        mSelectedFra = fragment;
    }

    /***
     *
     * @param preBack
     */
    private void perBackOrFinish(boolean preBack) {

        if (preBack) {

            ExToast.makeText("再按一次退出应用", Gravity.BOTTOM, DensityUtil.dip2px(60f)).show();
        } else {

            finish();
        }
    }

    /***
     * 打开主界面
     *
     * @param activity
     */
    public static void startActivityForIndex(Activity activity) {

        startActivity(activity, EXTRA_VALUE_LAUNCHER_TAB_INDEX, false, 0);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.push_exit_stop);
    }

    /***
     *  打开主界面
     *
     * @param context
     * @param launcherTab
     * @param newActivityTask
     * @param tabIndex
     */
    private static void startActivity(Context context, int launcherTab, boolean newActivityTask, int tabIndex) {

        Intent intent = new Intent();

        if (newActivityTask) {

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }
}
