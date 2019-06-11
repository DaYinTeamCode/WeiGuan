package com.sjteam.weiguan.page.aframe.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sjteam.weiguan.page.aframe.modeler.CpBaseModeler;


public abstract class CpBasePresenter<V, M extends CpBaseModeler> {

    private V mViewer;
    private M mModeler;
    private boolean mIsAttachedViewer;
    private Handler mMainHandler;

    public CpBasePresenter(V viewer) {

        this(viewer, null);
    }

    protected CpBasePresenter(V viewer, M modeler) {

        mViewer = viewer;
        mModeler = modeler;
    }

    /**
     * 被界面绑定
     */
    public void onAttachedToViewer() {

        mIsAttachedViewer = true;
    }

    /**
     * 从界面剥离回调
     */
    public void onDetachedFromViewer() {

        clearMainHandlerAllMessage();
        releaseModeler();
        mIsAttachedViewer = false;
    }

    protected boolean isAttachedViewer() {

        return mIsAttachedViewer;
    }

    public V getViewer() {

        return mViewer;
    }

    protected M getModeler() {

        return mModeler;
    }

    private void releaseModeler() {

        if (mModeler != null) {
            mModeler.release();
        }
    }

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
                    if (mIsAttachedViewer) {
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
