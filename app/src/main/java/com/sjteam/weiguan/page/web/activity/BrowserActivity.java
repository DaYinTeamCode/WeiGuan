package com.sjteam.weiguan.page.web.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.androidex.util.TextUtil;
import com.sjteam.weiguan.dialog.CpBaseDialog;
import com.sjteam.weiguan.dialog.CpConfirmDialog;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;
import com.sjteam.weiguan.page.web.fragment.BrowserFra;
import com.sjteam.weiguan.utils.ActivityUtil;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:33 PM
 */
public class BrowserActivity extends CpFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentFragment(BrowserFra.class.getName());
    }

    public void showCameraMissingPermissionDialog() {

        CpConfirmDialog dialog = new CpConfirmDialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitleText("启用相机访问权限");
        dialog.setContentText("设置>权限>打开完成操作");
        dialog.setLeftButtonText("取消");
        dialog.setLeftButtonClickListener(new CpBaseDialog.OnDialogClickListener() {

            @Override
            public void onClick(final CpBaseDialog dialog) {

                dialog.dismiss();
            }
        });

        dialog.setRightButtonText("去开启");
        dialog.setRightButtonClickListener(new CpBaseDialog.OnDialogClickListener() {
            @Override
            public void onClick(CpBaseDialog dialog) {
                startAppSettings();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    /**
     * 打开APP设置页面
     */
    private void startAppSettings() {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {

            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

    }

    public static void startActivity(Context context, String url) {

        startActivity(context, url, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, true);
    }

    public static void startActivityWithNoPermission(Context context, String url) {

        startActivity(context, url, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, TextUtil.TEXT_EMPTY, false);
    }

    public static void startActivity(Context context, String url, String title, String pageName, String topicId) {

        startActivity(context, url, title, pageName, topicId, true);
    }

    public static void startActivity(Context context, String url, String title, String pageName, String topicId, boolean permissionEnable) {

        Intent intent = new Intent();
        intent.setClass(context, BrowserActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("pageName", pageName);//页面别名
        intent.putExtra("topicId", topicId);
        intent.putExtra("permissionEnable", permissionEnable);
        ActivityUtil.startActivitySafely(context, intent);
    }
}
