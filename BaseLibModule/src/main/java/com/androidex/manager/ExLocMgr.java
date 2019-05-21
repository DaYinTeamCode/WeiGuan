package com.androidex.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.androidex.util.CollectionUtil;
import com.androidex.util.LogMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 经纬度位置管理器
 * Created by yihaibin on 2018/6/18.
 */

public class ExLocMgr implements LocationListener {

    private static ExLocMgr mInstance;

    private LocationManager mSysLocMgr;
    private boolean mIsRequestLocUpdate;
    private Handler mHandler = new Handler();
    private List<Listener> mListenerList;
    private Runnable mLocLisnRemoveRunnable = new Runnable() {
        @Override
        public void run() {

            onRequestLocationUpdatesTimeout();
        }
    };

    private ExLocMgr(Context context) {

        mSysLocMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mListenerList = new ArrayList<>();
    }

    public static ExLocMgr getInstance(Context context) {

        if (mInstance == null)
            mInstance = new ExLocMgr(context);

        return mInstance;
    }

    public static void releaseInstance() {

        if (mInstance != null) {

            mInstance.release();
            mInstance = null;
        }
    }

    private void release() {

        if (mIsRequestLocUpdate) {

            removeLocationUpdateListener();
            mHandler.removeCallbacks(mLocLisnRemoveRunnable);
            clearListener();
            mIsRequestLocUpdate = false;
        }
    }

    public void requestLocationUpdates(Listener listener) {

        //添加监听器
        addListener(listener);

        if (!mIsRequestLocUpdate) {

            registerLocationUpdateListener();
            mIsRequestLocUpdate = true;
            mHandler.postDelayed(mLocLisnRemoveRunnable, 3000);//监测3秒后注销
        }
    }

    /**
     * 注册位置变化监听器
     */
    private void registerLocationUpdateListener() {

        try {

            if (mSysLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                mSysLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "registerLocationUpdateListener provider = " + LocationManager.GPS_PROVIDER);
            }

            if (mSysLocMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                mSysLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "registerLocationUpdateListener provider = " + LocationManager.NETWORK_PROVIDER);

            }

        } catch (Throwable t) {

            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "registerLocationUpdateListener error = " + t.getMessage());
        }
    }

    /**
     * 移除位置变化监听器
     */
    private void removeLocationUpdateListener() {

        try {

            mSysLocMgr.removeUpdates(this);
            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "removeLocationUpdateListener ok");

        } catch (Throwable t) {

            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "removeLocationUpdateListener error = " + t.getMessage());
        }
    }

    /**
     * 请求位置超时回调
     */
    private void onRequestLocationUpdatesTimeout() {

        removeLocationUpdateListener();
        clearListener();
        mIsRequestLocUpdate = false;
    }

    /**
     * 获取最后一次位置信息，优先返回gps位置，没有则返回网络位置
     *
     * @return
     */
    public Location getLastKnownLocation() {

        Location location = getLastKnownLocationByProvider(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = getLastKnownLocationByProvider(LocationManager.NETWORK_PROVIDER);

        return location;
    }


    private Location getLastKnownLocationByProvider(String provider) {

        Location location = null;
        try {

            location = mSysLocMgr.getLastKnownLocation(provider);
            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "getLastKnownLocationByProvider provider = " + provider + ", loc = " + location);

        } catch (Throwable e) {

            if (LogMgr.isDebug())
                LogMgr.d(simpleTag(), "getLastKnownLocationByProvider error = " + e.getMessage());
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "onLocationChanged location = " + location);

        callbackListener(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "onStatusChanged provider status = " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "onProviderEnabled provider = " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "onProviderDisabled provider = " + provider);
    }

    /**
     * 添加坐标监听器
     *
     * @param listener
     */
    private void addListener(Listener listener) {

        if (listener != null && !mListenerList.contains(listener))
            mListenerList.add(listener);
    }

    /**
     * 清空监听器
     */
    private void clearListener() {

        mListenerList.clear();
    }

    /**
     * 回调监听器坐标改变
     *
     * @param location
     */
    private void callbackListener(Location location) {

        if (location == null || CollectionUtil.isEmpty(mListenerList))
            return;

        Listener lisn;
        for (int i = 0; i < mListenerList.size(); i++) {

            lisn = mListenerList.get(i);
            if (lisn != null)
                lisn.onLocagionChanged(location.getLongitude(), location.getLatitude());
        }
    }

    private String simpleTag() {

        return getClass().getSimpleName();
    }

    public interface Listener {

        void onLocagionChanged(double lng, double lat);
    }
}
