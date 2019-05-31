package com.androidex.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.CollectionUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.ToastUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.ViewUtil;
import com.androidex.view.ExDecorView;
import com.ex.android.http.executer.HttpTaskExecuter;
import com.ex.android.http.executer.HttpTaskExecuterHost;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.listener.HttpTaskStringListener;
import com.ex.sdk.android.slideback.SlideBack;
import com.ex.sdk.android.slideback.impl.SlideBackCallBack;

import java.util.List;

/**
 * 根据Ex框架，扩展的基类Activity，提供titlebar、toast、view，httptask 相关常用的api(拷贝的ExActivity1)
 *
 * @author yhb
 */
public abstract class ExFragmentActivity extends FragmentActivity implements HttpTaskExecuterHost {

    private ExDecorView mExDecorView;
    private Fragment mContentFragment;
    private HttpTaskExecuter mHttpTaskExecuter;
    // 默认当前页面支持横划Back返回
    private boolean mIsCurPageSlidebackSupport = true;

    private View mStatusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initAndSetExDecorView();
        if (mIsCurPageSlidebackSupport) {

            /*** 侧滑删除 默认为页面有滑动 */
            SlideBack.register(this, true, new SlideBackCallBack() {
                @Override
                public void onSlideBack() {

                    onBackPressed();
                }
            });
        }

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onDestroy() {

        /*** 注销侧滑删除 */
        if (mIsCurPageSlidebackSupport)
            SlideBack.unregister(this);

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //super.onSaveInstanceState(outState);
        //fragmentActivity 靠该方法保存状态:fragment的复用等，取消状态的保存
    }


    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isStatusbarEnabled() {

        return true;
    }

    /***
     *  初始化沉淀式状态栏
     */
    protected void initStatusBar() {

        if (isStatusbarEnabled()) {

            StatusBarManager.getInstance()
                    .initStatusbar(this, getStatusbarView());
        }
    }

    /***
     *  设置Statusbar View
     *
     * @param view
     */
    protected void setStatusbarView(@Nullable View view) {

        if (view == null) {

            return;
        }
        mStatusBarView = view;
    }

    /***
     *  获取状态栏资源Id
     *
     * @return
     */
    protected View getStatusbarView() {

        return mStatusBarView;
    }

    private void initAndSetExDecorView() {

        mExDecorView = new ExDecorView(this);
        super.setContentView(mExDecorView, VglpUtil.getVglpMM());
    }

    @Override
    public void setContentView(int layoutResId) {

        this.setContentView(getLayoutInflater().inflate(layoutResId, null));
    }

    @Override
    public void setContentView(View view) {

        mExDecorView.setContentView(view);
        initData();
        initTitleView();
        initContentView();
        initStatusBar();
    }

    protected Fragment setContentFragment(Class<?> clazz) {

        return setContentFragment(clazz.getName());
    }

    protected Fragment setContentFragment(String fragmentName) {

        return setContentFragment(Fragment.instantiate(this, fragmentName, getIntent().getExtras()));
    }

    protected Fragment setContentFragment(String fragmentName, Bundle bundle) {

        return setContentFragment(Fragment.instantiate(this, fragmentName, bundle));
    }

    protected Fragment setContentFragment(Fragment fragment) {

        FrameLayout container = new FrameLayout(this);
        container.setId(R.id.ex_decor_view_fragment_container_act);
        mExDecorView.setContentView(container);

        addFragment(R.id.ex_decor_view_fragment_container_act, fragment);
        mContentFragment = fragment;

        initData();
        initTitleView();
        initContentView();
        initStatusBar();

        return fragment;
    }

    public void removeContentFragment() {

        removeFragment(mContentFragment);
        mContentFragment = null;
    }

    protected boolean hasContentFragment() {

        return mContentFragment != null && mContentFragment.isAdded();
    }

    /**
     * 需要在子类调用super.onCreate()之前调用才生效！
     * <p>
     * 设置当前页面是否需要手势Back返回
     *
     * @param isSupport
     */
    protected void setCurPageSlidebackSupport(boolean isSupport) {

        mIsCurPageSlidebackSupport = isSupport;
    }

    protected abstract void initData();

    protected abstract void initTitleView();

    protected abstract void initContentView();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (CollectionUtil.isEmpty(fragments))
            return;

        Fragment fragment = null;
        for (int i = 0; i < fragments.size(); i++) {

            fragment = fragments.get(i);
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (isFinishing())
            abortAllHttpTask();
    }

    @Override
    public void onBackPressed() {

        try {
            super.onBackPressed();
        } catch (Exception e) {
            if (LogMgr.isDebug())
                e.printStackTrace();
        }
    }

    /**
     * fragment part
     */

    public Fragment getContentFragment() {

        return mContentFragment;
    }

    public void addFragment(int frameId, Fragment fragment) {

        if (isFinishing() || fragment == null)
            return;

        getSupportFragmentManager().beginTransaction().add(frameId, fragment).commitAllowingStateLoss();
    }

    public void addFragment(int frameId, Fragment fragment, String tag) {

        if (isFinishing() || fragment == null)
            return;

        getSupportFragmentManager().beginTransaction().add(frameId, fragment, tag).commitAllowingStateLoss();
    }

    public void removeFragment(Fragment fragment) {

        if (isFinishing() || fragment == null)
            return;

        if (fragment.isAdded())
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    public void showFragment(Fragment fragment) {

        if (isFinishing() || fragment == null || !fragment.isHidden())
            return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commitAllowingStateLoss();
    }

    public void hideFragment(Fragment fragment) {

        if (isFinishing() || fragment == null || fragment.isHidden())
            return;

        try {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragment);
            transaction.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public Fragment findFragmentByTag(String tag) {

        if (tag == null)
            return null;

        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public Fragment findFragmentById(int id) {

        return getSupportFragmentManager().findFragmentById(id);
    }

    /**
     * http task api
     */

    public boolean executeHttpTask(int what, HttpTaskParams params, HttpTaskStringListener<?> lisn) {

        return executeHttpTask(what, params, false, lisn);
    }

    public boolean executeHttpTaskCache(int what, HttpTaskParams params, HttpTaskStringListener<?> lisn) {

        return executeHttpTask(what, params, true, lisn);
    }

    public boolean executeHttpTask(int what, HttpTaskParams params, boolean cacheOnly, HttpTaskStringListener<?> lisn) {

        return isFinishing() ? false : getHttpTaskExecuter().executeHttpTask(what, params, cacheOnly, lisn);
    }

    public boolean isHttpTaskRunning(int what) {

        return mHttpTaskExecuter == null ? false : mHttpTaskExecuter.isHttpTaskRunning(what);
    }

    public void abortHttpTask(int what) {

        if (mHttpTaskExecuter != null)
            mHttpTaskExecuter.abortHttpTask(what);
    }

    public void abortAllHttpTask() {

        if (mHttpTaskExecuter != null)
            mHttpTaskExecuter.abortAllHttpTask();
    }

    public HttpTaskExecuter getHttpTaskExecuter() {

        if (mHttpTaskExecuter == null)
            mHttpTaskExecuter = new HttpTaskExecuter(this);

        return mHttpTaskExecuter;
    }

    /**
     * HttpTaskExecuter的宿主接口也有该方法
     * 这里重载来标识一下
     *
     * @return
     */
    @Override
    public boolean isFinishing() {

        return super.isFinishing();
    }

    /**
     * status bar api
     */

    public void setStatusBarColor(int color) {

        DeviceUtil.setStatusBarColor(this, color);
    }

    public void setStatusBarColorResource(int colorResId) {

        DeviceUtil.setStatusBarColorResource(this, colorResId);
    }

    public boolean setStatusBarTranslucent(boolean translucent, boolean kitkatEnable) {

        if (DeviceUtil.setStatusBarTranslucent(this, translucent, kitkatEnable)) {

            return mExDecorView.setTitleViewSupportStatusBarTrans(translucent, kitkatEnable);
        } else {

            return false;
        }
    }

    public int getStatusBarHeight() {

        return DeviceUtil.STATUS_BAR_HEIGHT;
    }

    /**
     * get decor view part
     */

    public ExDecorView getExDecorView() {

        return mExDecorView;
    }

    protected FrameLayout getTitleView() {

        return mExDecorView.getTitleView();
    }

    public FrameLayout getTitleViewMaybeNull() {

        return mExDecorView.getTitleViewMaybeNull();
    }

    protected LinearLayout getTitleLeftView() {

        return mExDecorView.getTitleLeftView();
    }

    protected LinearLayout getTitleMiddleView() {

        return mExDecorView.getTitleMiddleView();
    }

    protected LinearLayout getTitleRightView() {

        return mExDecorView.getTitleRightView();
    }

    protected int getTitleViewHeight() {

        return mExDecorView.getTitleHeight();
    }

    protected boolean isTitleViewSupportStatusBarTrans() {

        return mExDecorView.isTitleViewSupportStatusBarTrans();
    }

    protected void goneTitleView() {

        mExDecorView.goneTitleView();
    }

    protected void showTitleView() {

        mExDecorView.showTitleView();
    }

    /**
     * add title view left part
     */

    protected ImageView addTitleLeftImageView(int icResId, OnClickListener lisn) {

        return mExDecorView.addTitleLeftImageView(icResId, lisn);
    }

    protected ImageView addTitleLeftImageViewHoriWrap(int icResId, OnClickListener lisn) {

        return mExDecorView.addTitleLeftImageViewHoriWrap(icResId, lisn);
    }

    protected TextView addTitleLeftTextView(int textRid, OnClickListener lisn) {

        return mExDecorView.addTitleLeftTextView(textRid, lisn);
    }

    protected TextView addTitleLeftTextView(CharSequence text, OnClickListener lisn) {

        return mExDecorView.addTitleLeftTextView(text, lisn);
    }

    protected void addTitleLeftView(View v) {

        mExDecorView.addTitleLeftView(v);
    }

    protected void addTitleLeftView(View v, LinearLayout.LayoutParams lllp) {

        mExDecorView.addTitleLeftView(v, lllp);
    }

    protected ImageView addTitleLeftBackView() {

        return mExDecorView.addTitleLeftImageViewBack(new OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    protected ImageView addTitleLeftBackView(OnClickListener clickLisn) {

        return mExDecorView.addTitleLeftImageViewBack(clickLisn);
    }

    /**
     * add title view middle part
     */

    protected ImageView addTitleMiddleImageViewWithBack(int icResId) {

        addTitleLeftBackView();
        return addTitleMiddleImageView(icResId);
    }

    protected ImageView addTitleMiddleImageView(int icResId) {

        return mExDecorView.addTitleMiddleImageView(icResId);
    }

    protected ImageView addTitleMiddleImageViewHoriWrapWithBack(int icResId) {

        addTitleLeftBackView();
        return addTitleMiddleImageViewHoriWrap(icResId);
    }

    protected ImageView addTitleMiddleImageViewHoriWrap(int icResId) {

        return mExDecorView.addTitleMiddleImageViewHoriWrap(icResId);
    }

    protected TextView addTitleMiddleTextViewWithBack(int textRid) {

        return addTitleMiddleTextViewWithBack(getResources().getText(textRid));
    }

    protected TextView addTitleMiddleTextView(int textRid) {

        return mExDecorView.addTitleMiddleTextView(textRid);
    }

    protected TextView addTitleMiddleTextView(CharSequence text) {

        return mExDecorView.addTitleMiddleTextView(text);
    }

    protected TextView addTitleMiddleTextViewWithBack(CharSequence text) {

        addTitleLeftBackView();
        return mExDecorView.addTitleMiddleTextView(text);
    }

    protected TextView addTitleMiddleTextViewMainStyle(int textResId) {

        return mExDecorView.addTitleMiddleTextViewMainStyle(textResId);
    }

    protected TextView addTitleMiddleTextViewMainStyle(CharSequence text) {

        return mExDecorView.addTitleMiddleTextViewMainStyle(text);
    }

    protected TextView addTitleMiddleTextViewSubStyle(int textResId) {

        return mExDecorView.addTitleMiddleTextViewSubStyle(textResId);
    }

    protected TextView addTitleMiddleTextViewSubStyle(CharSequence text) {

        return mExDecorView.addTitleMiddleTextViewSubStyle(text);
    }

    protected void addTitleMiddleViewWithBack(View v) {

        addTitleLeftBackView();
        addTitleMiddleView(v);
    }

    protected void addTitleMiddleView(View v) {

        mExDecorView.addTitleMiddleView(v);
    }

    protected void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp) {

        mExDecorView.addTitleMiddleView(v, lllp);
    }

    protected void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp, boolean horiCenter) {

        mExDecorView.addTitleMiddleView(v, lllp, horiCenter);
    }

    /**
     * add title view right part
     */

    protected ImageView addTitleRightImageView(int icResId, OnClickListener lisn) {

        return mExDecorView.addTitleRightImageView(icResId, lisn);
    }

    protected ImageView addTitleRightImageViewHoriWrap(int icResId, OnClickListener lisn) {

        return mExDecorView.addTitleRightImageViewHoriWrap(icResId, lisn);
    }

    protected TextView addTitleRightTextView(int textRid, OnClickListener lisn) {

        return mExDecorView.addTitleRightTextView(textRid, lisn);
    }

    protected TextView addTitleRightTextView(CharSequence text, OnClickListener lisn) {

        return mExDecorView.addTitleRightTextView(text, lisn);
    }

    protected void addTitleRightView(View v) {

        mExDecorView.addTitleRightView(v);
    }

    protected void addTitleRightView(View v, LinearLayout.LayoutParams lllp) {

        mExDecorView.addTitleRightView(v, lllp);
    }

    /**
     * receiver
     */

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {

        try {
            return super.registerReceiver(receiver, filter);
        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.d("~~registerReceiver error, msg=" + e.getMessage());
        }
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {

        try {
            super.unregisterReceiver(receiver);
        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.d("~~unregisterReceiver error, msg=" + e.getMessage());
        }
    }

    /**
     * toast part
     */

    protected void showToast(int rid) {

        ToastUtil.showToast(rid);
    }

    protected void showToast(String text) {

        ToastUtil.showToast(text);
    }

    /**
     * view util part
     */

    protected void showView(View v) {

        ViewUtil.showView(v);
    }

    protected void hideView(View v) {

        ViewUtil.hideView(v);
    }

    protected void goneView(View v) {

        ViewUtil.goneView(v);
    }

    protected void showImageView(ImageView v, int imageResId) {

        ViewUtil.showImageView(v, imageResId);
    }

    protected void showImageView(ImageView v, Drawable drawable) {

        ViewUtil.showImageView(v, drawable);
    }

    protected void hideImageView(ImageView v) {

        ViewUtil.hideImageView(v);
    }

    protected void goneImageView(ImageView v) {

        ViewUtil.goneImageView(v);
    }

    /**
     * tag part
     */

    public String simpleTag() {

        return getClass().getSimpleName();
    }

    public String tag() {

        return getClass().getName();
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), simpleTag() + " finalize()");
    }
}
