package com.androidex.manager;

import com.androidex.util.CollectionUtil;

import java.util.ArrayList;

/**
 * activity回收器
 * Created by yihaibin on 16/4/17.
 */
public class ActivityRecycleManager {

    private static ActivityRecycleManager mInstance;

    private final int KEEP_SIZE = 8;

    private ArrayList<ActivityRecycleListener> mActivityList;

    private ActivityRecycleManager(){

        mActivityList = new ArrayList<ActivityRecycleListener>();
    }

    public static ActivityRecycleManager getInstance(){

        if(mInstance == null)
            mInstance = new ActivityRecycleManager();

        return mInstance;
    }

    public static void releaseInstance(){

        if(mInstance != null){

            mInstance.release();
            mInstance = null;
        }
    }

    public void add(ActivityRecycleListener activity){

        if(activity != null)
            mActivityList.add(activity);
    }

    public void remove(ActivityRecycleListener activity){

        if(activity != null)
            mActivityList.remove(activity);
    }

    public void triggerActivityRecycle(){

        if(mActivityList.size() > KEEP_SIZE){

            ActivityRecycleListener lisn = CollectionUtil.getItem(mActivityList, mActivityList.size() - KEEP_SIZE - 1);
            if(lisn != null)
                lisn.onActivityTriggerRecycle();
        }
    }

    private void release(){

        mActivityList.clear();
    }


    public static interface ActivityRecycleListener{

        void onActivityTriggerRecycle();
    }
}
