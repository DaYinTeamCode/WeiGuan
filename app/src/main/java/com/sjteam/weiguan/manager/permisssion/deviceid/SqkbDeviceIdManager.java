package com.sjteam.weiguan.manager.permisssion.deviceid;

import android.Manifest;
import android.content.Context;
import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.androidex.util.TextUtil;
import com.ex.sdk.android.expermissions.ExEasyPermissions;
import com.sjteam.weiguan.R;

import java.util.List;

/**
 * 管理类，开发维护逻辑
 * <p>
 * 一、数据三层：
 * 数据管理层：SqkbDeviceIdManager，管理获取逻辑，统一入口
 * 本地数据化层：DeviceIdPrefs，只负责从持久化获取设备
 * 设备数据层：IdentityUtil，只负责从设备获取数据
 * <p>
 * 二、逻辑数据流
 * 内存数据 从 本地持久化文件中赋值
 * 本地持久化数据  从  设备数据层从读取
 * <p>
 * 三、异常场景
 * 如果数据层不合法，那么，最终会从设备中获取。
 * 本地持久化 <-- 设备数据
 */
public class SqkbDeviceIdManager extends Observable<SqkbDeviceIdManager.SqkbDeviceIdStateChangeObserver> {

    private static final String TAG = "SqkbDeviceIdManager";
    private static final int RC_READ_PHONE_STATE_PERM = 123;
    private static final SqkbDeviceIdManager INSTANCE = new SqkbDeviceIdManager();

    private DeviceIdPrefs mDevicePrefs;

    /**
     * 私有构造器
     */
    private SqkbDeviceIdManager() {

    }

    public static final SqkbDeviceIdManager getInstance() {

        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {

        mDevicePrefs = new DeviceIdPrefs(context);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 设备号相关api
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 全局唯一值入口：得到 DeviceId，如果处于 Mock、Test模式，得到的是修正的DeviceId
     * 整个应用统一的设备号出口，一定要用它！！！！！！
     *
     * @return
     */
    public String getDeviceId() {

        return mDevicePrefs.getDeviceId();
    }

    /**
     * deviceId是否有值
     * 改函数不走测试设备号逻辑
     *
     * @return
     */
    private boolean hasDeviceId() {

        return !TextUtil.isEmpty(mDevicePrefs.getDeviceId());
    }

    /**
     * 全局唯一值入口：得到 IMEI_1
     * 正常情况下，imei_1 应该和deviceId一致
     * 因此在debug环境下：
     * 测试设备号如果有值，imei_1 应该返回测试设备号
     * 测试设备号没有值，imei_1 返回存储imei
     * <p>
     * 正式环境中：imei_1直接返回存储的值
     *
     * @return
     */
    public String getIMEI() {

        return mDevicePrefs.getImei();
    }

    /**
     * 全局唯一值入口：得到 IMEI_2
     * imei2不参与测试逻辑,直接返回存储的值
     *
     * @return
     */
    public String getIMEI2() {

        return mDevicePrefs.getImei2();
    }

    /**
     * 全局唯一值入口：得到 IMSI
     * imsi不参与测试逻辑,直接返回存储的值
     *
     * @return
     */
    public String getIMSI() {

        return mDevicePrefs.getImsi();
    }

    /**
     * 获取测试设备号
     * 优先使用mock测试设备号
     * 如果mock测试设备号没有用本地测试设备号
     * 如果没有测试设备号值，返回给的默认值
     *
     * @param defaultDeviceId
     * @return
     */
    private String getTestDeviceId(String defaultDeviceId) {

        String deviceId = mDevicePrefs.getMockTestDeviceId();
        if (!TextUtil.isEmpty(deviceId)) {
            return deviceId;
        }

        deviceId = mDevicePrefs.getLocalTestDeviceId();
        if (!TextUtil.isEmpty(deviceId)) {
            return deviceId;
        }

        return defaultDeviceId;
    }

    /**
     * 保存本地测试设备号
     *
     * @param localTestDeviceId
     */
    public void saveLocalTestDeviceId(String localTestDeviceId) {

        mDevicePrefs.saveLocalTestDeviceId(localTestDeviceId);
    }

    /**
     * 获取本地测试设备号
     *
     * @return
     */
    public String getLocalTestDeviceId() {

        return mDevicePrefs.getLocalTestDeviceId();
    }

    /**
     * 保存mock测试设备号
     *
     * @param mockTestDeviceId
     */
    public void saveMockTestDeviceId(String mockTestDeviceId) {

        mDevicePrefs.saveMockTestDeviceId(mockTestDeviceId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 权限管理api
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 是否需要设备号授权
     *
     * @return
     */
    public boolean needDeviceIdPermission() {

        return !hasDeviceId() || !hasPhoneStatePermission();
    }

    /**
     * 是否含有 READ_PHONE_STATE 权限
     *
     * @return
     */
    private boolean hasPhoneStatePermission() {

        return ExEasyPermissions.hasPermissions(mDevicePrefs.getContext(), Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * 全局唯一值入口：申请权限，并回调
     *
     * @param activity
     * @param callback
     */
    public void requestPhoneStatePermission(@NonNull FragmentActivity activity, final ExEasyPermissions.ExPermissionCallbacks callback) throws IllegalStateException {

        try {
            new ExEasyPermissions.RequestBuilder()
                    .with(activity)
                    .rationale(activity.getString(R.string.sqkb_core_permission_read_phone_state))
                    .appSettingRationale(activity.getString(R.string.sqkb_core_permission_read_phone_permanently))
                    .showCloseButton(false)
                    .requestCode(RC_READ_PHONE_STATE_PERM)
                    .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .showAppSettingsDialogWhenNeverAskAgain(true)
                    .permissionCallbacks(new DeviceIdPermissionCallback(callback) {
                        @Override
                        public void onAleadyHasOrAllPermissionsGranted(int i, @NonNull List<String> list, boolean aleadyHasAllPermissions) {

                            onPhoneStatePermissionsGranted(aleadyHasAllPermissions);

                            super.onAleadyHasOrAllPermissionsGranted(i, list, aleadyHasAllPermissions);
                        }
                    })
                    .request();
        } catch (Exception e) {

            onPhoneStatePermissionsGranted(hasPhoneStatePermission());

            throw new IllegalStateException("ExEasyPermissions ERROR!!! , msg : " + e.getMessage());
        }
    }

    /**
     * 当权限变化时的公共处理方法
     *
     * @param aleadyHasAllPermissions
     */
    private void onPhoneStatePermissionsGranted(boolean aleadyHasAllPermissions) {
        syncDeviceInfoToPrefs();
        dispatchChange(aleadyHasAllPermissions);
    }

    /**
     * 同步设备信息到存储中
     */
    private void syncDeviceInfoToPrefs() {

        if (mDevicePrefs != null) {
            mDevicePrefs.syncFromDevice();
        }
    }

    private void dispatchChange(boolean aleadyHasAllPermissions) {
        synchronized (mObservers) {
            for (SqkbDeviceIdStateChangeObserver observer : mObservers) {
                observer.onSqkbDeviceIdStateChanged(aleadyHasAllPermissions);
            }
        }
    }

    /**
     * 获取设备信息标识字符串
     *
     * @return
     */
    public String getDeviceInfoString() {

        return "SqkbDeviceIdManager{" +
                "DEVICE_ID='" + getDeviceId() + '\'' +
                ", IMEI='" + getIMEI() + '\'' +
                ", IMEI2='" + getIMEI2() + '\'' +
                ", IMSI='" + getIMSI() + '\'' +
                ", device prefs info = " + mDevicePrefs.getAllDeviceInfoString() +
                '}';
    }

    /**
     * 观察者可能会更好，此处使用回调，目的在于收敛回调入口，收敛作用域。
     */
    public interface SqkbDeviceIdStateChangeObserver {
        void onSqkbDeviceIdStateChanged(boolean aleadyHasAllPermissions);
    }
}
