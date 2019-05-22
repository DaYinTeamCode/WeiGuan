package com.jzyd.lib.httptask;

import com.ex.android.http.params.HttpTaskParams;

/**
 * JzydHttpFrameFragment 使用的请求参数对象
 */
public class HttpFrameParams {

	public HttpTaskParams params;
	public Class<?> clazz;

	public HttpFrameParams(){

	}

	public HttpFrameParams(HttpTaskParams params, Class<?> clazz) {

		this.params = params;
		this.clazz = clazz;
	}
}
