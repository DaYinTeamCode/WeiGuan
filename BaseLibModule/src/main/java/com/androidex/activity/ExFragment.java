package com.androidex.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.CollectionUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.androidex.util.ToastUtil;
import com.androidex.util.ViewUtil;
import com.androidex.view.ExDecorView;
import com.ex.android.http.executer.HttpTaskExecuter;
import com.ex.android.http.executer.HttpTaskExecuterHost;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.listener.HttpTaskStringListener;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 根据Ex框架，扩展的基类Activity，提供toast、view，httptask 相关常用的api
 * #这里暂时将ExDecorView去掉，因为其实没啥用
 *
 * @author yhb
 */
public abstract class ExFragment extends Fragment implements HttpTaskExecuterHost {

    private ExDecorView mExDecorView;
    private Fragment mContentFragment;
    private HttpTaskExecuter mHttpTaskExecuter;
    private View mStatusBarView;
    protected Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mExDecorView = onCreateExDecorView();
        return mExDecorView;
    }

    protected ExDecorView onCreateExDecorView() {

        return new ExDecorView(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mIsSupportOnCreateLifecycle = true;
    }

    @Override
    public void onStart() {

        super.onStart();
        mIsSupportStarted = true;
        callbackSupportShowToUserChanged(isSupportShowToUser(), SHOW_TO_USER_FROM_ONSTART);
        mIsSupportOnCreateLifecycle = false;
    }

    @Override
    public void onPause() {

        super.onPause();
        mIsSupportOnCreateLifecycle = false;
    }

    @Override
    public void onStop() {

        super.onStop();
        mIsSupportStarted = false;
        mIsSupportOnCreateLifecycle = false;
        callbackSupportShowToUserChanged(isSupportShowToUser(), SHOW_TO_USER_FROM_ONSTART);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        abortAllHttpTask();
        if (unbinder != null) {

            unbinder.unbind();
        }
    }

    protected void setContentView(int layoutResId) {

        setContentView(getActivity().getLayoutInflater().inflate(layoutResId, null));
    }

    protected void setContentView(View view) {

        mExDecorView.setContentView(view);
        callbackInit();
    }

    protected Fragment setContentFragment(Class<?> clazz) {

        return setContentFragment(clazz.getName());
    }

    protected Fragment setContentFragment(String fragmentName) {

        return setContentFragment(Fragment.instantiate(getActivity(), fragmentName));
    }

    protected Fragment setContentFragment(String fragmentName, Bundle bundle) {

        return setContentFragment(Fragment.instantiate(getActivity(), fragmentName, bundle));
    }

    protected Fragment setContentFragment(Fragment fragment) {

        setFragmentFrameViewToContentView();
        resetContentFragment(fragment);
        callbackInit();
        return fragment;
    }

    protected void setContentFragmentFrameView() {

        setFragmentFrameViewToContentView();
        callbackInit();
    }

    private void setFragmentFrameViewToContentView() {

        FrameLayout container = new FrameLayout(getActivity());
        container.setId(R.id.ex_decor_view_fragment_container_fra);
        mExDecorView.setContentView(container);
    }

    private void callbackInit() {

        //            注解绑定
        unbinder = ButterKnife.bind(this, mExDecorView);
        initData();
        initTitleView();
        initContentView();
        initStatusBar();
    }

    protected abstract void initData();

    protected abstract void initTitleView();

    protected abstract void initContentView();


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
                    .initStatusbar(this, getStatusBarView());
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
    protected View getStatusBarView() {

        return mStatusBarView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (CollectionUtil.isEmpty(fragments))
            return;

        for (int i = 0; i < fragments.size(); i++) {

            if (fragments.get(i) != null)
                fragments.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * fragment part
     */

    protected Fragment getContentFragment() {

        return mContentFragment;
    }

    protected FrameLayout getContentFragmentFrameView() {

        return (FrameLayout) findViewById(R.id.ex_decor_view_fragment_container_fra);
    }

    protected void resetContentFragment(Fragment newFra) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (newFra != null)
            ft.add(R.id.ex_decor_view_fragment_container_fra, newFra);

        if (mContentFragment != null)
            ft.remove(mContentFragment);

        mContentFragment = newFra;

        ft.commitAllowingStateLoss();
    }

    protected void addFragment(int frameId, Fragment f) {

        if (f != null)
            getChildFragmentManager().beginTransaction().add(frameId, f).commitAllowingStateLoss();
    }

    protected void addFragment(int frameId, Fragment f, String tag) {

        if (f != null)
            getChildFragmentManager().beginTransaction().add(frameId, f, tag).commitAllowingStateLoss();
    }

    protected void replaceFragment(int containerViewId, Fragment f) {

        if (f != null)
            getChildFragmentManager().beginTransaction().replace(containerViewId, f).commitAllowingStateLoss();
    }

    protected void removeFragment(Fragment f) {

        if (f != null)
            getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }

    protected Fragment findFragmentByTag(String tag) {

        if (tag == null)
            return null;

        return getChildFragmentManager().findFragmentByTag(tag);
    }

    protected Fragment findFragmentById(int id) {

        return getChildFragmentManager().findFragmentById(id);
    }

    public void showFragment(Fragment fragment) {

        if (fragment == null || !fragment.isHidden())
            return;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commitAllowingStateLoss();
    }

    public void hideFragment(Fragment fragment) {

        if (fragment == null || fragment.isHidden())
            return;

        try {

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.hide(fragment);
            transaction.commitAllowingStateLoss();

        } catch (IllegalStateException e) {

            e.printStackTrace();
        }
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

    private boolean executeHttpTask(int what, HttpTaskParams params, boolean cacheOnly, HttpTaskStringListener<?> lisn) {

        return getHttpTaskExecuter().executeHttpTask(what, params, cacheOnly, lisn);
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

    @Override
    public boolean isFinishing() {

        return isActivityFinishing() || isRemoving();
    }

    private boolean isActivityFinishing() {

        return getActivity() == null || getActivity().isFinishing();
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

    protected void goneTitleView() {

        mExDecorView.goneTitleView();
    }

    protected void showTitleView() {

        mExDecorView.showTitleView();
    }

    public boolean setTitleViewSupportStatusBarTrans(boolean support, boolean kitkatEnable) {

        return mExDecorView.setTitleViewSupportStatusBarTrans(support, kitkatEnable);
    }

    protected int getTitleViewHeight() {

        return mExDecorView.getTitleHeight();
    }

    protected boolean isTitleViewSupportStatusBarTrans() {

        return mExDecorView.isTitleViewSupportStatusBarTrans();
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

                finishActivity();
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

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {

        try {
            return getActivity().registerReceiver(receiver, filter);
        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.e(simpleTag(), "registerReceiver error, msg=" + e.getMessage());
        }
        return null;
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {

        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.e(simpleTag(), "unregisterReceiver error, msg=" + e.getMessage());
        }
    }

    /**
     * viewpager选中
     * 需要自己手动触发回调
     *
     * @param selected
     */
    public void onPageSelectChanged(boolean selected) {

        //nothing
    }

    /**
     * viewpager选中后停止滚动
     * 需要自己手动触发回调
     */
    public void onPageSelectedScrollIdle() {

        //nothing
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
     * new help method
     */

    protected View findViewById(int id) {

        return mExDecorView.findViewById(id);
    }

    protected boolean finishActivity() {

        if (getActivity() == null) {

            return false;
        } else {

            getActivity().finish();
            return true;
        }
    }

    protected boolean finishActivity(int resultCode) {

        return finishActivity(resultCode, null);
    }

    protected boolean finishActivity(int resultCode, Intent intent) {

        if (getActivity() == null) {

            return false;
        } else {

            if (intent == null)
                getActivity().setResult(resultCode);
            else
                getActivity().setResult(resultCode, intent);

            getActivity().finish();
            return true;
        }
    }

    /**
     * argument part
     */

    public int getArgumentInt(String key) {

        return getArgumentInt(key, 0);
    }

    public long getArgumentLong(String key) {

        return getArgumentLong(key, 0);
    }

    public long getArgumentLong(String key, long defValue) {

        return getArguments() == null ? defValue : getArguments().getLong(key, defValue);
    }

    public int getArgumentInt(String key, int defValue) {

        return getArguments() == null ? defValue : getArguments().getInt(key, defValue);
    }

    public float getArgumentFloat(String key) {

        return getArgumentFloat(key, 0);
    }

    public float getArgumentFloat(String key, float defValue) {

        return getArguments() == null ? defValue : getArguments().getFloat(key, defValue);
    }

    public double getArgumentDouble(String key) {

        return getArgumentDouble(key, 0);
    }

    public double getArgumentDouble(String key, double defValue) {

        return getArguments() == null ? defValue : getArguments().getDouble(key, defValue);
    }

    public boolean getArgumentBoolean(String key) {

        return getArgumentBoolean(key, false);
    }

    public boolean getArgumentBoolean(String key, boolean defValue) {

        return getArguments() == null ? defValue : getArguments().getBoolean(key, defValue);
    }

    public String getArgumentString(String key) {

        return getArgumentString(key, TextUtil.TEXT_EMPTY);
    }

    public String getArgumentString(String key, String defValue) {

        return getArguments() == null ? defValue : getArguments().getString(key, defValue);
    }

    public Serializable getArgumentSerializable(String key) {

        return getArguments() == null ? null : getArguments().getSerializable(key);
    }

    public Parcelable getArgumentParcelable(String key) {

        return getArguments() == null ? null : getArguments().getParcelable(key);
    }

    public String[] getArgumentStringArray(String key) {

        return getArguments() == null ? null : getArguments().getStringArray(key);
    }

    public int[] getArgumentIntArray(String key) {

        return getArguments() == null ? null : getArguments().getIntArray(key);
    }

    /*
     * 空实现，供子类覆盖 part
     */

    /**
     * fragment内容滚动至顶部
     */
    public void scrollTop() {

        //nothing
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

    /**
     * 新增
     */
    public static final int SHOW_TO_USER_FROM_ONSTART = 1;
    public static final int SHOW_TO_USER_FROM_PAGE_SELECT = 2;
    public static final int SHOW_TO_USER_FROM_HIDDEN = 3;
    public static final int SHOW_TO_USER_FROM_PARENT = 4;

    private boolean mIsSupportStarted;
    private boolean mIsSupportOnCreateLifecycle;
    private boolean mIsSupportVisibleToUser;
    private boolean mIsSupportViewPagerMode;
    private boolean mIsSupportShowToUser;
    private boolean mIsSupportParentShow = true;

    private boolean isSupportParentShow() {

        return mIsSupportParentShow;
    }

    public boolean isSupportOnCreateLifecycle() {

        return mIsSupportOnCreateLifecycle;
    }

    public boolean isSupportStarted() {

        return mIsSupportStarted;
    }

    public void setSupportViewPagerMode(boolean isViewPagerMode) {

        mIsSupportViewPagerMode = isViewPagerMode;
    }

    public boolean isSupportViewPagerMode() {

        return mIsSupportViewPagerMode;
    }

    /**
     * 在ViewPager中有调用该函数:
     * 手势滑动 先调pageselected，再调 setUserVisibleHint（不一定）
     * 通过函数 setCurrentItem 先调 setUserVisibleHint，再调先调pageselected（不一定）
     * 该函数调用好像都早于生命周期回调,当ViewPager切换时都会先调用fra的该函数(不管是将要可见的,还是将要不可见的)
     * 在创建fra时，都会先调用setUserVisibleHint(false),早于生命周期的调用.
     * 当该fra是要显示的,则会再调setUserVisibleHint(true),早于生命周期的调用。(但此时调用getUserVisibleHint()返回的是false,非常奇怪,有时间再研究)
     * 在destroy时，
     * fragment 生命周期函数不会回调
     * 父fragment hidden变化不会回调
     * 容器view隐藏变化也不会回调
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        setSupportVisibleToUser(isVisibleToUser);
        if (isSupportViewPagerMode()) {
            if (isAdded())
                callbackSupportShowToUserChanged(isSupportShowToUser(), SHOW_TO_USER_FROM_PAGE_SELECT);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);
        callbackSupportShowToUserChanged(isSupportShowToUser(), SHOW_TO_USER_FROM_HIDDEN);
    }

    private void setSupportVisibleToUser(boolean isVisibleToUser) {

        mIsSupportVisibleToUser = isVisibleToUser;
    }

    private boolean isSupportVisibleToUser() {

        return mIsSupportVisibleToUser;
    }

    public boolean isSupportPageSelected() {

        return isSupportViewPagerMode() && isSupportVisibleToUser();
    }

    public boolean isSupportShowToUser() {

        return isSupportParentShow() && isAdded() && isSupportStarted() && !isHidden()
                && (!isSupportViewPagerMode() || isSupportPageSelected());
    }

    private void callbackSupportShowToUserChanged(boolean isShowToUser, int from) {

        if (mIsSupportShowToUser != isShowToUser) {
            mIsSupportShowToUser = isShowToUser;
            onSupportShowToUserChanged(isShowToUser, from);
            onSupportShowToUserChangedAfter(isShowToUser, from);
        }
    }

    public void performSupportParentShowChanged(boolean parentShow) {

        mIsSupportParentShow = parentShow;
        callbackSupportShowToUserChanged(isSupportShowToUser(), SHOW_TO_USER_FROM_PARENT);
    }

    protected void onSupportShowToUserChanged(boolean isShowToUser, int from) {
        //nothing
    }

    protected void onSupportShowToUserChangedAfter(boolean isShowToUser, int from) {
        //nothing
    }

    /**
     * 供小猪使用
     *
     * @param hidden
     */
    public void onParentFragmentHiddenChanged(boolean hidden) {

    }
}
