package com.sjteam.weiguan.page.aframe.callback;

import com.jzyd.lib.httptask.JzydJsonListener;

public abstract class CpJsonListener<T, R> extends JzydJsonListener<T> {

    private CpCallback<R> mCallback;

    public CpJsonListener(Class<?> clazz, CpCallback<R> callback) {

        super(clazz);
        mCallback = callback;
    }

    @Override
    public void onTaskPre() {

        super.onTaskPre();
        if (mCallback != null) {

            mCallback.onTaskPre();
        }
    }

    @Override
    public void onTaskResult(T result) {

        R covertResult = onTaskResultConvert(result);
        if (mCallback != null) {

            mCallback.onTaskResult(covertResult);
        }
    }

    @Override
    public void onTaskFailed(int failedCode, String msg) {

        if (mCallback != null) {

            mCallback.onTaskFailed(failedCode, msg);
        }
    }

    protected abstract R onTaskResultConvert(T t);
}
