package com.androidex.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * ===========================================================
 * ***********************************************************
 * 作    者：大印
 * Git 地址:https://github.com/DaYinTeamCode
 * 邮    箱：gaoyin_vip@126.com
 * 版    本：1.0
 * 创建日期：2018/6/13 下午9:14
 * 描    述： 获取手机经纬度坐标
 * <p>
 * 修订历史：
 * <p>
 * ***********************************************************
 * ===========================================================
 */
public class LocationUtil {

    private volatile static LocationUtil mInstance;
    private LocationManager locationManager;
    private String locationProvider;
    private static Location location;
    private static double lng, lat;

    private LocationUtil() {

    }

    //采用Double CheckLock(DCL)实现单例
    public static LocationUtil getInstance() {

        if (mInstance == null) {
            synchronized (LocationUtil.class) {
                if (mInstance == null) {
                    mInstance = new LocationUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置当前定位位置
     */
    public void setLocation(Context context) {

        if (checkLocationPermission(context) || context == null)
            return;

        //1.获取位置管理器
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null)
            return;

        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        // 优先获取GPS，在判断NetWork可用获取
        if (providers.contains(LocationManager.GPS_PROVIDER))
            locationProvider = LocationManager.GPS_PROVIDER;
        else if (providers.contains(LocationManager.NETWORK_PROVIDER))
            locationProvider = LocationManager.NETWORK_PROVIDER;
        else
            return;

        //3.获取Location的对象,第一次获取为null
        if (location == null)
            location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {

            lng = location.getLongitude();
            lat = location.getLatitude();
        }
    }

    /**
     * 检查定位权限
     *
     * @return
     */
    private boolean checkLocationPermission(Context context) {

        if (context == null)
            return true;

        return Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public static Location showLocation() {

        return location;
    }

    /**
     * 获取经度
     *
     * @return
     */
    public static double getLng() {

        return lng;
    }

    /**
     * 获取纬度
     *
     * @return
     */
    public static double getLat() {

        return lat;
    }

    /**
     * 移除定位
     */
    public void removeLocation() {

        if (location != null)
            location = null;

        if (locationManager != null)
            locationManager = null;

        if (mInstance != null)
            mInstance = null;
    }
}
