package com.androidex.util;

import com.androidex.context.ExApplication;

/**
 * dp px 转换工具类
 */
public class DensityUtil {

	private static float scale = ExApplication.getContext().getResources().getDisplayMetrics().density;

	/**
	 * dp 转 px
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(float dpValue) {

		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px 转 dp
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(float pxValue) {

		return (int) (pxValue / scale + 0.5f);
	}

}
