package com.jzyd.lib.activity;

import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.HttpTask;
import com.jzyd.lib.httptask.JzydJsonListener;
import com.jzyd.lib.httptask.ExResponse;
import com.jzyd.lib.httptask.HttpFrameParams;

public abstract class JzydHttpFrameFragment<T> extends JzydFragment {
	
	// 暂时不给事件
	// protected static final int LOGIC_EVENT_REFRESH = 1;
	// protected static final int LOGIC_EVENT_CACHE_ONLY = 2;
	// protected static final int LOGIC_EVENT_CACHE_REFRESH = 3;
	// protected static final int LOGIC_EVENT_CACHE_AUTO_REFRESH_FORE = 4;
	// protected static final int LOGIC_EVENT_CACHE_AUTO_REFRESH_FORE = 5;
	
	private HttpTask mHttpTask;
	private boolean mFrameIsNeedCache;
	
	@Override
	public void onDestroy() {

		super.onDestroy();
		abortFrameHttpTaskIfRunning();
	}

	protected void abortFrameHttpTaskIfRunning(){
		
		if(mHttpTask != null && mHttpTask.isRunning()){
			
			mHttpTask.abort();
			mHttpTask = null;
		}
	}
	
	protected boolean isFrameHttpTaskRunning(){
		
		return mHttpTask != null && mHttpTask.isRunning();
	}
	
	/*
	 * 网络刷新回调 部分----------------------------------------------------------------------
	 */
	
	/**
	 * 从网络拿数据，不存储缓存
	 */
	public boolean executeFrameRefresh(Object... params){

		return frameRefresh(false, params);
	}
	
	/**
	 * 只从网络拿数据，并回调缓存存储
	 */
	protected boolean executeFrameRefreshAndCache(Object... params){
		
		return frameRefresh(true, params);
	}	
	
	private boolean frameRefresh(boolean saveCache, Object... params){
		
		if(isFinishing() || isFrameHttpTaskRunning())
			return false;
		
		HttpFrameParams hfp = getHttpParamsOnFrameExecute(params);
		
		mFrameIsNeedCache = saveCache;
		if(hfp == null){
			
			mHttpTask = new HttpTask(null);
			mHttpTask.setListener(new FrameRefreshListener(null, saveCache));
		}else{
			
			mHttpTask = new HttpTask(hfp.params);
			mHttpTask.setListener(new FrameRefreshListener(hfp.clazz, saveCache));
		}
		mHttpTask.execute();
		return true;
	}		
	
	private final class FrameRefreshListener extends JzydJsonListener<T> {
		
		private boolean mSaveCache;
		
		public FrameRefreshListener(Class<?> clazz, boolean saveCache) {
			
			super(clazz);
			mSaveCache = saveCache;
		}

		@Override
		public void onTaskPre() {

			if(!isFinishing())
				switchLoadingOnFrameRefresh();
		}

		@Override
		public void onTaskResultDoInBackground(T t) {

			onFrameResultDoInBackground(t);
		}

		@Override
		public void onTaskSuccess(ExResponse<T> resp) {

			onTaskSuccessOnFrameRefresh(resp);
			super.onTaskSuccess(resp);
		}

		@Override
		public void onTaskResult(T result) {

			if(!isFinishing())
				switchContentOnFrameRefresh(result);

			mHttpTask = null;
		}

        @Override
		public boolean onTaskSaveCache(ExResponse<T> resp) {

			if(isFinishing())
				return false;
			else
				return mSaveCache ? onFrameSaveCache(resp) : super.onTaskSaveCache(resp);
		}
		
		@Override
		public void onTaskFailed(int failedCode, String msg) {

			if(!isFinishing())
				switchFailedOnFrameRefresh(failedCode, msg);

			mHttpTask = null;
		}
	};
	
	protected void switchLoadingOnFrameRefresh(){
		
		hideContent();
		hideContentDisable();
		hideFailed();
		showLoading();
	}

	protected void onTaskSuccessOnFrameRefresh(ExResponse<T> resp){

	}
	
	protected void switchContentOnFrameRefresh(T result){
	
		hideLoading();
		if(invalidateContent(result)){
			
			showContent();
		}else{
			
			showContentDisable();
		}
	}
	
	protected void switchFailedOnFrameRefresh(int failedCode, String msg){
		
		hideLoading();
		showFailed(failedCode, msg);
	}
	
	/*
	 * 读取本地缓存部分--------------------------------------------------------
	 */
	
	/**
	 * 只读缓存
	 * @param params
	 * @return
	 */
	protected boolean executeFrameCache(Object... params){
		
		return frameCache(false, params);
	}
	
	/**
	 * 先读取本地缓存，再进行缓存刷新，并回调缓存存储。
	 */
	protected boolean executeFrameCacheAndRefresh(Object... params){
		
		return frameCache(true, params);
	}	
	
	private boolean frameCache(boolean cacheRefresh, Object... params){
		
		if(isFinishing() || isFrameHttpTaskRunning())
			return false;
		
		HttpFrameParams htp = getHttpParamsOnFrameExecute(params);
		
		mFrameIsNeedCache = true;
		if(htp == null){
			
			mHttpTask = new HttpTask(null);
			mHttpTask.setListener(new FrameCacheListener(null, null, cacheRefresh));
		}else{
			
			mHttpTask = new HttpTask(htp.params);
			mHttpTask.setListener(new FrameCacheListener(htp.params, htp.clazz, cacheRefresh));
		}
		mHttpTask.setCacheOnly(true);
		mHttpTask.execute();
		return true;
	}
	
	private final class FrameCacheListener extends JzydJsonListener<T> {
		
		private HttpTaskParams mParams;
		private boolean mCacheRefresh;
		
		private FrameCacheListener(HttpTaskParams params, Class<?> clazz, boolean cacheRefresh) {
			
			super(clazz);
			mParams = params;
			mCacheRefresh = cacheRefresh;
		}

		@Override
		public void onTaskPre() {

			if(!isFinishing())
				switchLoadingOnFrameCache();
		}

		@Override
		public void onTaskResultDoInBackground(T t) {

			onFrameResultDoInBackground(t);
		}

		@Override
		public void onTaskSuccess(ExResponse<T> resp) {

			onTaskSuccessOnFrameCache(resp);
			super.onTaskSuccess(resp);
		}

		@Override
		public void onTaskResult(T result) {

			if(isFinishing()){

				mHttpTask = null;
				return;
			}

			boolean cacheEnable = switchContentOnFrameCache(result, mCacheRefresh);
			if(mCacheRefresh){
				
				if(onInterceptCacheRefreshStart(cacheEnable)){
					
					switchContentOnFrameCacheRefreshIntercept(cacheEnable);
					mHttpTask = null;
				}else{
					
					startFrameCacheRefresh(mParams, mClazz, cacheEnable);
				}
			}else{
				
				mHttpTask = null;
			}
		}

		@Override
		public void onTaskFailed(int failedCode, String msg) {

			if(isFinishing()){

				mHttpTask = null;
				return;
			}

			switchFailedOnFrameCache(failedCode, msg, mCacheRefresh);
			if(mCacheRefresh){
				
				if(onInterceptCacheRefreshStart(false)){
					
					switchContentOnFrameCacheRefreshIntercept(false);
					mHttpTask = null;
				}else{
					
					startFrameCacheRefresh(mParams, mClazz, false);
				}
			}else{
				
				mHttpTask = null;
			}
		}
	}
	
	protected void switchLoadingOnFrameCache(){
		
		hideContent();
		hideContentDisable();
		hideFailed();
		showLoading();
	}

	protected void onTaskSuccessOnFrameCache(ExResponse<T> resp){

	}
	
	protected boolean switchContentOnFrameCache(T result, boolean cacheRefresh){
		
		if(invalidateContent(result)){
			
			//缓存数据可用，
			hideLoading();
			showContent();
			return true;
			
		}else{
			
			//缓存数据不可用
			
			if(cacheRefresh){
				
				//开启自动刷新且有网络，继续loading界面
			}else{
				
				//未开启自动刷新，显示不可用界面
				hideLoading();
				showContentDisable();
			}
			
			return false;
		}
	}
	
	protected void switchFailedOnFrameCache(int failedCode, String msg, boolean cacheRefresh) {
		
		if(cacheRefresh){
			
			//缓存刷新失败，但是开启自动刷新，可以执行刷新缓存操作。
			//继续显示loading界面

		}else{
			
			//缓存刷新失败，且没有开启自动刷新，切换为数据不可用
			hideLoading();
			showContentDisable();
		}
	}
	
	/**
	 * 当要进行缓存刷新时，给个回调.
	 * @param cacheEnable
	 * @return true:表示该缓存刷新被拦截，框架不会执行缓存刷新，false：不拦截，继续走框架逻辑
	 */
	protected boolean onInterceptCacheRefreshStart(boolean cacheEnable) {
		
		return false;
	}
	
	/**
	 * 当缓存刷新被拦截时，回调该方法
	 * 如果缓存不可用，且缓存刷新被拦截，则切换为content页面
	 * @param cacheEnable
	 */
	protected void switchContentOnFrameCacheRefreshIntercept(boolean cacheEnable) {
		
		if(!cacheEnable){
			
			hideLoading();
			showContent();
		}
	}
	
	/*
	 * 读取缓存后的刷新操作***********************************************************************************
	 */
	
	private void startFrameCacheRefresh(HttpTaskParams params, Class<?> clazz, boolean cacheEnable){
		
		mFrameIsNeedCache = true;
		mHttpTask = new HttpTask(params);
		mHttpTask.setListener(new FrameCacheRefreshListener(clazz, cacheEnable));
		mHttpTask.execute();
	}
	
	private final class FrameCacheRefreshListener extends JzydJsonListener<T> {
		
		private boolean mCacheEnable;
		
		public FrameCacheRefreshListener(Class<?> clazz, boolean cacheEnable) {
			
			super(clazz);
			mCacheEnable = cacheEnable;
		}
		
		@Override
		public void onTaskPre() {

			if(!isFinishing())
				switchLoadingOnFrameCacheRefresh(mCacheEnable);
		}

		@Override
		public void onTaskResultDoInBackground(T t) {

			onFrameResultDoInBackground(t);
		}

		@Override
		public void onTaskSuccess(ExResponse<T> resp) {

			onTaskSuccessOnFrameCacheRefresh(resp);
			super.onTaskSuccess(resp);
		}

		@Override
		public void onTaskResult(T result) {

			if(!isFinishing())
				switchContentOnFrameCacheRefresh(result, mCacheEnable);

			mHttpTask = null;//clear
		}
		
		@Override
		public boolean onTaskSaveCache(ExResponse<T> resp) {
			
			return isFinishing() ? false : onFrameSaveCache(resp);
		}

		@Override
		public void onTaskFailed(int failedCode, String msg) {

			if(!isFinishing())
				switchFailedOnFrameCacheRefresh(failedCode, msg, mCacheEnable);

			mHttpTask = null;//clear
		}
	}

	protected void onTaskSuccessOnFrameCacheRefresh(ExResponse<T> resp){

	}
	
	protected void switchLoadingOnFrameCacheRefresh(boolean cacheEnable){
		
		//nothing
	}
	
	protected void switchContentOnFrameCacheRefresh(T result, boolean cacheEnable){
	
		if (cacheEnable) {
			
			//之前缓存可用
			
			if (invalidateContent(result)) {

				// 如果之前缓存可用，新刷新数据也可用，这里就什么都不用做了
				
			} else {

				// 如果之前缓存可用，新数据不可用,则切换为不可用界面
				hideContent();
				showContentDisable();
			}
			
		} else {

			// 之前缓存不可用
			
			hideLoading();
			if (invalidateContent(result)) {

				// 刷新成功，显示内容界面
				showContent();
			} else {
				
				// 新数据不可用
				showContentDisable();
			}
		}
	}

	protected void switchFailedOnFrameCacheRefresh(int failedCode, String msg, boolean cacheEnable) {
		
		if(cacheEnable){
			
			//如果之前缓存可用，则什么都不做
		}else{
		
			//如果之前缓存不可用，显示网络失败界面
			hideLoading();
			showFailed(failedCode, msg);
		}
	}

	protected void onFrameResultDoInBackground(T t){

	}

	protected void switchBlank(){

		hideLoading();
		hideContent();
		hideContentDisable();
		hideFailed();
	}

	protected void switchContent(){

		showContent();
		hideLoading();
		hideContentDisable();
		hideFailed();
	}

	protected void switchContentDisable(){

		hideLoading();
		hideContent();
		hideFailed();
		showContentDisable();
	}
	
	/**
	 * 缓存前台刷新和后台刷新是否存储缓存，提供默认的实现
	 * @param resp
	 * @return true:存储，false:不存储
	 */
	protected boolean onFrameSaveCache(ExResponse<T> resp) {
		
		return resp != null && resp.isSuccess();
	}
	
	protected boolean isFrameNeedCache(){
		
		return mFrameIsNeedCache;
	}
	
	protected abstract HttpFrameParams getHttpParamsOnFrameExecute(Object... params);
		
	protected abstract void showLoading();
	protected abstract void hideLoading();
	protected abstract boolean invalidateContent(T result);//return 标识数据是否可用
	protected abstract void showContent();
	protected abstract void hideContent();
	protected abstract void showContentDisable();
	protected abstract void hideContentDisable();
	protected abstract void showFailed(int failedCode, String msg);
	protected abstract void hideFailed();
}
