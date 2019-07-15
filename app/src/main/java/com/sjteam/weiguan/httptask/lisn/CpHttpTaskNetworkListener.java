package com.sjteam.weiguan.httptask.lisn;

import com.androidex.util.DeviceUtil;
import com.ex.android.http.task.HttpTask;

/**
 * HttpTask网络状态监听器
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 3:47 PM
 */
public class CpHttpTaskNetworkListener implements HttpTask.HttpTaskNetworkListener {

    public CpHttpTaskNetworkListener() {

    }

    @Override
    public boolean onHttpTaskCheckNetworkEnable() {

        return DeviceUtil.isNetworkEnable();
    }
}
