package com.sjteam.weiguan.page.aframe.callback;

public interface CpCallback<T> {

    void onTaskPre();
    void onTaskResult(T t);
    void onTaskFailed(int failedCode, String msg);
}
