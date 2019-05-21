package com.androidex.util;

import android.os.Looper;

/**
 * 线程工具类
 */
public class ThreadUtil {

	/**
	 * 将当前线程睡眠,已捕获中断异常
	 * @param millis
	 * @return
	 */
	public static boolean sleep(long millis){
		
		try{
			
			if(millis > 0)
				Thread.sleep(millis);
			
			return true;
		}catch(Exception e){
			
			return false;
		}
	}

	/**
	 * 判断当前线程是否在主线程
	 * @return
	 */
	public static boolean isMainThread(){

		return Looper.myLooper() == Looper.getMainLooper();
	}
}
