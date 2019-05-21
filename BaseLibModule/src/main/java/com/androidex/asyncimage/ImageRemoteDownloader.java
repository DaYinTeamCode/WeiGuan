package com.androidex.asyncimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.net.Uri;
import android.text.TextUtils;

import com.androidex.util.FileUtil;
import com.androidex.util.IOUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;

public class ImageRemoteDownloader {

	private Map<String, ImageDownloadTask> mImageDownloadTasks;
	private File mImageDir;
	private SyncImageFileUtil mSyncImageFileUtil;
	private boolean mShutdown;
	private long MAX_FILE_COUNT = 1000;
	private ImageUrlListener mHttpDnsLisn;

	public ImageRemoteDownloader(File dirFile) {

		mImageDir = dirFile;
		mSyncImageFileUtil = new SyncImageFileUtil();
		mImageDownloadTasks = new HashMap<String, ImageDownloadTask>();
	}

	public ImageRemoteDownloader(String imageSaveDir){

		this(new File(imageSaveDir));
	}

	public void setImageUrlListener(ImageUrlListener lisn){

		mHttpDnsLisn = lisn;
	}

	public boolean hasImageUrlListener(){

		return mHttpDnsLisn != null;
	}

	public void setRemoteImageDir(String imageDir){

		setRemoteImageDir(new File(imageDir));
	}

	public void setRemoteImageDir(File imageDir){

		mImageDir = imageDir;
	}

	public boolean isRemoteImageExists(String remoteUri) {

		return mSyncImageFileUtil.isImageFileExists(remoteUri, mImageDir);
	}

	public boolean removeImageFile(String localUri){

		return mSyncImageFileUtil.removeImageFile(localUri);
	}

	public File getRemoteImageFile(String remoteUri){

		if(remoteUri == null || mImageDir == null)
			return null;

		return new File(mImageDir, String.valueOf(remoteUri.hashCode()));
	}

	public File getRemoteImageDir(){

		return mImageDir;
	}

	public boolean register(String imageUri, ImageDownloadListener lisn) {

		if (mShutdown)
			return false;

		ImageDownloadTask imageTask = mImageDownloadTasks.get(imageUri);
		if (imageTask == null) {

			imageTask = new ImageDownloadTask(imageUri, mImageDir);
			imageTask.addListener(lisn);
			imageTask.execute();
			mImageDownloadTasks.put(imageUri, imageTask);
		} else {

			imageTask.addListener(lisn);
		}

		return true;
	}

	public boolean unRegister(String imageUri, ImageDownloadListener lisn) {

		if (mShutdown)
			return false;

		ImageDownloadTask imageTask = mImageDownloadTasks.get(imageUri);
		if(imageTask == null)
			return false;

		boolean result = imageTask.removeListener(lisn);
		if(imageTask.cancelTaskIfListenersEmpty())
			removeImageDownloadTask(imageUri);

		return result;
	}

	private void removeImageDownloadTask(String imageUri){

		mImageDownloadTasks.remove(imageUri);
	}

	public void shutdown(){

		clearImageDownloadTasks();
		ImageRemoteTask.shutdown();
		mHttpDnsLisn = null;
		mShutdown = true;
	}

	private void clearImageDownloadTasks() {

		Iterator<ImageDownloadTask> iterator = mImageDownloadTasks.values().iterator();
		while(iterator.hasNext()){
			iterator.next().cancelAndClearlisteners();
		}
		mImageDownloadTasks.clear();
	}

	public static interface ImageDownloadListener {

		public void onDownloadPre(String imageUri, int progress);
		public void onProgressUpdate(String imageUri, int progress);
		public void onDownloadCompleted(String imageUri, boolean success);
	}

	private class ImageDownloadTask extends ImageRemoteTask<Void, Integer, Boolean> {

		private String mImageUri;
		private File mSaveDir;
		private int mProgress;
		private Set<ImageDownloadListener> mListeners;

		private Object mLockObj = new Object();
		private boolean mRealCancelled = false;
		private boolean mDoingBackground = false;

		public ImageDownloadTask(String imageUri, File saveDir) {

			mImageUri = imageUri;
			mSaveDir = saveDir;
			mListeners = new HashSet<ImageDownloadListener>();
		}

		public void addListener(ImageDownloadListener lisn) {

			if(lisn != null){

				mListeners.add(lisn);
				lisn.onDownloadPre(mImageUri, mProgress);
			}
		}

		public boolean removeListener(ImageDownloadListener lisn) {

			return lisn == null ? false : mListeners.remove(lisn);
		}

		public void cancelAndClearlisteners(){

			cancel(false);
			mListeners.clear();
		}

		public boolean cancelTaskIfListenersEmpty() {

			if(mListeners.size() > 0)
				return false;

			cancel(false);
			synchronized (mLockObj) {

				if(mDoingBackground){
					return false;
				}else{
					mRealCancelled = true;
					return true;
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			if(mShutdown)
				return;

			mProgress = values[0];
			if(mListeners.size() > 0){

				Iterator<ImageDownloadListener> iterator = mListeners.iterator();
				while(iterator.hasNext()){
					iterator.next().onProgressUpdate(mImageUri, mProgress);
				}
			}
		}

		@Override
		protected void onPostExecute(Boolean success){

			if(mShutdown || success == null)
				return;

			if(mListeners.size() > 0){
				Iterator<ImageDownloadListener> iterator = mListeners.iterator();
				while(iterator.hasNext()){
					iterator.next().onDownloadCompleted(mImageUri, success);
				}

				mListeners.clear();
			}

			removeImageDownloadTask(mImageUri);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			synchronized (mLockObj) {

				if(mRealCancelled)
					return null;
				else
					mDoingBackground = true;
			}

			try{

				return doInBackgroundDownloadImage(mImageUri, mSaveDir);

			}catch(Throwable t){

				if(LogMgr.isDebug()){

					String msg = "ImageRemoteDownloader doInBackground error "+t.getClass().getSimpleName()+" : "+t.getMessage();
					LogMgr.e(simpleTag(), msg);
					LogMgr.writeThrowableLog(new Throwable(msg, t), simpleTag());
				}
			}

			return false;
		}

		private boolean doInBackgroundDownloadImage(String imageUri, File imageDir) {

			if(TextUtils.isEmpty(imageUri) || imageDir == null)
				return false;

			// prepare sdcard and image dir
			if (!mSyncImageFileUtil.prepareEnvironment(imageDir))
				return false;

			File loadingImageFile = new File(imageDir, imageUri.hashCode() + "ing");
			File loadedImageFile  = new File(imageDir, String.valueOf(imageUri.hashCode()));

			if (loadedImageFile.exists()) {
				//publishProgress(0);//去除进度
				//publishProgress(100);//去除进度
				return true;
			}

			if (!prepareLoadingImageFile(loadingImageFile))
				return false;
//去除进度
//			boolean result = ImageUtil.downloadImageFile(imageUri, loadingImageFile, new ImageProgressCallback() {
//
//				@Override
//				public void onProgressUpdate(int progress) {
//
//					publishProgress(progress);
//				}
//			});

			boolean result = downloadImageFile(imageUri, loadingImageFile);

			if (result) {

				if (mSyncImageFileUtil.renameImageFile(loadingImageFile, loadedImageFile)){
					return true;
				}else {
					loadingImageFile.delete();
					return false;
				}
			} else {

				loadingImageFile.delete();
				return false;
			}
		}

		private boolean downloadImageFile(String imageUrl, File destFile){

			boolean result = false;
			InputStream input = null;
			FileOutputStream output = null;

			try {

				String ip = null;
				String host = null;
				if(mHttpDnsLisn != null){

					host = Uri.parse(imageUrl).getHost();
					ip = mHttpDnsLisn.onImageUrlDnsParse(host);
				}

				URLConnection conn = null;
				if(TextUtil.isEmpty(ip)) {

					if(LogMgr.isDebug()){

						LogMgr.d(simpleTag(),"httpdns no ip url = "+imageUrl);
						LogMgr.writeTextLog("httpdns no ip url = " + imageUrl + "\n", simpleTag());
					}

					conn = new URL(imageUrl).openConnection();
				}else{

					imageUrl = imageUrl.replaceFirst(host, ip);
					if(LogMgr.isDebug()){

						LogMgr.d("httpdns", "httpdns replace host, ip url = " + imageUrl);
						LogMgr.writeTextLog("httpdns replace host, ip url = " + imageUrl + "\n", simpleTag());
					}

					conn = new URL(imageUrl).openConnection();
					conn.setRequestProperty("Host", host);
				}

				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);

				input = (InputStream) conn.getContent();

				output = new FileOutputStream(destFile);
				byte[] buffer = new byte[1024];
				int len = -1;

				while ((len = input.read(buffer)) != -1) {//读取流内容

					output.write(buffer, 0, len);
				}

				result = true;

			} catch (Throwable t) {

				if(LogMgr.isDebug()){

					String msg = "download image file error "+t.getClass().getSimpleName()+" : "+t.getMessage();
					LogMgr.e(simpleTag(), msg);
					LogMgr.writeThrowableLog(new Throwable(msg, t), simpleTag());
				}

			} finally {

				IOUtil.closeInStream(input);
				IOUtil.closeOutStream(output);
			}

			return result;
		}

		private boolean prepareLoadingImageFile(File loadingImageFile){

			try{

				if(loadingImageFile.exists())
					return loadingImageFile.delete();
				else
					return loadingImageFile.createNewFile();

			}catch(Throwable t){

				if(LogMgr.isDebug()){

					String msg = "prepareLoadingImageFile error "+t.getClass().getName()+" : "+t.getMessage();
					LogMgr.e(simpleTag(), msg);
					LogMgr.writeThrowableLog(new Throwable(msg, t), simpleTag());
				}
			}

			return false;
		}

	}


	private class SyncImageFileUtil {

		private Object lockObj = new Object();
		private int fileCount = -1;

		public boolean isImageFileExists(String imageUri, File imageDir) {

			if(TextUtils.isEmpty(imageUri) || imageDir == null)
				return false;

			synchronized (lockObj) {

				return new File(imageDir, String.valueOf(imageUri.hashCode())).exists();
			}
		}

		public boolean renameImageFile(File src, File dest) {

			if(src == null || dest == null)
				return false;

			synchronized (lockObj) {

				return src.renameTo(dest.getAbsoluteFile());
			}
		}

		public boolean removeImageFile(String localUri){

			if(localUri == null)
				return false;

			synchronized (lockObj) {

				try{

					return new File(localUri).delete();

				}catch(Exception e){

				}

				return false;
			}
		}

		public boolean prepareEnvironment(File imageDir){

			synchronized (lockObj) {

				if(imageDir.exists()){

					try{
						checkDir(imageDir);
					}catch(Throwable t){

						if(LogMgr.isDebug()){

							String msg = "prepareEnvironment checkDir error="+t.getMessage();
							LogMgr.e(simpleTag(), msg);
							LogMgr.writeThrowableLog(new Throwable(msg, t), simpleTag());
						}
					}

					return true;
				}else{

					return imageDir.mkdirs();
				}
			}
		}

		private void checkDir(File imageDir){

			File[] files = null;

			if(fileCount == -1){

				files = imageDir.listFiles();
				fileCount = files == null ? 0 : files.length;
			}

			if(LogMgr.isDebug())
				LogMgr.d(simpleTag(), "checkDir fileCount="+fileCount);

			if(fileCount >= MAX_FILE_COUNT){

				long start = 0;
				if(LogMgr.isDebug())
					start = System.currentTimeMillis();

				if(files == null)
					files = imageDir.listFiles();

				Arrays.sort(files, new Comparator<File>() {

					@Override
					public int compare(File lhs, File rhs) {

						long lhsMillis = lhs.lastModified();
						long rhsMillis = rhs.lastModified();
						if (lhsMillis < rhsMillis)
							return -1;
						else if (lhsMillis > rhsMillis)
							return 1;
						else
							return 0;
					}
				});

				if(files != null){

					int count = (int)(files.length * 0.65f);
					for(int i=0; i<count; i++){

						FileUtil.deleteFile(files[i]);
					}

					fileCount = files.length - count;
				}

				if(LogMgr.isDebug())
					LogMgr.d(simpleTag(), "clear img cost time = "+(System.currentTimeMillis() - start)+", left file count="+fileCount);
			}

			fileCount ++;
		}
	}

	private String simpleTag(){

		return getClass().getSimpleName();
	}

	public interface ImageUrlListener{

		String onImageUrlDnsParse(String host);
	}
}
