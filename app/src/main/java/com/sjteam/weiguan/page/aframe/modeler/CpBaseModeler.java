package com.sjteam.weiguan.page.aframe.modeler;

import com.ex.android.http.executer.HttpTaskExecuter;
import com.ex.android.http.executer.HttpTaskExecuterHost;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.listener.HttpTaskStringListener;

public class CpBaseModeler implements HttpTaskExecuterHost {

    private HttpTaskExecuter mHttpTaskExecuter;

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

        return false;
    }


    public void release() {

        abortAllHttpTask();
    }
}
