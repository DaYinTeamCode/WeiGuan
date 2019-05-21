package com.androidex.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.util.DeviceUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.ToastUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.ViewUtil;
import com.androidex.view.ExDecorView;
import com.ex.android.http.executer.HttpTaskExecuter;
import com.ex.android.http.executer.HttpTaskExecuterHost;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.listener.HttpTaskStringListener;

/**
 * 根据Ex框架，扩展的基类Activity，提供titlebar、toast、view，httptask 相关常用的api
 * @author yhb
 */
public abstract class ExActivity extends Activity implements HttpTaskExecuterHost {

	private ExDecorView mExDecorView;
	private HttpTaskExecuter mHttpTaskExecuter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initAndSetExDecorView();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		abortAllHttpTask();
	}

	@Override
	public void onBackPressed() {

		try{
			super.onBackPressed();
		}catch(Exception e){
			if(LogMgr.isDebug())
				e.printStackTrace();
		}
	}

	private void initAndSetExDecorView() {

		mExDecorView = new ExDecorView(this);
		super.setContentView(mExDecorView, VglpUtil.getVglpMM());
	}

	@Override
	public void setContentView(int layoutResId) {

		this.setContentView(getLayoutInflater().inflate(layoutResId, null));
	}

	@Override
	public void setContentView(View view) {

		mExDecorView.setContentView(view);
		initData();
		initTitleView();
		initContentView();
	}

	protected abstract void initData();

	protected abstract void initTitleView();

	protected abstract void initContentView();

	/**
	 * http task api
	 */

	protected boolean executeHttpTask(int what, HttpTaskParams params, HttpTaskStringListener<?> lisn){

		return executeHttpTask(what, params, false, lisn);
	}

	protected boolean executeHttpTaskCache(int what, HttpTaskParams params, HttpTaskStringListener<?> lisn){

		return executeHttpTask(what, params, true, lisn);
	}

	private boolean executeHttpTask(int what, HttpTaskParams params, boolean cacheOnly, HttpTaskStringListener<?> lisn){

		return getHttpTaskExecuter().executeHttpTask(what, params, cacheOnly, lisn);
	}

	protected boolean isHttpTaskRunning(int what) {

		return mHttpTaskExecuter == null ? false : mHttpTaskExecuter.isHttpTaskRunning(what);
	}

	protected void abortHttpTask(int what) {

		if (mHttpTaskExecuter != null)
			mHttpTaskExecuter.abortHttpTask(what);
	}

	protected void abortAllHttpTask() {

		if (mHttpTaskExecuter != null)
			mHttpTaskExecuter.abortAllHttpTask();
	}

	protected HttpTaskExecuter getHttpTaskExecuter(){

		if (mHttpTaskExecuter == null)
			mHttpTaskExecuter = new HttpTaskExecuter(this);

		return mHttpTaskExecuter;
	}

	/**
	 * HttpTaskExecuter的宿主接口也有该方法
	 * 这里重载来标识一下
	 * @return
	 */
	@Override
	public boolean isFinishing() {

		return super.isFinishing();
	}

	/**
     *  status bar api
     */

	public void setStatusBarColor(int color) {

		DeviceUtil.setStatusBarColor(this, color);
	}

	public void setStatusBarColorResource(int colorResId){

		DeviceUtil.setStatusBarColorResource(this, colorResId);
	}

	public boolean setStatusBarTranslucent(boolean translucent, boolean kitkatEnable){

		if(DeviceUtil.setStatusBarTranslucent(this, translucent, kitkatEnable)){

			return mExDecorView.setTitleViewSupportStatusBarTrans(translucent, kitkatEnable);
		}else{

			return false;
		}
	}

	public int getStatusBarHeight(){

		return DeviceUtil.STATUS_BAR_HEIGHT;
	}


	/**
	 * get decor view part
	 */

	public ExDecorView getExDecorView() {

		return mExDecorView;
	}

	public FrameLayout getTitleView() {

		return mExDecorView.getTitleView();
	}

	public LinearLayout getTitleLeftView() {

		return mExDecorView.getTitleLeftView();
	}

	public LinearLayout getTitleMiddleView() {

		return mExDecorView.getTitleMiddleView();
	}

	public LinearLayout getTitleRightView() {

		return mExDecorView.getTitleRightView();
	}

	protected void goneTitleView(){

		mExDecorView.goneTitleView();
	}

	protected void showTitleView(){

		mExDecorView.showTitleView();
	}

	protected int getTitleViewHeight(){

		return mExDecorView.getTitleHeight();
	}

	protected boolean isTitleViewSupportStatusBarTrans(){

		return mExDecorView.isTitleViewSupportStatusBarTrans();
	}

	/**
	 * add title view left part
	 */

	public ImageView addTitleLeftImageView(int icResId, OnClickListener lisn) {

		return mExDecorView.addTitleLeftImageView(icResId, lisn);
	}

	public ImageView addTitleLeftImageViewHoriWrap(int icResId, OnClickListener lisn) {

		return mExDecorView.addTitleLeftImageViewHoriWrap(icResId, lisn);
	}

	public TextView addTitleLeftTextView(int textRid, OnClickListener lisn) {

		return mExDecorView.addTitleLeftTextView(textRid, lisn);
	}

	public TextView addTitleLeftTextView(CharSequence text, OnClickListener lisn) {

		return mExDecorView.addTitleLeftTextView(text, lisn);
	}

	public void addTitleLeftView(View v) {

		mExDecorView.addTitleLeftView(v);
	}

	public void addTitleLeftView(View v, LinearLayout.LayoutParams lllp) {

		mExDecorView.addTitleLeftView(v, lllp);
	}

	public ImageView addTitleLeftBackView() {

		return mExDecorView.addTitleLeftImageViewBack(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}

	public ImageView addTitleLeftBackView(OnClickListener clickLisn) {

		return mExDecorView.addTitleLeftImageViewBack(clickLisn);
	}

	/**
	 * add title view middle part
	 */

	public ImageView addTitleMiddleImageViewWithBack(int icResId) {

		addTitleLeftBackView();
		return addTitleMiddleImageView(icResId);
	}

	public ImageView addTitleMiddleImageView(int icResId) {

		return mExDecorView.addTitleMiddleImageView(icResId);
	}

	public ImageView addTitleMiddleImageViewHoriWrapWithBack(int icResId) {

		addTitleLeftBackView();
		return addTitleMiddleImageViewHoriWrap(icResId);
	}

	public ImageView addTitleMiddleImageViewHoriWrap(int icResId) {

		return mExDecorView.addTitleMiddleImageViewHoriWrap(icResId);
	}

	public TextView addTitleMiddleTextViewWithBack(int textRid) {

		return addTitleMiddleTextViewWithBack(getResources().getText(textRid));
	}

	public TextView addTitleMiddleTextView(int textRid) {

		return mExDecorView.addTitleMiddleTextView(textRid);
	}

	public TextView addTitleMiddleTextView(CharSequence text) {

		return mExDecorView.addTitleMiddleTextView(text);
	}

	public TextView addTitleMiddleTextViewWithBack(CharSequence text) {

		addTitleLeftBackView();
		return mExDecorView.addTitleMiddleTextView(text);
	}

	public TextView addTitleMiddleTextViewMainStyle(int textResId) {

		return mExDecorView.addTitleMiddleTextViewMainStyle(textResId);
	}

	public TextView addTitleMiddleTextViewMainStyle(CharSequence text) {

		return mExDecorView.addTitleMiddleTextViewMainStyle(text);
	}

	public TextView addTitleMiddleTextViewSubStyle(int textResId) {

		return mExDecorView.addTitleMiddleTextViewSubStyle(textResId);
	}

	public TextView addTitleMiddleTextViewSubStyle(CharSequence text) {

		return mExDecorView.addTitleMiddleTextViewSubStyle(text);
	}

	public void addTitleMiddleViewWithBack(View v) {

		addTitleLeftBackView();
		addTitleMiddleView(v);
	}

	public void addTitleMiddleView(View v) {

		mExDecorView.addTitleMiddleView(v);
	}

	public void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp) {

		mExDecorView.addTitleMiddleView(v, lllp);
	}

	protected void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp, boolean horiCenter) {

		mExDecorView.addTitleMiddleView(v, lllp, horiCenter);
	}

	/**
	 * add title view right part
	 */

	public ImageView addTitleRightImageView(int icResId, OnClickListener lisn) {

		return mExDecorView.addTitleRightImageView(icResId, lisn);
	}

	public ImageView addTitleRightImageViewHoriWrap(int icResId, OnClickListener lisn) {

		return mExDecorView.addTitleRightImageViewHoriWrap(icResId, lisn);
	}

	public TextView addTitleRightTextView(int textRid, OnClickListener lisn) {

		return mExDecorView.addTitleRightTextView(textRid, lisn);
	}

	public TextView addTitleRightTextView(CharSequence text, OnClickListener lisn) {

		return mExDecorView.addTitleRightTextView(text, lisn);
	}

	public void addTitleRightView(View v) {

		mExDecorView.addTitleRightView(v);
	}

	public void addTitleRightView(View v, LinearLayout.LayoutParams lllp) {

		mExDecorView.addTitleRightView(v, lllp);
	}

	/**
	 * receiver
	 */

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {

		try {
			return super.registerReceiver(receiver, filter);
		} catch (Exception e) {

			if (LogMgr.isDebug())
				LogMgr.e(simpleTag(), "registerReceiver error, msg=" + e.getMessage());
		}
		return null;
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {

		try {
			super.unregisterReceiver(receiver);
		} catch (Exception e) {

			if (LogMgr.isDebug())
				LogMgr.e(simpleTag(), "unregisterReceiver error, msg=" + e.getMessage());
		}
	}

	/**
	 * toast part
	 */

	public void showToast(int rid) {

        ToastUtil.showToast(rid);
	}

	public void showToast(String text) {

	    ToastUtil.showToast(text);
	}

	/**
	 * view util part
	 */

	public void showView(View v) {

		ViewUtil.showView(v);
	}

	public void hideView(View v) {

		ViewUtil.hideView(v);
	}

	public void goneView(View v) {

		ViewUtil.goneView(v);
	}

	public void showImageView(ImageView v, int imageResId) {

		ViewUtil.showImageView(v, imageResId);
	}

	public void showImageView(ImageView v, Drawable drawable) {

		ViewUtil.showImageView(v, drawable);
	}

	public void hideImageView(ImageView v) {

		ViewUtil.hideImageView(v);
	}

	public void goneImageView(ImageView v) {

		ViewUtil.goneImageView(v);
	}

	/**
	 * tag part
	 */

	public String simpleTag() {

		return getClass().getSimpleName();
	}

	public String tag() {

		return getClass().getName();
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		if(LogMgr.isDebug())
			LogMgr.d(simpleTag(), simpleTag()+" finalize()");
	}
}