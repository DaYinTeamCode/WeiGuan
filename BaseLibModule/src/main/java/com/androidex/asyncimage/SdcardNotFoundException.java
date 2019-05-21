package com.androidex.asyncimage;

/**
 * 自定义异常
 */
class SdcardNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	SdcardNotFoundException(String msg)
	{
		super(msg);
	}
}
