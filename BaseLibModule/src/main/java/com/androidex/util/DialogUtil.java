package com.androidex.util;

import android.app.Dialog;

/**
 * 对话框工具类
 * Created by yihaibin on 16/1/23.
 */
public class DialogUtil {

    public static void showDialog(Dialog dialog){

        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }

    public static boolean isShowing(Dialog dialog){

        return dialog != null && dialog.isShowing();
    }

    public static void dismissDialog(Dialog dialog){

        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
