package com.androidex.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.androidex.context.ExApplication;

/**
 * app信息获取工具类
 */
public class AppInfoUtil {

	/**
	 * 获取应用的包名
	 * @return
	 */
	public static String getAppPackageName(){

		try{

			return ExApplication.getContext().getPackageName();

		}catch(Exception e){

			return TextUtil.TEXT_EMPTY;
		}
	}

	/**
	 * 获取App名称
	 * @return
	 */
	public static String getAppName() { 
	
		try { 
			
			Context ctx = ExApplication.getContext();
			PackageManager packageManager = ctx.getPackageManager(); 
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0); 
			CharSequence charSequcence = packageManager.getApplicationLabel(applicationInfo);
			return charSequcence == null ? TextUtil.TEXT_EMPTY : charSequcence.toString();

		} catch (Exception e) { 
			
			if(LogMgr.isDebug())
				e.printStackTrace();
		} 
		
		return TextUtil.TEXT_EMPTY;
	}

	/**
	 * 获取清单文件中指定name的meta标签的value(boolean)值
	 * @param name
	 * @return
	 */
	public static boolean getMetaBooleanValue(String name) {

		try {

			Context ctx = ExApplication.getContext();
			ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getBoolean(name);

		} catch (Exception e) {

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return false;
	}

	/**
	 * 获取清单文件中指定name的meta标签文本值
	 * @param name
	 * @return
	 */
	public static String getMetaStringValue(String name){

		try {

			Context ctx = ExApplication.getContext();
			ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			return TextUtil.filterNull(appInfo.metaData.getString(name));

		} catch (Exception e) {

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return TextUtil.TEXT_EMPTY;
	}

	/**
	 * 获取app versionCode值
	 * @return 如果获取失败, 也返回1
	 */
	public static int getVersionCode() {

		try {

			Context ctx = ExApplication.getContext();
			PackageInfo packInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return packInfo.versionCode;

		} catch (Exception e) {
			
			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return 1;
	}

	/**
	 * 获取app versionName
	 * @return
	 */
	public static String getVersionName() {

		try {

			Context ctx = ExApplication.getContext();
			PackageInfo packInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return TextUtil.filterNull(packInfo.versionName).replace(',', '.');

		} catch (Exception e) {
			
			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return TextUtil.TEXT_EMPTY;
	}

	/**
	 * 获取应用第一次安装时间
	 * @return 获取失败返回0
	 */
	public static long getInstallAppTime() {

		try {
			
			Context ctx = ExApplication.getContext();
			PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return packageInfo.firstInstallTime;

		} catch (Exception e) {
			
			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 判断app是否在前台运行
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground (Context context) {

		try{

			ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			String currentPackageName = cn.getPackageName();
			return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());

		}catch(Exception e){

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return false;
	}

	/**
	 * 从清单文件中获取umeng标识的渠道
	 * 已不使用umeng,不建议使用拉
	 * @return
	 */
	@Deprecated
	public static String getChannelNameByUmeng() {

		try {

			Context ctx = ExApplication.getContext();
			ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			return TextUtil.filterNull(appInfo.metaData.getString("UMENG_CHANNEL"));

		} catch (Exception e) {

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return TextUtil.TEXT_EMPTY;
	}

	/**
	 * 判断app通知栏开关状态
	 * @return
	 */
	public static boolean isNotificationsEnabled(){

		try{

			return NotificationManagerCompat.from(ExApplication.getContext()).areNotificationsEnabled();

		}catch (Throwable e){

			if(LogMgr.isDebug())
				e.printStackTrace();
		}

		return true;
	}
}
