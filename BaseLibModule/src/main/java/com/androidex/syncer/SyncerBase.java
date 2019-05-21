package com.androidex.syncer;

import android.os.Handler;

import com.androidex.util.TextUtil;
import com.ex.android.http.executer.HttpTaskExecuter;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.listener.HttpTaskStringListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 状态同步者基类
 * Created by yihaibin on 15/11/12.
 */
public abstract class SyncerBase<T, K> {

    private LinkedList<T> mLisns;
    private Map<String, ListenerRunnable> mRunnableMap;
    private HttpTaskExecuter mHttpTaskExecuter;
    private Handler mHandler;

    protected SyncerBase(){

        mLisns = new LinkedList<T>();
    }

    public boolean register(T lisn){

        return mLisns.add(lisn);
    }

    public boolean unRegister(T lisn){

        return mLisns.remove(lisn);
    }

    public void abortSyncTaskIfRunning(){

        abortAllHttpTask();
    }

    protected void release(){

        abortAllHttpTask();
        clearListeners();
        removeAllMsgRunnable();
    }

    protected void clearListeners(){

        mLisns.clear();
    }

    private void removeAllMsgRunnable() {

        if (mRunnableMap == null || mRunnableMap.isEmpty())
            return;

        Collection<ListenerRunnable> tasks = mRunnableMap.values();
        Iterator<ListenerRunnable> iterator = tasks.iterator();
        while (iterator.hasNext()) {

            mHandler.removeCallbacks(iterator.next());
        }

        mRunnableMap.clear();
    }

    protected HttpTaskExecuter getHttpTaskExecuter(){

        if (mHttpTaskExecuter == null)
            mHttpTaskExecuter = new HttpTaskExecuter();

        return mHttpTaskExecuter;
    }

    protected boolean isHttpTaskRunning(int what) {

        return mHttpTaskExecuter == null ? false : mHttpTaskExecuter.isHttpTaskRunning(what);
    }

    protected void abortHttpTask(int what) {

        if (mHttpTaskExecuter != null)
            mHttpTaskExecuter.abortHttpTask(what);
    }

    protected void abortAllHttpTask() {

        if (mHttpTaskExecuter != null)
            mHttpTaskExecuter.abortAllHttpTask();
    }

    protected boolean executeHttpTask(int what, HttpTaskParams params, HttpTaskStringListener<?> lisn){

        return getHttpTaskExecuter().executeHttpTask(what, params, false, lisn);
    }

    protected void callbackListener(boolean isAsync, String key, K obj){

        checkHelper();

        key = TextUtil.filterNull(key);
        ListenerRunnable runnable = mRunnableMap.get(key);
        if(runnable == null){

            runnable = new ListenerRunnable(key);
            mRunnableMap.put(key, runnable);
        }else{

            mHandler.removeCallbacks(runnable);
        }

        runnable.updateObj(obj);

        if(isAsync){

            mHandler.post(runnable);
        } else{

            runnable.run();
        }
    }

    private void checkHelper(){

        if(mRunnableMap == null)
            mRunnableMap = new HashMap<String, ListenerRunnable>();

        if(mHandler == null)
            mHandler = new Handler();
    }

    private class ListenerRunnable implements Runnable {

        private String key = TextUtil.TEXT_EMPTY;
        private Object obj;

        public ListenerRunnable(String key){

            this.key = TextUtil.filterNull(key);
        }

        public void updateObj(Object obj){

            this.obj = obj;
        }

        @Override
        public void run() {

            mRunnableMap.remove(key);
            onListenerNeedCallback(mLisns, key, (K) obj);
        }
    }

    protected abstract void onListenerNeedCallback(List<T> lisns, String key, K obj);
}
