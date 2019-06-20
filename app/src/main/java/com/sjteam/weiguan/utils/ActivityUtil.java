package com.sjteam.weiguan.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class ActivityUtil {

    /**
     * 安全启动Activity
     *
     * @param context
     * @param intent
     */
    public static void startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {

        } catch (SecurityException ex) {
        }
    }
}
