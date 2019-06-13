package com.sjteam.weiguan.page.aframe;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidex.imageloader.fresco.FrescoHelper;
import com.androidex.util.DensityUtil;
import com.androidex.util.DeviceUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.ViewUtil;
import com.ex.android.http.task.HttpTaskStatus;
import com.jzyd.lib.activity.JzydHttpFrameFragment;
import com.jzyd.lib.loading.SqkbPageLoadingView;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.view.CpTextView;

/**
 * 该类实现半糖网络请求逻辑框架，
 * 使用View填充了页面基本元素的样式：网络加载中、网络数据显示、网络数据不可用、网络加载失败四种状态。
 * 网络加载laoding使用系统自带的loading样式
 * 网络数据显示的布局，子类可以通过setContentViewXX等方法设置, 布局的刷新子类通过实现invalidateContent(T result)覆写来实现
 * 网络数据不可用状态只支持图片提示、本类提供一个默认的实现、子类可以通过setDisabledImageResId(int resId)来更换
 * 网络数据加载失败状态只支持图片提示、本类提供一个默认的实现、子类可以通过setFailedImageResId(int resId)来更换
 * <p>
 * 本类还提供了网络加载失败和网络数据不可用状态的点击重试动作，子类可通过覆写onTipViewClick()方法来更换逻辑
 *
 * @param <T>
 * @author yhb
 */
public abstract class HttpFrameFragment<T> extends JzydHttpFrameFragment<T> {

    private Handler mMainHandler;
    private View mContentView;
    private CpTextView mTvTip;
    private SqkbPageLoadingView mPbLoading;
    private int mTipResId;
    private int mFailedNetworkImageResId, mFailedNetworkTextResId;
    private int mFailedDataErrorTextResId, mFailedDataErrorImageResId;
    private int mDisabledTextResId, mDisabledImageResId;

    @Override
    protected void setContentView(View view) {

        initView(view, VglpUtil.getFllpMM());
    }

    protected void setContentView(View view, FrameLayout.LayoutParams layoutParams) {

        initView(view, layoutParams);
    }

    private void initView(View view, FrameLayout.LayoutParams layoutParams) {

        addHttpFrameViewsToExDecorView(view, layoutParams);
        initDataPre();
        initData();
        initTitleView();
        initContentView();
        initStatusBar();
    }

    protected void initDataPre() {

        //nothing
    }

    protected void addHttpFrameViewsToExDecorView(View v, FrameLayout.LayoutParams layoutParams) {

        //add content view
        mContentView = v;
        getExDecorView().addContentView(v, layoutParams);

        //add tip view
        mTvTip = new CpTextView(getActivity());
        mTvTip.setCompoundDrawablePadding(DensityUtil.dip2px(13));
        mTvTip.setTextColor(0XFF999999);
        mTvTip.setGravity(Gravity.CENTER_HORIZONTAL);
        mTvTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mTvTip.setVisibility(View.INVISIBLE);//默认隐藏
        mTvTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onTipViewClick();
            }
        });
        getExDecorView().addContentView(mTvTip, VglpUtil.getFllpSS(VglpUtil.W, VglpUtil.W, Gravity.CENTER));

        //add progress bar
        mPbLoading = new SqkbPageLoadingView(getActivity());
        getExDecorView().addContentView(mPbLoading, VglpUtil.getFllpSS(DensityUtil.dip2px(46), DensityUtil.dip2px(8), Gravity.CENTER));

        //设置无网提示
        mFailedNetworkImageResId = R.drawable.core_ic_page_tip_network_none;
        mFailedNetworkTextResId = R.string.page_tip_network_none;
        //设置数据获取失败提示
        mFailedDataErrorImageResId = R.drawable.core_ic_page_tip_failure;
        mFailedDataErrorTextResId = R.string.page_tip_data_error;
        //mDisabledImageResId = R.drawable.ic_tip_null;//没有默认的数据不可用提示图
    }

    protected View getFrameContentView() {

        return mContentView;
    }

    protected void setFrameContentView(View view) {

        mContentView = view;
    }

    protected TextView getFrameTipView() {

        return mTvTip;
    }

    protected View getFrameLoadingView() {

        return mPbLoading;
    }

    @Override
    protected void showLoading() {

        //尝试修复umeng 报 NotFoundException: File res/drawable/core_anim_page_loadingading.xml from drawable resource ID #0x7f060002
        if (!isFinishing())
            mPbLoading.show();
    }

    @Override
    protected void hideLoading() {

        mPbLoading.hide();
    }

    @Override
    protected void showContent() {

        ViewUtil.showView(mContentView);
    }

    @Override
    protected void hideContent() {

        ViewUtil.hideView(mContentView);
    }

    @Override
    protected void showContentDisable() {

        setTipDrawableResource(mDisabledImageResId, mDisabledTextResId);
    }

    @Override
    protected void hideContentDisable() {
        if (mTvTip == null) {
            return;
        }
        ViewUtil.hideView(mTvTip);
        mTvTip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    @Override
    protected void showFailed(int failedCode, String msg) {

        int failedImageResId;
        int failedTextResId;
        if (failedCode == HttpTaskStatus.TASK_FAILED_NETWORK_DISABLE) {

            failedImageResId = mFailedNetworkImageResId;
            failedTextResId = mFailedNetworkTextResId;
        } else {

            failedImageResId = mFailedDataErrorImageResId;
            failedTextResId = mFailedDataErrorTextResId;
        }

        setTipDrawableResource(failedImageResId, failedTextResId);
    }

    protected void setTipDrawableResource(int imageResId, int textResId) {

        try {

            mTipResId = imageResId;

            if (mTipResId == 0)
                mTvTip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            else
                mTvTip.setCompoundDrawablesWithIntrinsicBounds(0, imageResId, 0, 0);

            if (textResId == 0)
                mTvTip.setText(TextUtil.TEXT_EMPTY);
            else
                mTvTip.setText(textResId);

            ViewUtil.showView(mTvTip);

        } catch (Throwable t) {

            //大部分情况是OOM，一旦OOM，清除所有图片缓存
            FrescoHelper.clearMemoryCache();

            System.gc();

            if (LogMgr.isDebug())
                LogMgr.e(simpleTag(), "showFailed error: " + t.getMessage());
        }
    }

    @Override
    protected void hideFailed() {
        if (mTvTip == null) {
            return;
        }
        ViewUtil.hideView(mTvTip);
        mTvTip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mTvTip.setText(TextUtil.TEXT_EMPTY);
    }

    public void setFailedImageResId(int resId) {

        mFailedDataErrorImageResId = resId;
    }

    public void setFailedTextResId(int resId) {

        mFailedDataErrorTextResId = resId;
    }

    public void setDisabledImageResId(int resId) {

        mDisabledImageResId = resId;
    }

    public void setDisabledTextResId(int resId) {

        mDisabledTextResId = resId;
    }

    protected void onTipViewClick() {

        if (mTipResId == 0)
            return;

        if (DeviceUtil.isNetworkDisable()) {

            showToast(R.string.toast_network_none);
            return;
        }

        if (isFrameNeedCache()) {

            executeFrameRefreshAndCache();
        } else {

            executeFrameRefresh();
        }
    }

    public boolean isDataDisableTip() {

        return mTipResId == mDisabledImageResId;
    }


    //****** 全局事件 ******//


    private boolean mPageBackEventEnable;
    private boolean mPagePvEventEnable;
    private boolean mOnPauseBackRecorded;

    @Override
    protected void onSupportShowToUserChangedAfter(boolean isShowToUser, int from) {

        super.onSupportShowToUserChangedAfter(isShowToUser, from);

    }

    /**
     * 全局退出事件埋点
     */
    @Override
    public void onPause() {

        super.onPause();
        if (mPageBackEventEnable && isFinishing()) {

            mOnPauseBackRecorded = true;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        clearMainHandlerAllMessage();
    }


    /**
     * 设置是否发送用用的页面back事件
     *
     * @param enable
     */
    public void setPageBackEventEnable(boolean enable) {

        this.mPageBackEventEnable = enable;
    }

    /**
     * 设置是否发送通用的页面pv事件
     *
     * @param enable
     */
    public void setPageCommonPvEventEnable(boolean enable) {

        this.mPagePvEventEnable = enable;
    }


    //**************************** handler api ****************************


    /**
     * 返回页面持有的唯一主线程handler
     *
     * @return
     */
    protected Handler getMainHandler() {

        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (!isFinishing()) {
                        onMainHandlerMessage(msg);
                    }
                }
            };
        }
        return mMainHandler;
    }

    /**
     * 清空Handler所有消息
     */
    protected void clearMainHandlerAllMessage() {

        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 处理主线程消息
     *
     * @param msg
     */
    protected void onMainHandlerMessage(Message msg) {

        //nothing
    }
}
