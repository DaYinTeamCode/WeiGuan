package com.androidex.util;

import java.io.File;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import com.androidex.context.ExApplication;

/**
 * 系统Activity启动帮助类
 */
public class ActivityUtil {

    public static boolean isFinishing(Activity activity) {

        return activity != null && activity.isFinishing();
    }

    /**
     * 打开设置对应的app通知开关详情页
     *
     * @param activity
     */
    public static void startSettingAppNotifyActivity(Activity activity) {

        if (activity == null)
            return;

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                activity.startActivity(intent);
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", activity.getPackageName());
                intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                activity.startActivity(intent);
            } else if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }

        } catch (Exception e) {

        }
    }

    /**
     * 判断是否可以跳转Vivo自启动列表管理
     *
     * @param activity
     * @return
     */
    public static boolean isCanJumpVivoSystemAutoRunActivity(Activity activity) {

        if (activity == null)
            return false;

        try {

            Intent intentVivo = new Intent();
            intentVivo.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
            return activity.getPackageManager().resolveActivity(intentVivo, PackageManager.MATCH_DEFAULT_ONLY) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 跳转Vivo自启动列表管理页面
     *
     * @param activity
     */
    public static void startVivoSystemAutoRunActivity(Activity activity) {

        if (activity == null)
            return;

        try {

            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.vivo.permissionmanager"
                    , "com.vivo.permissionmanager.activity.PurviewTabActivity");

            intent.setComponent(componentName);
            activity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 判断是否可以跳转Oppo系统自启动管理列表
     *
     * @param activity
     * @return
     */
    public static boolean isCanJumpOppoSystemAutoRunActivity(Activity activity) {

        if (activity == null)
            return false;

        try {

            Intent intentOppo = new Intent();
            intentOppo.setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity");
            return activity.getPackageManager().resolveActivity(intentOppo, PackageManager.MATCH_DEFAULT_ONLY) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 跳转Oppo自启动列表管理页面
     *
     * @param activity
     */
    public static void startOppoSystemAutoRunActivity(Activity activity) {

        if (activity == null)
            return;

        try {

            Intent intent = new Intent();
            ComponentName componentName = ComponentName.unflattenFromString("com.coloros.safecenter" +
                    "/.startupapp.StartupAppListActivity");
            intent.setComponent(componentName);
            activity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 调起系统拨号界面
     *
     * @param phoneNumber
     * @return
     */
    public static boolean startPhoneCallActivity(String phoneNumber) {

        if (TextUtil.isEmpty(phoneNumber))
            return false;

        return startPhoneCallActivity(Uri.parse("tel:" + phoneNumber));
    }

    /**
     * 调起系统拨号界面
     *
     * @param telUri
     * @return
     */
    public static boolean startPhoneCallActivity(Uri telUri) {

        try {

            Intent intent = new Intent(Intent.ACTION_CALL, telUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ExApplication.getContext().startActivity(intent);
            return true;

        } catch (Exception e) {

            if (LogMgr.isDebug())
                e.printStackTrace();
        }

        return false;
    }

    /**
     * 调起系统发送短信界面
     *
     * @param number
     * @param smsContent
     * @return
     */
    public static boolean startSmsActivity(String number, String smsContent) {

        try {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + TextUtil.filterNull(number)));
            intent.putExtra("sms_body", TextUtil.filterNull(smsContent));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ExApplication.getContext().startActivity(intent);
            return true;

        } catch (Exception e) {

            if (LogMgr.isDebug())
                e.printStackTrace();
        }

        return false;
    }

    /**
     * 根据uri字符串调起系统界面
     *
     * @param uriStr
     * @param newTask
     * @return
     */
    public static boolean startUriActivity(String uriStr, boolean newTask) {

        try {

            Uri uri = Uri.parse(uriStr);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (newTask)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ExApplication.getContext().startActivity(intent);
            return true;

        } catch (Exception e) {

            if (LogMgr.isDebug())
                e.printStackTrace();
        }

        return false;
    }

    /**
     * 调起系统相机
     *
     * @param activity
     * @param photoFile   拍照后存储的文件路径
     * @param requestCode
     * @return
     */
    public static boolean startCameraActivityForResult(Activity activity, File photoFile, int requestCode) {

        try {

            if (photoFile == null)
                return false;

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, requestCode);
            return true;

        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.e("startCameraActivityForResult", e.toString());
        }

        return false;
    }

    /**
     * 调用系统图库打开图片文件
     *
     * @param activity
     * @param imagePath 图片路径
     * @return
     */
    public static boolean startSystemImageLibActivity(Activity activity, String imagePath) {

        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse(imagePath), "image/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            activity.startActivity(intent);
            return true;

        } catch (Exception e) {

            if (LogMgr.isDebug())
                LogMgr.e("startImageLibActivity", e.toString());
        }

        return false;
    }

    /**
     * 设置activity的开始和结束动画(如果是dialog样式的无效)
     *
     * @param activity
     * @param inAnim
     * @param outAnim
     */
    public static void startActAnim(Activity activity, int inAnim, int outAnim) {

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
            new Object() {
                public void overridePendingTransition(Activity act, int i, int j) {
                    act.overridePendingTransition(i, j);
                }
            }.overridePendingTransition(activity, inAnim, outAnim);
        }
    }

    /**
     * 取当前activity名称
     *
     * @param activity
     * @return
     */
    public static String getTopActity(Context activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getShortClassName();
    }

    /**
     * 跳转至系统里 应用的设备页面
     * @param activity
     */
    public static void startAppSetting(Activity activity) {

        if(activity == null)
            return;

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }
}
