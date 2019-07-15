package com.sjteam.weiguan.constants;

import com.androidex.util.AppInfoUtil;
import com.androidex.util.DeviceUtil;
import com.sjteam.weiguan.app.WgApp;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 3:38 PM
 */
public interface CommonConstant {

    String DEVICE_ID = DeviceUtil.getDeviceId();
    String IMEI = DeviceUtil.getIMEI();
    String IMEI2 = DeviceUtil.getIMEI2();
    String IMSI = DeviceUtil.getIMSI();
    String ANDROID_ID = DeviceUtil.getAndroidId();
    String APP_VERSION_NAME = AppInfoUtil.getVersionName();
    String APK_CHANNEL_NAME = WgApp.getApkChannelName();
    String OS_VERSION = DeviceUtil.getOsVersion();
    String DEVICE_BRAND = DeviceUtil.getBrand();
    String DEVICE_MODEL = DeviceUtil.getDeviceModel();
    String NETWORK_TYPE = DeviceUtil.getNetworkType();
    String APP_INSTALL_TIME = String.valueOf(AppInfoUtil.getInstallAppTime() / 1000);
    String SCREEN_WIDTH = String.valueOf(DimenConstant.SCREEN_WIDTH);
    String SCREEN_HEIGHT = String.valueOf(DimenConstant.SCREEN_HEIGHT);
}
