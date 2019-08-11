package com.sjteam.weiguan.manager.permisssion.deviceid;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidex.prefs.ExSharedPrefs;
import com.androidex.util.TextUtil;
import com.sjteam.weiguan.app.WgApp;

class DeviceIdPrefs {

    private final String KEY_DEVICE_ID = "device_id";
    private final String KEY_IMEI = "imei";
    private final String KEY_IMEI2 = "imei2";
    private final String KEY_IMSI = "imsi";
    private final String KEY_LOCAL_TEST_DEVICE_ID = "local_test_device_id";//存储测试设备号key
    private final String KEY_MOCK_TEST_DEVICE_ID = "mock_test_device_id";//存储模拟设备号key

    private Context mContext;
    private ExSharedPrefs mExSharedPrefs;

    public DeviceIdPrefs(Context context) {

        mContext = WgApp.getContext();
        mExSharedPrefs = new ExSharedPrefs(context, "prefs_device");
    }

    public Context getContext() {

        return mContext;
    }

    /**
     * 调用系统api，读取设备标识信息存储至prefs中
     * 如果某个标识在prefs中已经有值不会再重新设置
     */
    public void syncFromDevice() {

        //2.8.90兼容imei号返回0的情况，所以改成了判断trim后的长度
        //同时DeviceUtil的getDeviceId() getImei() getImei2() 都做了调整，如果是0，返回TextUtil.TEXT_EMPTY;
        boolean deviceIdEmpty = TextUtil.size(mExSharedPrefs.getString(KEY_DEVICE_ID, TextUtil.TEXT_EMPTY)) <= 1;
        boolean imeiEmpty = TextUtil.size(mExSharedPrefs.getString(KEY_IMEI, TextUtil.TEXT_EMPTY)) <= 1;
        boolean imei2Empty = TextUtil.size(mExSharedPrefs.getString(KEY_IMEI2, TextUtil.TEXT_EMPTY)) <= 1;
        boolean imsiEmpty = TextUtil.isEmpty(mExSharedPrefs.getString(KEY_IMSI, TextUtil.TEXT_EMPTY));

        SharedPreferences.Editor editor = null;
        if (deviceIdEmpty || imeiEmpty || imei2Empty || imsiEmpty) {
            editor = mExSharedPrefs.editor();
        }

        if (imeiEmpty) {
            editor.putString(KEY_IMEI, IdentityUtil.getIMEI(mContext));
        }

        if (imei2Empty) {
            editor.putString(KEY_IMEI2, IdentityUtil.getIMEI2(mContext));
        }

        if (imsiEmpty) {
            editor.putString(KEY_IMSI, IdentityUtil.getIMSI(mContext));
        }

        if (deviceIdEmpty) {
            editor.putString(KEY_DEVICE_ID, IdentityUtil.getDeviceId(mContext));
        }

        if (editor != null) {
            editor.commit();
        }
    }

    /**
     * 获取设备号
     *
     * @return
     */
    public String getDeviceId() {

        return mExSharedPrefs.getString(KEY_DEVICE_ID, TextUtil.TEXT_EMPTY);
    }

    /**
     * 获取imei_1
     *
     * @return
     */
    public String getImei() {

        return mExSharedPrefs.getString(KEY_IMEI, TextUtil.TEXT_EMPTY);
    }

    /**
     * 获取imei_2
     *
     * @return
     */
    public String getImei2() {

        return mExSharedPrefs.getString(KEY_IMEI2, TextUtil.TEXT_EMPTY);
    }

    /**
     * 获取imsi
     *
     * @return
     */
    public String getImsi() {

        return mExSharedPrefs.getString(KEY_IMSI, TextUtil.TEXT_EMPTY);
    }

    /**
     * 保存本地测试设备号
     *
     * @param testDeviceId
     */
    public void saveLocalTestDeviceId(String testDeviceId) {

        mExSharedPrefs.putString(KEY_LOCAL_TEST_DEVICE_ID, TextUtil.filterNull(testDeviceId));
    }

    /**
     * 获取本地测试设备号
     *
     * @return
     */
    public String getLocalTestDeviceId() {

        return mExSharedPrefs.getString(KEY_LOCAL_TEST_DEVICE_ID, TextUtil.TEXT_EMPTY);
    }

    /**
     * 保存mock测试设备号
     *
     * @param testDeviceId
     */
    public void saveMockTestDeviceId(String testDeviceId) {

        mExSharedPrefs.putString(KEY_MOCK_TEST_DEVICE_ID, TextUtil.filterNull(testDeviceId));
    }

    /**
     * 获取mock测试设备号
     *
     * @return
     */
    public String getMockTestDeviceId() {

        return mExSharedPrefs.getString(KEY_MOCK_TEST_DEVICE_ID, TextUtil.TEXT_EMPTY);
    }

    /**
     * 获取所有的设备标识信息字符串
     *
     * @return
     */
    public String getAllDeviceInfoString() {

        return "DeviceIdPrefs{" +
                "KEY_DEVICE_ID='" + getDeviceId() + '\'' +
                ", KEY_IMEI='" + getImei() + '\'' +
                ", KEY_IMEI2='" + getImei2() + '\'' +
                ", KEY_IMSI='" + getImsi() + '\'' +
                ", KEY_LOCAL_TEST_DEVICE_ID='" + getLocalTestDeviceId() + '\'' +
                ", KEY_MOCK_TEST_DEVICE_ID='" + getMockTestDeviceId() + '\'' +
                '}';
    }
}
