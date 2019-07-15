package com.androidex.statusbar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;

/**
 * 沉淀式状态管理类
 * 提供工具类中间件，目的为了解耦
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/17 12:12 PM
 */
public class StatusBarManager {

    /*** 单列实例对象 */
    private volatile static StatusBarManager mInstance;

    /*** 沉淀式状态栏是否可用 ，全局控制 默认为true */
    private boolean isStatusbarEnable = true;

    /***
     *  获取沉淀式状态栏单例
     *
     * @return
     */
    public static StatusBarManager getInstance() {

        if (mInstance == null) {

            synchronized (StatusBarManager.class) {
                if (mInstance == null) {
                    mInstance = new StatusBarManager();
                }
            }
        }
        return mInstance;
    }

    /***
     *   私有化构造函数
     */
    private StatusBarManager() {

    }

    /***
     *  沉淀式状态栏是否可用
     *
     * @return
     */
    public boolean isIsStatusbarEnable() {

        return isStatusbarEnable;
//                && ImmersionBar.isSupportStatusBarDarkFont()
//                && ImmersionBar.isSupportNavigationIconDark();
    }

    /***
     *   设置沉淀式状态栏是否可用
     *
     * @param isStatusbarEnable
     */
    public void setStatusbarEnable(boolean isStatusbarEnable) {

        this.isStatusbarEnable = isStatusbarEnable;
    }

    /***
     *  获取状态栏高度
     *
     * @param activity Activity
     * @return 返回状态栏高度
     */
    public int getStatusbarHeight(Activity activity) {

        /*** 针对Android 4.4以下手机 不做任何改变 */
        if (activity == null
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                || !isIsStatusbarEnable()) {

            return 0;
        }
        /*** 获取StatusBar区域高度 */
        return ImmersionBar.getStatusBarHeight(activity);
    }

    /**
     * 初始化状态栏样式
     *
     * @param activity
     */
    public void initStatusbar(Activity activity) {

        if (activity == null
                || !isIsStatusbarEnable()) {

            return;
        }
        initStatusbar(activity, null);
    }

    /***
     *  初始化状态栏样式
     *
     * @param activity
     * @param color
     */
    public void initStatusbar(Activity activity, @ColorRes int color) {

        if (activity == null
                || !isIsStatusbarEnable()) {

            return;
        }
        try {

            ImmersionBar.with(activity)
                    .statusBarDarkFont(true, 0.2f)
                    .statusBarColor(color)
                    .autoDarkModeEnable(true)
                    .fitsSystemWindows(true)
                    .keyboardEnable(false)
                    .init();
        } catch (Exception ex) {

        }
    }

    /***
     * 初始化状态栏样式
     *
     * @param activity
     * @param view
     */
    public void initStatusbar(Activity activity, View view) {

        if (activity == null
                || !isIsStatusbarEnable()) {

            return;
        }
        try {

            ImmersionBar immersionBar = ImmersionBar.with(activity)
                    .statusBarDarkFont(true, 0.2f)
                    .keyboardEnable(false)
                    .fitsSystemWindows(true);

            if (view != null) {

                immersionBar.titleBar(view);
            }
            immersionBar.init();
        } catch (Exception ex) {

        }
    }

    /**
     * 初始化状态栏样式
     *
     * @param fragment
     * @param view
     */
    public void initStatusbar(Fragment fragment, View view) {

        if (fragment == null
                || !isIsStatusbarEnable()) {

            return;
        }
        initStatusbar(ImmersionBar.with(fragment), view);
    }

    /**
     * 初始化状态栏样式
     *
     * @param fragment
     * @param view
     */
    public void initStatusbar(android.support.v4.app.Fragment fragment, View view) {

        if (fragment == null
                || !isIsStatusbarEnable()) {

            return;
        }
        initStatusbar(ImmersionBar.with(fragment), view);
    }

    /**
     * 初始化状态栏样式
     *
     * @param dialogFragment
     * @param view
     */
    public void initStatusbar(android.app.DialogFragment dialogFragment, View view) {

        if (dialogFragment == null
                || !isIsStatusbarEnable()) {

            return;
        }

        initStatusbar(ImmersionBar.with(dialogFragment), view);
    }

    /***
     *  初始化Statusbar相关配置
     *
     * @param immersionBar
     * @param view
     */
    private void initStatusbar(ImmersionBar immersionBar, View view) {

        if (immersionBar == null) {

            return;
        }
        try {

            if (view != null) {

                immersionBar
                        .statusBarDarkFont(true, 0.2f)
                        .titleBar(view)
                        .fitsSystemWindows(true)
                        .keyboardEnable(false)
                        .autoDarkModeEnable(true)
                        .init();
            }

//            else {
//
//                /***判断手机支不支持状态栏字体变色 */
//                if (ImmersionBar.isSupportStatusBarDarkFont()) {
//
//                    immersionBar
//                            .statusBarDarkFont(true, 0.2f)
//                            .fitsSystemWindows(true)
//                            .statusBarColor(R.color.cardview_light_background)
//                            .keyboardEnable(true)
//                            .autoDarkModeEnable(true)
//                            .init();
//                } else {
//
//                    /*** 针对 状态栏字体不能变色机型进行适配 */
//                    immersionBar
//                            .fitsSystemWindows(true)
//                            .statusBarDarkFont(false, 0.2f)
//                            .statusBarColor(R.color.vpi__light_theme)
//                            .keyboardEnable(true)
//                            .autoDarkModeEnable(true)
//                            .init();
//                }
//            }
        } catch (Exception ex) {

        }
    }
}
