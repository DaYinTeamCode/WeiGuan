package com.androidex.plugin;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.androidex.util.ViewUtil;

/**
 * 输入法控制的帮助类
 * @author yhb
 */
public class KeyboardHandler implements ViewTreeObserver.OnGlobalLayoutListener{

	public final int HIDE_KEYBOARD_MILLIS = 250;
	
	private Activity mActivity;
	private InputMethodManager mInputMethodManager;
	private boolean mSoftKeyLocked;

	private View mKeyboardView;
	private int mOriginHeight;
	private int mPreHeight;
	private boolean mKeyboardShowing;
	private KeyBoardListener mKeyBoardLisn;


	public KeyboardHandler(Activity activity){
		
		mActivity = activity;
	}
	
	private void initInputMethodMgr(){
		
		if(mInputMethodManager == null)
			mInputMethodManager = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

    /**
     * 默认实现
     * @param tokenView
     * @return
     */
    public boolean showSoftInput(View tokenView){

        return showSoftInput(tokenView, InputMethodManager.SHOW_IMPLICIT);//InputMethodManager.HIDE_NOT_ALWAYS
    }

    /**
     * 自定义实现
     * @param tokenView
     * @param flag
     * @return
     */
    public boolean showSoftInput(View tokenView, int flag){

        initInputMethodMgr();
        return mInputMethodManager.showSoftInput(tokenView, flag);
    }

    /**
     * 默认实现
     * @param tokenView
     * @return
     */
	public boolean hideSoftInput(View tokenView){

        return hideSoftInput(tokenView, InputMethodManager.HIDE_NOT_ALWAYS);
	}

    /**
     * 自定义实现
     * @param tokenView
     * @param flag
     * @return
     */
    public boolean hideSoftInput(View tokenView, int flag){

        initInputMethodMgr();
        return mInputMethodManager.hideSoftInputFromWindow(tokenView.getWindowToken(), flag);
    }

    /**
     * 默认实现
     * @param tokenView
     * @return
     */
    public void finishActivityBySoftInput(View tokenView){

        finishActivityBySoftInput(tokenView, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 自定义实现
     * @param tokenView
     * @param flag
     * @return
     */
	public void finishActivityBySoftInput(View tokenView, int flag){
		
		if(mActivity.isFinishing() || mSoftKeyLocked)
			return;
		
		if(hideSoftInput(tokenView, flag)){
			
			mSoftKeyLocked = true;
			tokenView.postDelayed(new Runnable() {
				@Override
				public void run() {
					
					mSoftKeyLocked = false;
					if(!mActivity.isFinishing())
						mActivity.finish();
				}
			}, HIDE_KEYBOARD_MILLIS);
		}else{
			
			mActivity.finish();
		}
	}

    /**
     * 默认实现
     * @param tokenView
     * @return
     */
    public void hideSoftInputPost(View tokenView, final Runnable runnable){

        hideSoftInputPost(tokenView, InputMethodManager.HIDE_NOT_ALWAYS, runnable);
    }

    /**
     * 自定义实现
     * @param tokenView
     * @param flag
     * @return
     */
	public void hideSoftInputPost(View tokenView, int flag, final Runnable runnable){
		
		if(mActivity.isFinishing() || mSoftKeyLocked)
			return;
		
		if(hideSoftInput(tokenView, flag)){
			
			mSoftKeyLocked = true;
			tokenView.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					mSoftKeyLocked = false;
					 if(!mActivity.isFinishing())
						 runnable.run();
				}
			}, HIDE_KEYBOARD_MILLIS);
			
		}else{
			
			runnable.run();
		}
	}
	
	public boolean isSoftKeyLocked(){
		
		return mSoftKeyLocked;
	}

	public void setKeyBoardListener(KeyBoardListener lisn){

		mKeyBoardLisn = lisn;
		mKeyboardView = mActivity.findViewById(android.R.id.content);
		if(mKeyboardView != null)
			mKeyboardView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	public boolean isKeyboardShowing(){

		return mKeyboardShowing;
	}

	public void destroy() {

		if(mKeyboardView != null)
			ViewUtil.removeOnGlobalLayoutListener(mKeyboardView, this);
	}

	@Override
	public void onGlobalLayout() {

		if(mKeyboardView == null)
			return;

		int currHeight = mKeyboardView.getHeight();
		if (currHeight == 0)
			return;

		boolean hasChange = false;
		if (mPreHeight == 0) {

			mPreHeight = currHeight;
			mOriginHeight = currHeight;
		} else {

			if (mPreHeight != currHeight) {

				hasChange = true;
				mPreHeight = currHeight;
			} else {

				hasChange = false;
			}
		}

		if (hasChange) {

			boolean isShow;
			int keyboardHeight = 0;
			if (mOriginHeight == currHeight) {
				//hidden
				isShow = false;
			} else {
				//show
				keyboardHeight = mOriginHeight - currHeight;
				isShow = true;
			}

			mKeyboardShowing = isShow;

			if (mKeyBoardLisn != null)
				mKeyBoardLisn.onKeyboardChanged(isShow, keyboardHeight);
		}
	}

	public interface KeyBoardListener {

		void onKeyboardChanged(boolean isShow, int keyboardHeight);
	}
}
