package com.androidex.statusbar;

/**
 * 沉淀式状态栏
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/5/17 7:18 PM
 */
public interface StatusbarOwner {

    /**
     * 用户可见时候调用
     * On visible.
     */
    void onVisible();

    /**
     * 用户不可见时候调用
     * On invisible.
     */
    void onInvisible();

    /**
     * 初始化沉浸式代码
     * Init immersion bar.
     */
    void initStatusbar();

    /**
     * 是否可以实现沉浸式，当为true的时候才可以执行initStatusbar方法
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    boolean isStatusbarEnabled();
}
