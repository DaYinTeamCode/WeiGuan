package com.androidex.plugin;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.androidex.util.ViewUtil;

/**
 * 布局的组件类，用于分离Activity中成块的布局
 * @author yhb
 *
 */
public abstract class ExBaseWidget implements AnimationListener{
	
	private Activity mActivity;
	private View mContentView;
	private Animation mShowAnim;
	private Animation mHideAnim;
	private AnimationListener mShowAnimLisn, mHideAnimLisn;
	private boolean mCheckAnimState = true;//转换动画时，是否保持动画运行
	private OnWidgetViewClickListener mClicker;
	private OnWidgetVisibleChangedListener mVisibleLisn;
	
	protected ExBaseWidget(Activity activity){
		
		mActivity = activity;
	}

	public String simpleTag(){

		return getClass().getSimpleName();
	}
	
	protected void setContentView(View v){
		
		mContentView = v;
	}

	protected boolean isActivityFinishing(){

		return mActivity == null || mActivity.isFinishing();
	}

	public Activity getActivity(){
		
		return mActivity;
	}

	public View getContentView(){
		
		return mContentView;
	}

	public void removeFromParentView(){

		ViewGroup parent = (ViewGroup) mContentView.getParent();
		if(parent != null)
			parent.removeView(mContentView);
	}

	/**
	 * show,hide,gone part--------------------------------------------------------------------------
	 */

	public boolean show(){

		return changeVisible(View.VISIBLE);
	}

	public boolean hide(){

		return changeVisible(View.INVISIBLE);
	}

	public boolean gone(){

		return changeVisible(View.GONE);
	}

	private boolean changeVisible(int visible){

		clearContentViewAnimation();

		boolean result;

		switch(visible){
			case View.VISIBLE:
				result = ViewUtil.showView(mContentView);
				callbackWidgetVisibleChangedListener(mContentView, true);
				break;
			case View.INVISIBLE:
				result = ViewUtil.hideView(mContentView);
				callbackWidgetVisibleChangedListener(mContentView, false);
				break;
			case View.GONE:
				result = ViewUtil.goneView(mContentView);
				callbackWidgetVisibleChangedListener(mContentView, false);
				break;
			default:
				result = true;
		}

		return result;
	}

	public boolean isShowing(){

		return (ViewUtil.isShow(mContentView) || isShowAnimRunning()) && !isHideAnimRunning();
	}

	/**
	 * life part------------------------------------------------------------------------------------
	 */
	public void onStart(){}
	public void onResume(){}
	public void onPause(){}
	public void onStop(){}
	public void onDestroy(){}
	public void onActivityResult(int requestCode, int resultCode, Intent data){}

	/**
	 * animation part-------------------------------------------------------------------------------
	 */

	public void setShowAnimation(Animation anim){

		mShowAnim = anim;
		if(mShowAnim != null)
			mShowAnim.setAnimationListener(this);
	}

	public void setHideAnimation(Animation anim){

		mHideAnim = anim;
		if(mHideAnim != null)
			mHideAnim.setAnimationListener(this);
	}

	public void setShowAnimationListener(AnimationListener lisn){

		mShowAnimLisn = lisn;
	}

	public void setHideAnimationListener(AnimationListener lisn){

		mHideAnimLisn = lisn;
	}

	public void animShow() {

		if (hasShowAnimation()) {

			if (mCheckAnimState && isShowing())
				return;

			clearContentViewAnimation();
			ViewUtil.showView(mContentView);
			getContentView().startAnimation(mShowAnim);
		}
	}

	public void animHide() {

		if (hasHideAnimation()){

			if(mCheckAnimState && !isShowing())
				return;

			clearContentViewAnimation();
			ViewUtil.showView(mContentView);
			getContentView().startAnimation(mHideAnim);
		}
	}

	public boolean hasShowAnimation(){

		return mShowAnim != null;
	}

	public boolean hasHideAnimation(){

		return mHideAnim != null;
	}

	public boolean isShowAnimRunning(){

		return hasShowAnimation() && mShowAnim.hasStarted() && !mShowAnim.hasEnded();
	}

	public boolean isHideAnimRunning(){

		return hasHideAnimation() && mHideAnim.hasStarted() && !mHideAnim.hasEnded();
	}

	public void clearContentViewAnimation(){

		if(mContentView != null)
			mContentView.clearAnimation();
	}

	@Override
	public void onAnimationStart(Animation animation) {

		if(animation == mShowAnim){

			onShowAnimationStart(animation);
		}else if(animation == mHideAnim){

			onHideAnimationStart(animation);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {

		if(animation == mShowAnim) {

			ViewUtil.showView(mContentView);
			onShowAnimationEnd(animation);
		}else if(animation == mHideAnim){

			ViewUtil.hideView(mContentView);
			onHideAnimationEnd(animation);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

		if(animation == mShowAnim) {

			onShowAnimationRepeat(animation);
		}else if(animation == mHideAnim) {

			onHideAnimationRepeat(animation);
		}
	}

	public void onShowAnimationStart(Animation animation){

		if(mShowAnimLisn != null)
			mShowAnimLisn.onAnimationStart(animation);
	}

	public void onShowAnimationEnd(Animation animation){

		if(mShowAnimLisn != null)
			mShowAnimLisn.onAnimationEnd(animation);
	}

	public void onShowAnimationRepeat(Animation animation){

		if(mShowAnimLisn != null)
			mShowAnimLisn.onAnimationRepeat(animation);
	}

	public void onHideAnimationStart(Animation animation){

		if(mHideAnimLisn != null)
			mHideAnimLisn.onAnimationStart(animation);
	}

	public void onHideAnimationEnd(Animation animation){

		if(mHideAnimLisn != null)
			mHideAnimLisn.onAnimationEnd(animation);
	}

	public void onHideAnimationRepeat(Animation animation){

		if(mHideAnimLisn != null)
			mHideAnimLisn.onAnimationRepeat(animation);
	}

	/**
	 * listener part--------------------------------------------------------------------------------
	 */

	public void setOnWidgetViewClickListener(OnWidgetViewClickListener lisn){
		
		mClicker = lisn;
	}

	public void setOnWidgetVisibleChangedListener(OnWidgetVisibleChangedListener lisn){

		mVisibleLisn = lisn;
	}

	protected void callbackWidgetViewClickListener(View v){
		
		if(mClicker != null)
			mClicker.onWidgetViewClick(v);
	}

	protected void callbackWidgetVisibleChangedListener(View v, boolean showing){

		if(mVisibleLisn != null)
			mVisibleLisn.onWidgetVisibleChanged(v, showing);
	}

	public static interface OnWidgetViewClickListener{
		
		public void onWidgetViewClick(View v);
	}

	public static interface OnWidgetVisibleChangedListener{

		public void onWidgetVisibleChanged(View widget, boolean showing);
	}
}
