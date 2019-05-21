package com.androidex.asyncimage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.androidex.asyncimage.AsyncImageLoader.ImageCallback;
import com.androidex.asyncimage.AsyncImageLoader.ImageTask;
import com.androidex.drawable.CircleBitmapDrawable;
import com.androidex.R;
import com.androidex.util.ImageUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.androidex.util.ViewUtil;

import java.io.File;

public class AsyncImageView extends ImageView implements ImageCallback, ImageUtil.Constants{

    public static final int STATE_IDLE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISHED = 2;

    public static final int DRAWABLE_RESID_NONE = 0;
    public static final String DRAWABLE_URI_NONE = TextUtil.TEXT_EMPTY;

    private static AsyncImageLoader mAsyncImageLoader;

    private int mCurrentState = STATE_IDLE;
    private boolean mIsAsyncMode = true;
    protected String mCurrentUri, mFailUri;
    protected boolean mIsCache;
    protected int mDefDrawableId = DRAWABLE_RESID_NONE;
    protected String mDefDrawableUri = TextUtil.TEXT_EMPTY;
    private boolean mFadeIn = true;
    private boolean mCacheFadeIn = false;
    private int mDestW, mDestH;//图片目标尺寸:高和宽
    private ImageTask mImageTask;
    private AsyncImageListener mAsyncImageLisn;
    private boolean mRemoveCacheOnDetachedFromWindow = false;
    private IRefBitmap mImageDrawable,mBgDrawable;
    private boolean mFirstVisible = true;
    private String mImageTag;
    private boolean mAutoRecycleEnable = true;
    private boolean mIsCircleEnable;
    private short mFadeInDuration = 300;
    private boolean mDefResIdIsColor;
    private boolean mIsImageHQ = true;

    public AsyncImageView(Context context){

        super(context);
        init();
    }

    public AsyncImageView(Context context, AttributeSet attrs){

        this(context, attrs, 0);
    }

    public AsyncImageView(Context context, AttributeSet attr, int defStyle){

        super(context, attr, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attr, R.styleable.AsyncImageView);
        mDefDrawableId = ta.getResourceId(R.styleable.AsyncImageView_asyncBackground, DRAWABLE_RESID_NONE);
        ta.recycle();
        init();
    }

    private void init(){

        mImageTag = String.valueOf(getContext().hashCode());
        setDrawingCacheEnabled(false);
        initImageLoaderIfNull();
    }

    private static void initImageLoaderIfNull(){

        if(mAsyncImageLoader == null){

            mAsyncImageLoader = AsyncImageLoader.newInstance(1024 * 1024 * 5,
                    new File(Environment.getExternalStorageDirectory(), "android"+File.separator+"asyncimage"));
        }
    }

    public static void initImageLoader(int cacheByteSize, File imageDir){

        if(mAsyncImageLoader == null)
            mAsyncImageLoader = AsyncImageLoader.newInstance(cacheByteSize, imageDir);
    }

    public static AsyncImageLoader getAsyncImageLoader(){

        initImageLoaderIfNull();
        return mAsyncImageLoader;
    }

    public static ImageRemoteDownloader getImageDownloader(){

        initImageLoaderIfNull();
        return mAsyncImageLoader.getImageDownloader();
    }

    public static File getRemoteImageDir(){

        initImageLoaderIfNull();
        return mAsyncImageLoader.getRemoteImageDir();
    }

    public static File getRemoteImageFile(String imageUri){

        initImageLoaderIfNull();
        return mAsyncImageLoader.getRemoteImageFile(imageUri);
    }

    public String getImageTag(){

        return mImageTag;
    }

    public String getImageUri(){

        return mCurrentUri;
    }

    public void setFailUri(String uri){

        mFailUri = uri;
    }

    public void setDefDrawableId(int defDrawableId) {

        mDefDrawableId = defDrawableId;
        setBackgroundResource(defDrawableId);
    }

    public static boolean checkAsyncImageInCache(String imageUri, int destW, int destH, String imageTag){

        return mAsyncImageLoader.getDrawableFromImageCache(imageUri, destW, destH, imageTag) != null;
    }

    public static boolean isRemoteImageExists(String imageUri){

        initImageLoaderIfNull();
        return mAsyncImageLoader.isRemoteImageExists(imageUri);
    }

    public static void clearCache(){

        if(mAsyncImageLoader != null)
            mAsyncImageLoader.clearCache();
    }

    public static void clearCacheIfOverFlow(int maxNumOfPixels){

        if(mAsyncImageLoader != null)
            mAsyncImageLoader.clearCacheIfOverFlow(maxNumOfPixels);
    }

    public static void clearCacheByImageTag(String imageTag){

        if(mAsyncImageLoader != null)
            mAsyncImageLoader.clearCacheByImageTag(imageTag);
    }

    public static void shutdownImageLoader(){

        if(mAsyncImageLoader != null){

            mAsyncImageLoader.shutdown();
            mAsyncImageLoader = null;
        }
    }

    public static boolean isImageLoaderShutdown(){

        return mAsyncImageLoader == null;
    }
	
	/*
	 * ------------------------------------------------------------------------------------------------ 
	 */

    public void setAsyncImageListener(AsyncImageListener lisn){

        mAsyncImageLisn = lisn;
    }

    public void setFadeIn(boolean fadeIn){

        mFadeIn = fadeIn;
    }

    public void setCacheFadeIn(boolean fadeIn){

        mCacheFadeIn = fadeIn;
    }

    public void setAutoRecycleEnable(boolean enable){

        mAutoRecycleEnable = enable;
    }

    public void setImageTag(String imageTag){

        mImageTag = imageTag;
    }

    public void setCircleEnable(boolean enable){

        mIsCircleEnable = enable;
    }

    public void setDefResIdIsColor(boolean resIdIsColor){

        mDefResIdIsColor = resIdIsColor;
    }

    public void setAsyncImageHQ(boolean isHQ){

        mIsImageHQ = isHQ;
    }

    public void setFadeInDuration(short duration){

        mFadeInDuration = duration;
    }

//    @Deprecated
//    public boolean isAsyncImageIncache(){
//
//        return mAsyncImageLoader.getDrawableFromImageCache(mCurrentUri, mDestW, mDestH, mImageTag) != null;
//    }

	/*
	 * 不带缓存，不支持自定义缩放 
	 */

    public void setAsyncImage(String imageUri){

        setAsyncImage(imageUri, mDefDrawableId);
    }

    public void setAsyncImage(String imageUri, int defImageRid){

        setAsyncImage(imageUri, 0, 0, false, defImageRid, DRAWABLE_URI_NONE);
    }

    public void setAsyncImage(String imageUri, String defImageUri){

        setAsyncImage(imageUri, 0, 0, false, mDefDrawableId, defImageUri);
    }
	
	/*
	 * 不带缓存，支持自定义缩放 
	 */

    public void setAsyncScaleImageByLp(String imageUri){

        setAsyncScaleImageByLp(imageUri, mDefDrawableId);
    }

    public void setAsyncScaleImageByLp(String imageUri, int defImageRid){

        setAsyncScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height, defImageRid);
    }

    public void setAsyncScaleImageByLp(String imageUri, String defImageUri){

        setAsyncScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height, defImageUri);
    }

    public void setAsyncScaleImage(String imageUri, int destW, int destH){

        setAsyncImage(imageUri, destW, destH, false, mDefDrawableId, DRAWABLE_URI_NONE);
    }

    public void setAsyncScaleImage(String imageUri, int destW, int destH, int defImageRid){

        setAsyncImage(imageUri, destW, destH, false, defImageRid, DRAWABLE_URI_NONE);
    }

    public void setAsyncScaleImage(String imageUri, int destW, int destH, String defImageUri){

        setAsyncImage(imageUri, destW, destH, false, mDefDrawableId, defImageUri);
    }

	/*
	 * 带缓存，不支持自定义缩放
	 */

    public void setAsyncCacheImage(String imageUri){

        setAsyncCacheImage(imageUri, mDefDrawableId);
    }

    public void setAsyncCacheImage(String imageUri, int defImageRid){

        setAsyncImage(imageUri, 0, 0, true, defImageRid, DRAWABLE_URI_NONE);
    }

    public void setAsyncCacheImage(String imageUri, String defImageUri){

        setAsyncImage(imageUri, 0, 0, true, mDefDrawableId, defImageUri);
    }

    /*
     * 带缓存，支持自定义缩放
     */

    public void setAsyncCacheScaleImageByLp(String imageUri){

        setAsyncCacheScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height, mDefDrawableId);
    }

    public void setAsyncCacheScaleImageByLp(String imageUri, int defImageRid){

        setAsyncCacheScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height, defImageRid);
    }

    public void setAsyncCacheScaleImageByLp(String imageUri, String defImageUri){

        setAsyncCacheScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height, defImageUri);
    }

    public void setAsyncCacheScaleImage(String imageUri, int destW, int destH){

        setAsyncImage(imageUri, destW, destH, true, mDefDrawableId, DRAWABLE_URI_NONE);
    }

    public void setAsyncCacheScaleImage(String imageUri, int destW, int destH, int defImageRid){

        setAsyncImage(imageUri, destW, destH, true, defImageRid, DRAWABLE_URI_NONE);
    }

    public void setAsyncCacheScaleImage(String imageUri, int destW, int destH, String defImageUri){

        setAsyncImage(imageUri, destW, destH, true, mDefDrawableId, defImageUri);
    }

    private void setAsyncImage(String imageUri, int destW, int destH, boolean isCache, int defDrawableRid, String defDrawableUri){

        if(LogMgr.isDebug())
            LogMgr.d(simpleTag(), "setAsyncImage isCache="+isCache+", w="+destW+", h="+destH+", uri="+imageUri);

        mIsAsyncMode = true;
        if(isImageLoaderShutdown() || (checkUriEquals(imageUri) && !isIdleState())){
            return;
        }

        cancelTaskIfRunning();

        setCurrentUriInfo(imageUri, destW, destH, isCache, defDrawableRid, defDrawableUri);

        //first: if image is cacheable, load drawable from cache
        if(isCache){

            IRefBitmap drawable = mAsyncImageLoader.getDrawableFromImageCache(imageUri, destW, destH, mImageTag);
            if(drawable != null){

                setImageRecycleDrawable(drawable, mFadeIn && mCacheFadeIn);
                //CompatUtil.setViewBackground(this, null);//多余操作感觉
                setCurrentState(STATE_FINISHED);
                //if(mAsyncImageLisn != null)这里其实不应该给回调，要给也不是调这个方法
                 //   mAsyncImageLisn.onImageLocalCompleted(imageUri, true);

                return;
            }
        }

        //second: set def drawable
        setDefaultDrawable(defDrawableRid, defDrawableUri);

        //third: if image no in cache exec image task
        mImageTask = mAsyncImageLoader.asyncLoadImage(imageUri, mFailUri, destW, destH, mIsImageHQ, isCache, mImageTag, this);

        if(mImageTask == null){
            setCurrentState(STATE_IDLE);
            //onImageLocalCompleted(imageUri, null);
        }else{
            setCurrentState(STATE_RUNNING);
        }
    }

    /*
     * 同步设置图片方法区
     */

    public void setSyncCacheImage(String imageUri){

        setSyncImage(imageUri, 0, 0, true);
    }

    public void setSyncCacheScaleImageByLp(String imageUri){

        setSyncCacheScaleImage(imageUri, getLayoutParams().width, getLayoutParams().height);
    }

    public void setSyncCacheScaleImage(String imageUri, int destW, int destH){

        setSyncImage(imageUri, destW, destH, true);
    }

    public void setSyncScaleImageByLp(String imageUri){

        setSyncImage(imageUri, getLayoutParams().width, getLayoutParams().height, false);
    }

    private void setSyncImage(String imageUri, int destW, int destH, boolean isCache){

        mIsAsyncMode = false;
        if(checkUriEquals(imageUri) && !isIdleState())
            return;

        cancelTaskIfRunning();
        setCurrentUriInfo(imageUri, destW, destH, isCache, DRAWABLE_RESID_NONE, DRAWABLE_URI_NONE);
        ViewUtil.setViewBackground(this, null);

        IRefBitmap drawable = mAsyncImageLoader.syncLoadImage(imageUri, destW, destH, mImageTag, isCache);
        if(drawable == null){

            setImageDrawable(null);
            setCurrentState(STATE_IDLE);
        }else{

            setImageRecycleDrawable(drawable, mFadeIn);
            setCurrentState(STATE_FINISHED);
        }
    }

    public void restoreAsyncImage(){

        if(mIsAsyncMode){

            setAsyncImage(mCurrentUri, mDestW, mDestH, mIsCache, mDefDrawableId, mDefDrawableUri);
        }else{

            setSyncImage(mCurrentUri, mDestW, mDestH, mIsCache);
        }
    }

    public void clearAsyncImage(boolean clearInfo){

        cancelTaskIfRunning();
        if(clearInfo)
            setCurrentUriInfo(DRAWABLE_URI_NONE, 0, 0, mIsCache, mDefDrawableId, DRAWABLE_URI_NONE);

        setImageDrawable(null);
        ViewUtil.setViewBackground(this, null);
        mImageDrawable = null;
        mBgDrawable = null;
        setCurrentState(STATE_IDLE);
    }

    private boolean checkUriEquals(String uri){

        return mCurrentUri == null ? false:mCurrentUri.equals(uri);
    }

    private void cancelTaskIfRunning(){

        if(mImageTask != null)
            mImageTask.cancel();
    }

    private void setCurrentUriInfo(String uri, int destW, int destH, boolean isCache, int defDrawableRid, String defDrawableUri){

        if(uri == null)
            uri = TextUtil.TEXT_EMPTY;

        mCurrentUri = uri;
        mIsCache = isCache;
        mDestW = destW;
        mDestH = destH;
        mDefDrawableId = defDrawableRid;
        mDefDrawableUri = defDrawableUri;
    }

    private void setDefaultDrawable(int defDrawableRid, String defDrawableUri){

        setImageDrawable(null);

        if(mDefResIdIsColor){

            setBackgroundColor(defDrawableRid);

        }else if(defDrawableRid > DRAWABLE_RESID_NONE){

            setBackgroundResource(defDrawableRid);

        }else if(!TextUtils.isEmpty(defDrawableUri)){

            IRefBitmap bg = mAsyncImageLoader.syncLoadImage(defDrawableUri, mDestW, mDestH, mImageTag, true);
            if(bg == null)
                ViewUtil.setViewBackground(this, null);
            else
                setBackgroundRecycleDrawable(bg);

        }else{

            ViewUtil.setViewBackground(this, null);
        }
    }

    private void setCurrentState(int state){

        mCurrentState = state;
    }

    private boolean isIdleState(){

        return mCurrentState == STATE_IDLE;
    }

    private boolean isFinishedState(){

        return mCurrentState == STATE_FINISHED;
    }

    private void setImageRecycleDrawable(IRefBitmap iDrawable, boolean fadeInAim){

        iDrawable.refCountAdd1();
        Drawable drawable = mIsCircleEnable ? new CircleBitmapDrawable(iDrawable.getBitmap()) : new BitmapDrawable(getResources(), iDrawable.getBitmap());
        if(fadeInAim){

            TransitionDrawable transDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(0X00000000), drawable});
            setImageDrawable(transDrawable);
            transDrawable.startTransition(mFadeInDuration);
            mCacheFadeIn = false;
        }else{

            setImageDrawable(drawable);
        }

        mImageDrawable = iDrawable;
    }

    private void setBackgroundRecycleDrawable(IRefBitmap drawable){

        drawable.refCountAdd1();
        ViewUtil.setViewBackground(this, new BitmapDrawable(getResources(), drawable.getBitmap()));
        mBgDrawable = drawable;
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {

        //Drawable oldDrawable = getDrawable();
        super.setBackgroundDrawable(background);
        recycleBgDrawable();
    }

    @Override
    public void setBackgroundColor(int color) {

        //Drawable oldDrawable = getDrawable();
        super.setBackgroundColor(color);
        recycleBgDrawable();
    }

    @Override
    public void setBackgroundResource(int resid) {

        //Drawable oldDrawable = getDrawable();
        super.setBackgroundResource(resid);
        recycleBgDrawable();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setBackground(Drawable background) {

        //Drawable oldDrawable = getDrawable();
        super.setBackground(background);
        recycleBgDrawable();
    }

    @Override
    public void setImageDrawable(Drawable drawable){

        //Drawable oldDrawable = getDrawable();
        super.setImageDrawable(drawable);
        recycleImageDrawable();
    }

    @Override
    public void setImageBitmap(Bitmap bmp){

        //Drawable drawable = getDrawable();
        super.setImageBitmap(bmp);
        recycleImageDrawable();
    }

    @Override
    public void setImageResource(int resId){

        //Drawable oldDrawable = getDrawable();
        super.setImageResource(resId);
        recycleImageDrawable();
    }

    @Override
    public void setImageURI(Uri uri){

        //Drawable oldDrawable = getDrawable();
        super.setImageURI(uri);
        recycleImageDrawable();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility){

        super.onWindowVisibilityChanged(visibility);
        if(!mAutoRecycleEnable)
            return;

        if(View.VISIBLE == visibility){

            if(mFirstVisible)
                mFirstVisible = false;
            else
                restoreAsyncImage();

        }else if(View.GONE == visibility){

            mFirstVisible = false;
            clearAsyncImage(false);
        }
    }

    private void recycleImageDrawable(){

        if(mImageDrawable != null){

            mImageDrawable.refCountMinus1();
            mImageDrawable = null;
        }
    }

    private void recycleBgDrawable(){

        if(mBgDrawable != null){

            mBgDrawable.refCountMinus1();
            mBgDrawable = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {

        super.onAttachedToWindow();
        //if(!mFirstVisible)//兼容Gallery?
        //    mFirstVisible = true;
    }

    @Override
    protected void onDetachedFromWindow(){

        super.onDetachedFromWindow();
        if(mRemoveCacheOnDetachedFromWindow && mAsyncImageLoader != null){

            if(mIsCache)
                mAsyncImageLoader.removeCache(mCurrentUri, mDestW, mDestH, mImageTag);
        }
    }

    public void setRemoveCacheOnDetachedFromWindow(boolean remove){

        mRemoveCacheOnDetachedFromWindow = remove;
    }

    @Override
    public void onImageLocalPre(String imageUri) {

        if(mAsyncImageLisn != null)
            mAsyncImageLisn.onImageLocalPre(imageUri);
    }

    @Override
    public void onImageRemotePre(String imageUri, int progress) {

        if(mAsyncImageLisn != null)
            mAsyncImageLisn.onImageRemotePre(imageUri, progress);
    }

    @Override
    public void onImageRemoteProgressUpdate(String imageUri, int progress) {

        if(mAsyncImageLisn != null)
            mAsyncImageLisn.onImageRemoteProgressUpdate(imageUri, progress);
    }

    @Override
    public void onImageRemoteCompleted(String imageUri, boolean result) {

        if(mAsyncImageLisn != null)
            mAsyncImageLisn.onImageRemoteCompleted(imageUri, result);
    }

    @Override
    public Bitmap onImageLocalBitmapInBackground(String iamgeUri, Bitmap bitmap) {

        if(LogMgr.isDebug()){

            if(bitmap == null)
                LogMgr.d(simpleTag(),"onImageLocalBitmapInBackground bitmap = null");
            else
                LogMgr.d(simpleTag(),"onImageLocalBitmapInBackground bitmap w = "+bitmap.getWidth()+", h="+bitmap.getHeight());
        }

        return mAsyncImageLisn == null ? bitmap : mAsyncImageLisn.onImageLocalBitmapInBackground(iamgeUri, bitmap);
    }

    @Override
    public Bitmap onImageLocalBitmap(String imageUri, Bitmap bitmap) {

        if(LogMgr.isDebug()){

            if(bitmap == null)
                LogMgr.d(simpleTag(),"onImageLocalBitmap bitmap = null");
            else
                LogMgr.d(simpleTag(),"onImageLocalBitmap bitmap w = "+bitmap.getWidth()+", h="+bitmap.getHeight());

        }
        return mAsyncImageLisn == null ? bitmap : mAsyncImageLisn.onImageLocalBitmap(imageUri, bitmap);
    }

    @Override
    public void onImageLocalCompleted(String imageUri, IRefBitmap drawable) {

        if (drawable == null) {

            setCurrentState(STATE_IDLE);
        } else {

            setImageRecycleDrawable(drawable, mFadeIn);
            setCurrentState(STATE_FINISHED);
        }

        if(mAsyncImageLisn != null)
            mAsyncImageLisn.onImageLocalCompleted(imageUri, drawable != null);
    }

    private String simpleTag(){

        return AsyncImageView.class.getSimpleName();
    }

    public static abstract class AsyncImageListener{

        public void onImageLocalPre(String imageUri){}
        public void onImageRemotePre(String imageUri, int progress){}
        public void onImageRemoteProgressUpdate(String imageUri, int progress){}
        public void onImageRemoteCompleted(String imageUri, boolean result){}
        public Bitmap onImageLocalBitmapInBackground(String imageUri, Bitmap bitmap){return bitmap;}
        public Bitmap onImageLocalBitmap(String imageUri, Bitmap bitmap){return bitmap;}
        public void onImageLocalCompleted(String imageUri, boolean result){}
    }

    //  丢弃代码
//    public void setImageFromCache(String imageUri, int defDrawableRid){
//
//        setImageFromCache(imageUri, IMAGE_MAX_PIXELS_NONE, defDrawableRid, DRAWABLE_URI_NONE);
//    }
//
//    public void setImageFromCache(String imageUri, String defDrawableUri){
//
//        setImageFromCache(imageUri, IMAGE_MAX_PIXELS_NONE, DRAWABLE_RESID_NONE, defDrawableUri);
//    }
//
//    public void setImageFromCache(String imageUri, int maxNumOfpixels, int defDrawableRid){
//
//        setImageFromCache(imageUri, maxNumOfpixels, defDrawableRid, DRAWABLE_URI_NONE);
//    }
//
//    public void setImageFromCache(String imageUri, int maxNumOfpixels, String defDrawableUri)
//    {
//        setImageFromCache(imageUri, maxNumOfpixels, DRAWABLE_RESID_NONE, defDrawableUri);
//    }
//
//    private void setImageFromCache(String imageUri, int maxNumOfpixels, int defDrawableRid, String defDrawableUri){
//
//        if(isImageLoaderShutdown() || (checkUriEquals(imageUri) && isFinishedState()))
//            return;
//
//        cancelTaskIfRunning();
//
//        IRefBitmap drawable = mAsyncImageLoader.getDrawableFromImageCache(imageUri, maxNumOfpixels, mImageTag);
//        if(drawable == null){
//
//            setCurrentUriInfo(imageUri, maxNumOfpixels, true, defDrawableRid, defDrawableUri);
//            setDefaultDrawable(defDrawableRid, defDrawableUri, mFadeIn);
//            setCurrentState(STATE_IDLE);
//        }else{
//
//            setCurrentUriInfo(imageUri, maxNumOfpixels, mFadeIn, defDrawableRid, defDrawableUri);
//            setImageRecycleDrawable(drawable, false);
//            setCurrentState(STATE_FINISHED);
//        }
//    }


//    private void setDefaultDrawable(int defDrawableRid, String defDrawableUri, boolean fadeIn){
//
//        if(defDrawableRid > DRAWABLE_RESID_NONE){
//
//            setImageDrawable(null);
//            setBackgroundResource(defDrawableRid);
//
////以下代码想多了
////            if(fadeIn){
////
////                setImageDrawable(null);
////                setBackgroundResource(defDrawableRid);
////            }else{
////
////                setImageResource(defDrawableRid);
////                CompatUtil.setViewBackground(this, null);
////            }
//
//        }else if(!TextUtils.isEmpty(defDrawableUri)){
//
//            setImageDrawable(null);
//            IRefBitmap bg = mAsyncImageLoader.syncLoadImage(defDrawableUri, IMAGE_MAX_PIXELS_NONE, mImageTag, true);
//            if(bg == null){
//
//                setBackgroundRecycleDrawable(bg);
//            }else{
//
//                CompatUtil.setViewBackground(this, null);
//            }
//
////以下代码想多了
////            if(fadeIn){
////
////                if(drawable != null)
////                    setBackgroundRecycleDrawable(drawable);
////
////                setImageDrawable(null);
////            }else{
////
////                if(drawable != null)
////                    setImageRecycleDrawable(drawable, false);
////
////                CompatUtil.setViewBackground(this, null);
////            }
//        }else{
//
//            setImageDrawable(null);
//            CompatUtil.setViewBackground(this, null);
//        }
//    }
}
