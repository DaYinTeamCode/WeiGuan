package com.sjteam.weiguan.manager.permisssion.deviceid;

import android.support.annotation.NonNull;

import com.ex.sdk.android.expermissions.ExEasyPermissions.ExPermissionCallbacks;

import java.util.List;

/**
 * 权限回调封装，简化书写
 */
class DeviceIdPermissionCallback extends ExPermissionCallbacks {

    private ExPermissionCallbacks mCallback;

    DeviceIdPermissionCallback() {

        this(null);
    }

    DeviceIdPermissionCallback(ExPermissionCallbacks callback) {

        mCallback = callback;
    }

    @Override
    public void onAleadyHasOrAllPermissionsGranted(int i, @NonNull List<String> list, boolean b) {

        if (mCallback != null) {
            mCallback.onAleadyHasOrAllPermissionsGranted(i, list, b);
        }
    }

    @Override
    public void onAlertSystemPermissionDialogStat(int requestCode, @NonNull List<String> perms) {

        super.onAlertSystemPermissionDialogStat(requestCode, perms);
        if (mCallback != null) {
            mCallback.onAlertSystemPermissionDialogStat(requestCode, perms);
        }
    }

    @Override
    public void onAlertRationaleDialogStat(int requestCode, @NonNull List<String> perms) {

        super.onAlertRationaleDialogStat(requestCode, perms);
        if (mCallback != null) {
            mCallback.onAlertRationaleDialogStat(requestCode, perms);
        }
    }

    @Override
    public void onAlertAppSettingsDialogStat(int requestCode, @NonNull List<String> perms) {

        super.onAlertAppSettingsDialogStat(requestCode, perms);
        if (mCallback != null) {
            mCallback.onAlertAppSettingsDialogStat(requestCode, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        if (mCallback != null) {
            mCallback.onPermissionsGranted(requestCode, perms);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (mCallback != null) {
            mCallback.onPermissionsDenied(requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {

        super.onRequestPermissionsResult(i, strings, ints);
        if (mCallback != null) {
            mCallback.onRequestPermissionsResult(i, strings, ints);
        }
    }

    @Override
    public boolean onAllPermissionsDeniedIntercept(int requestCode, @NonNull List<String> perms) {

        if (mCallback == null) {
            return super.onAllPermissionsDeniedIntercept(requestCode, perms);
        } else {
            return mCallback.onAllPermissionsDeniedIntercept(requestCode, perms);
        }
    }
}
