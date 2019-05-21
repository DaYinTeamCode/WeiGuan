package com.androidex.asyncimage;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.androidex.util.ExLruCache;
import com.androidex.util.ImageUtil;
import com.androidex.asyncimage.ImageLocalLoader.ImageLocalLoadListener;
import com.androidex.asyncimage.ImageLocalLoader.ImageLocalLoadTask;
import com.androidex.asyncimage.ImageRemoteDownloader.ImageDownloadListener;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;

import java.io.File;
import java.util.Iterator;

/**
 * @author yhb
 */
class AsyncImageLoader implements ImageUtil.Constants{

    public final String TAG = "AsyncImageLoader";

    private ExLruCache<String, IRefBitmap> mImageCache;
    private ImageRemoteDownloader mImageDownloader;
    private ImageLocalLoader mImageLocalLoader;
    private boolean mShutdown;

    private AsyncImageLoader(){

        //5MB
        this(1024 * 1024 * 5, new File(Environment.getExternalStorageDirectory(),"android"+File.separator+"asyncimage"));
    }

    private AsyncImageLoader(int cacheMaxSize, File imageSdcardDir){

        mImageLocalLoader = new ImageLocalLoader();
        mImageDownloader = new ImageRemoteDownloader(imageSdcardDir);

        mImageCache = new ExLruCache<String, IRefBitmap>(cacheMaxSize){
            @Override
            protected void entryRemoved(boolean evicted, String key, IRefBitmap oldValue, IRefBitmap newValue){

                if(oldValue != null)
                    oldValue.refCountMinus1();
            }

            @Override
            protected int sizeOf(String key, IRefBitmap value){

                return value.getByteSize();
            }
        };
    }

    public static AsyncImageLoader newInstance(){

        return new AsyncImageLoader();
    }

    public static AsyncImageLoader newInstance(int imageCacheSize, File imageDir){

        return new AsyncImageLoader(imageCacheSize, imageDir);
    }

    public ImageRemoteDownloader getImageDownloader(){

        return mImageDownloader;
    }

    public void setRemoteImageDir(String imageDir){

        mImageDownloader.setRemoteImageDir(imageDir);
    }

    public void setRemoteImageDir(File imageDir){

        mImageDownloader.setRemoteImageDir(imageDir);
    }

    public File getRemoteImageFile(String imageUri){

        return mImageDownloader.getRemoteImageFile(imageUri);
    }

    public File getRemoteImageDir(){

        return mImageDownloader.getRemoteImageDir();
    }

    public boolean isRemoteImageExists(String imageUri){

        return mImageDownloader.isRemoteImageExists(imageUri);
    }

    public void clearCache(){

        if(mShutdown)
            return;

        mImageCache.evictAll();
    }

    public void clearCacheIfOverFlow(int maxNumOfPixels){

        if(mShutdown)
            return;

        int maxSize = mImageCache.maxSize();
        int clearSize = maxNumOfPixels * 4;
        if(maxSize - mImageCache.size() >= clearSize)
            return;

        int trimSize = maxSize - clearSize;
        mImageCache.trimToSize(trimSize < 0 ? 0 : trimSize);
        mImageCache.trimToSize(maxSize);//resume max size;
    }

    public void clearCacheByImageTag(String imageTag){

        if(mShutdown || TextUtil.isEmpty(imageTag))
            return;

        Iterator<String> iterator = mImageCache.snapshot().keySet().iterator();
        String key;
        while(iterator.hasNext()){

            key = iterator.next();
            if(key != null && key.endsWith("_"+imageTag))
                mImageCache.remove(key);
        }
    }

    public void trimCacheSize(int newSize){

        if(mShutdown)
            return;

        mImageCache.trimToSize(newSize);
    }

    public void shutdown(){

        mShutdown = true;
        mImageDownloader.shutdown();
        mImageLocalLoader.shutdown();
        mImageCache.evictAll();
    }

    public void removeCache(String imageUri, int destW, int destH, String imageTag){

        if(mShutdown || TextUtils.isEmpty(imageUri))
            return;

        String key = null;
        if(TextUtil.isEmpty(imageTag)){

            key = imageUri+String.valueOf(destW * destH);
        }else{

            key = imageUri+String.valueOf(destW * destH)+"_"+imageTag;
        }

        IRefBitmap drawable = getDrawableFromImageCache(imageUri, destW, destH, imageTag);

        if(drawable != null && drawable.getRefCount() == 1){

            mImageCache.remove(key);
        }else{

            //nothing
        }
    }

    public IRefBitmap getDrawableFromImageCache(String imageUri, int destW, int destH, String imageTag){

        if(mShutdown || TextUtils.isEmpty(imageUri))
            return null;

        if(TextUtil.isEmpty(imageTag))
            return mImageCache.get(imageUri+String.valueOf(destW * destH));
        else
            return mImageCache.get(imageUri+String.valueOf(destW * destH)+"_"+imageTag);
    }

    private void putDrawableToImageCache(String imageUri, int destW, int destH, String imageTag, IRefBitmap drawable){

        if(LogMgr.isDebug())
            LogMgr.d(TAG, "putDrawableToImageCache before size="+(mImageCache.size()/1024/1024));

        drawable.refCountAdd1();
        if(TextUtil.isEmpty(imageTag))
            mImageCache.put(imageUri+String.valueOf(destW * destH), drawable);
        else
            mImageCache.put(imageUri+String.valueOf(destW * destH)+"_"+imageTag, drawable);

        if(LogMgr.isDebug())
            LogMgr.d(TAG, "putDrawableToImageCache after size=" +(mImageCache.size()/1024/1024));
    }

    public ImageTask asyncLoadImage(String imageUri, int destW, int destH, boolean isHQ, boolean isCache, String imageTag, ImageCallback imageCallback){

        return asyncLoadImage(imageUri, null, destW, destH, isHQ, isCache, imageTag, imageCallback);
    }

    public ImageTask asyncLoadImage(String imageUri, String failUri, int destW, int destH, boolean isHQ, boolean isCache, String imageTag, ImageCallback imageCallback){

        if(mShutdown || TextUtils.isEmpty(imageUri))
            return null;

        ImageTask task = new ImageTask(imageUri, failUri, destW, destH, isHQ, isCache, imageTag, imageCallback);
        task.execute();
        return task;
    }

    public void cancelImageTask(ImageTask imageTask){

        if(imageTask != null)
            imageTask.cancel();
    }

    public IRefBitmap syncLoadImage(String imageUri, int destW, int destH, String imageTag, boolean isCache){

        if(mShutdown || TextUtils.isEmpty(imageUri))
            return null;

        IRefBitmap drawable = null;

        //first: load drawable from cache
        if(isCache){
            drawable = getDrawableFromImageCache(imageUri, destW, destH, imageTag);
            if(drawable != null)
                return drawable;
        }

        //second: check image type
        Bitmap bmp = null;
        if(ImageUtil.isRemoteUri(imageUri)){

            bmp = ImageUtil.loadBitmap(mImageDownloader.getRemoteImageFile(imageUri), destW, destH);

        }else if (ImageUtil.isThumbnailsUri(imageUri)) {

            bmp = ImageUtil.loadBitmapThumbnail(imageUri, destW * destH);

        }else{

            bmp = ImageUtil.loadBitmap(imageUri, destW, destH);
        }

        //third: load drawable from uri
        if(bmp != null){

            drawable = new RefBitmap(imageUri, destW, destH, bmp);

            if(isCache)
                putDrawableToImageCache(imageUri, destW, destH, imageTag, drawable);
        }

        return drawable;
    }

    /********************** inner class part ****************************/

    public interface ImageCallback {

        void onImageLocalPre(String imageUri);
        void onImageRemotePre(String imageUri, int progress);
        void onImageRemoteProgressUpdate(String imageUri, int progress);
        void onImageRemoteCompleted(String imageUri, boolean result);
        Bitmap onImageLocalBitmapInBackground(String iamgeUri, Bitmap bmp);
        Bitmap onImageLocalBitmap(String imageUri, Bitmap bitmap);
        void onImageLocalCompleted(String imageUri, IRefBitmap drawable);
    }

    public class ImageTask implements ImageDownloadListener, ImageLocalLoadListener{

        public static final int STATE_LOCAL_NONE = 0;
        public static final int STATE_LOCAL_DOWNLOAD_BEFORE = 1;
        public static final int STATE_LOCAL_DOWNLOAD_ING = 2;
        public static final int STATE_LOCAL_DOWNLOAD_AFTER = 3;
        public static final int STATE_LOCAL_CANCELLED = 4;
        private ImageCallback mImageCallback;
        private String mImageUri, mFailUri;
        private int mDestW, mDestH;
        private boolean mIsHQ;
        private boolean mIsCache;
        private boolean mNeedDownloadImage;
        private String mImageTag;
        private ImageLocalLoadTask mImageLocalTask;

        private byte mState = STATE_LOCAL_NONE;

        public ImageTask(String imageUri, String failUri, int destW, int destH, boolean isHQ, boolean isCache, String iamgeTag, ImageCallback imageCallback) {

            mImageUri = imageUri;
            mFailUri = failUri;
            mDestW = destW;
            mDestH = destH;
            mIsHQ = isHQ;
            mIsCache = isCache;
            mImageCallback = imageCallback;
            mImageTag = iamgeTag;
        }

        public void execute(){

            if(mImageCallback != null)
                mImageCallback.onImageLocalPre(mImageUri);

            mImageLocalTask = mImageLocalLoader.execute(mImageUri, mDestW, mDestH, mIsHQ, this);
            mState = STATE_LOCAL_DOWNLOAD_BEFORE;
        }

        /*
         * running in sub thread
        */
        @Override
        public boolean onLocalCheckRemoteImageExists(String uri) {

            boolean exists = mImageDownloader.isRemoteImageExists(uri);
            mNeedDownloadImage = !exists;
            return exists;
        }

        /*
         * running in sub thread
         */
        @Override
        public String onLocalGetRemoteImagePath(String uri) {

            return mImageDownloader.getRemoteImageFile(uri).getAbsolutePath();
        }

        /*
         * running in sub thread
        */
        @Override
        public void onLocalRemoveRemoteErrorFile(String localUri) {

            mImageDownloader.removeImageFile(localUri);
        }

        @Override
        public Bitmap onLocalLoadCompletedInBackground(String uri, Bitmap bmp) {

            if(mImageCallback == null)
                return bmp;
            else
                return mImageCallback.onImageLocalBitmapInBackground(uri, bmp);
        }

        @Override
        public void onLocalLoadCompleted(Bitmap bitmap) {

            if (mState == STATE_LOCAL_CANCELLED){

                return;

            } if (mState == STATE_LOCAL_DOWNLOAD_BEFORE) {

                if (mNeedDownloadImage)
                    executeRemote();
                else
                    handleBitmapCallback(bitmap);

            } else if (mState == STATE_LOCAL_DOWNLOAD_AFTER) {

                handleBitmapCallback(bitmap);
            }

            mImageLocalTask = null;
        }

        @Override
        public void onDownloadPre(String imageUri, int progress) {

            if(mImageCallback != null)
                mImageCallback.onImageRemotePre(imageUri, progress);
        }

        @Override
        public void onProgressUpdate(String imageUri, int progress) {

            if(mImageCallback != null)
                mImageCallback.onImageRemoteProgressUpdate(imageUri, progress);
        }

        @Override
        public void onDownloadCompleted(String imageUri, boolean success) {

            if(mState == STATE_LOCAL_CANCELLED)
                return;

            if(mImageCallback != null)
                mImageCallback.onImageRemoteCompleted(imageUri, success);

            if(success){

                executeLocalAfterDownload(mImageUri);
            }else{

                if(!TextUtil.isEmpty(mFailUri))
                    executeLocalAfterDownload(mFailUri);
            }
        }

        private void handleBitmapCallback(Bitmap bitmap){

            if(mImageCallback == null)
                return;

            bitmap = mImageCallback.onImageLocalBitmap(mImageUri, bitmap);

            IRefBitmap ird = null;
            if(bitmap != null)
                ird = new RefBitmap(mImageUri, mDestW, mDestH, bitmap);

            mImageCallback.onImageLocalCompleted(mImageUri, ird);

            if(ird != null && !ird.isRecycled() && mIsCache)
                putDrawableToImageCache(mImageUri, mDestW, mDestH, mImageTag, ird);
        }

        public void executeRemote(){

            mImageDownloader.register(mImageUri, this);
            mState = STATE_LOCAL_DOWNLOAD_ING;
        }

        public void executeLocalAfterDownload(String imageUri){

            mImageLocalTask = mImageLocalLoader.execute(imageUri, mDestW, mDestH, mIsHQ, this);
            mState = STATE_LOCAL_DOWNLOAD_AFTER;
        }

        public void cancel(){

            if(mState == STATE_LOCAL_CANCELLED)
                return;

            if(mState == STATE_LOCAL_DOWNLOAD_ING){

                mImageDownloader.unRegister(mImageUri, this);
            }else{

                if(mImageLocalTask != null && mImageLocalTask.isRunning())
                    mImageLocalTask.cancel(false);

                mImageLocalTask = null;
            }

            mState = STATE_LOCAL_CANCELLED;
        }
    }
}
