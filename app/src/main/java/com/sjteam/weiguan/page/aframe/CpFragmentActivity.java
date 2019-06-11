package com.sjteam.weiguan.page.aframe;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jzyd.lib.activity.JzydFragmentActivity;

/**
 * 优惠券FragmentActivity基类
 */
public abstract class CpFragmentActivity extends JzydFragmentActivity {


    private Handler mMainHandler;

    private boolean mPageBackEventEnable;
    private boolean mPagePvEventEnable;
    private boolean mOnPauseBackRecorded;


    @Override
    protected void onStart() {

        super.onStart();
    }

    /**
     * 全局退出事件埋点
     */
    @Override
    protected void onPause() {

        super.onPause();
        if (mPageBackEventEnable && isFinishing()) {

            mOnPauseBackRecorded = true;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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