package com.androidex.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.androidex.context.ExApplication;

/**
 * AlarmerManager的工具类
 */
public class AlarmerUtil {

	/**
	 * 启动一个闹钟任务
	 * @param intent
	 * @param delayMillis
	 * @param cancelBefore
	 */
	public static void start(Intent intent, long delayMillis, boolean cancelBefore) {

		Context ctx = ExApplication.getContext();
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (cancelBefore)
			alarmManager.cancel(pendIntent);

		alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delayMillis, pendIntent);
	}

	/**
	 * 取消一个闹钟任务
	 * @param intent
	 */
	public static void cancel(Intent intent) {

		Context ctx = ExApplication.getContext();
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendIntent);
	}
}
