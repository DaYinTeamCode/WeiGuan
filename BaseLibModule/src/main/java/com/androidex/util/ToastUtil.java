package com.androidex.util;

import com.androidex.context.ExApplication;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司工具类
 */
public class ToastUtil {

	private static Toast mToast;
    private static ToastUtilListener mLisn;
	
    public static void showToast(int textResId){

        try{

            init();
            mToast.setText(textResId);
            mToast.show();
        }catch (Exception e){

            release();
        }
    }

    public static void showToast(String text){

        try{

            init();
            mToast.setText(text);
            mToast.show();

        }catch (Exception e){

            release();
        }
    }
    
    public static void showToast(int stringResId, Object... args){

    	showToast(ExApplication.getContext().getResources().getString(stringResId, args));
    }

    private static void init(){

        if(mToast == null || mToast.getView() == null)
            mToast = Toast.makeText(ExApplication.getContext(), TextUtil.TEXT_EMPTY, Toast.LENGTH_SHORT);
    }

    public static void setListener(ToastUtilListener lisn){

        mLisn = lisn;
    }

    public static void release(){

        mToast = null;
    }

    public static interface ToastUtilListener{

        Toast getBackupToast(Context context);
    }
}
