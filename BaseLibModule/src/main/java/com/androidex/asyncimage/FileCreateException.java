package com.androidex.asyncimage;

/**
 * 自定义异常
 */
public class FileCreateException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	FileCreateException(String msg)
	{
		super(msg);
	}
}
