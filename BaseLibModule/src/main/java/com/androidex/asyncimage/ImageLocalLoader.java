package com.androidex.asyncimage;

import android.graphics.Bitmap;
import android.net.Uri;

import com.androidex.util.ImageUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;

public class ImageLocalLoader implements ImageUtil.Constants {

	public ImageLocalLoadTask execute(String iamgeUri, int destW, int destH, boolean isHQ, ImageLocalLoadListener lisn) {

		return (ImageLocalLoadTask) new ImageLocalLoadTask(lisn).executeTask(iamgeUri, destW, destH, isHQ);
	}

	public void shutdown() {

		ImageLocalTask.shutdown();
	}

	protected class ImageLocalLoadTask extends ImageLocalTask<Object, Void, Bitmap> {

		private ImageLocalLoadListener mLisn;

		protected ImageLocalLoadTask(ImageLocalLoadListener lisn) {

			mLisn = lisn;
		}

		public ImageLocalLoadTask executeTask(String imageUri, int destW, int destH, boolean isHQ) {

			super.execute(imageUri, destW, destH, isHQ);
			return this;
		}

		public boolean isRunning(){

			return getStatus() == Status.RUNNING;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {

			try{

				if (isCancelled())
					return null;

				String imageUri = (String) params[0];
				int destW = (Integer) params[1];
				int destH = (Integer)params[2];
				boolean isHQ = (boolean) params[3];

				if (ImageUtil.isRemoteUri(imageUri))
					return handleRemoteUri(imageUri, destW, destH, isHQ);
				else
					return handleOtherUri(imageUri, destW, destH, isHQ);

			}catch (Throwable t){

				if(LogMgr.isDebug())
					LogMgr.e(simpleTag(), simpleTag()+" doInBackground error = "+t.getMessage());
			}

			return null;
		}

		/**
		 * 处理http https 链接的图片
		 * @param imageUri
		 * @param destW
		 * @param destH
		 * @param isHQ
		 * @return
		 */
		private Bitmap handleRemoteUri(String imageUri, int destW, int destH, boolean isHQ){

			boolean exists = true;
			if (mLisn != null)
				exists = mLisn.onLocalCheckRemoteImageExists(imageUri);

			if (!exists)
				return null;

			String localUri = TextUtil.TEXT_EMPTY;
			if (mLisn != null)
				localUri = mLisn.onLocalGetRemoteImagePath(imageUri);

			Bitmap bmp = ImageUtil.loadBitmapCommon(Uri.parse(localUri), destW, destH, isHQ, false);
			if(bmp == null && mLisn != null)
				mLisn.onLocalRemoveRemoteErrorFile(localUri);

			if(mLisn != null)
				bmp = mLisn.onLocalLoadCompletedInBackground(imageUri, bmp);

			return bmp;
		}

		/**
		 * 处理其他路径的图片uri
		 * @param imageUri
		 * @param destW
		 * @param destH
		 * @param isHQ
		 * @return
		 */
		private Bitmap handleOtherUri(String imageUri, int destW, int destH, boolean isHQ) {

			Bitmap bmp = null;
			if (ImageUtil.isThumbnailsUri(imageUri))
				bmp = ImageUtil.loadBitmapThumbnail(imageUri, destW * destH);
			else
				bmp = ImageUtil.loadBitmapCommon(Uri.parse(imageUri), destW, destH, isHQ, false);

			if(mLisn != null)
				bmp = mLisn.onLocalLoadCompletedInBackground(imageUri, bmp);

			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (isCancelled()) {

				if (bitmap != null && !bitmap.isRecycled())
					bitmap.recycle();
			} else {

				if (mLisn != null)
					mLisn.onLocalLoadCompleted(bitmap);
			}
		}
	}

	public String simpleTag(){

		return getClass().getSimpleName();
	}

	public static interface ImageLocalLoadListener {

		boolean onLocalCheckRemoteImageExists(String uri);
		String onLocalGetRemoteImagePath(String uri);
		void onLocalRemoveRemoteErrorFile(String uri);
		Bitmap onLocalLoadCompletedInBackground(String uri, Bitmap bmp);
		void onLocalLoadCompleted(Bitmap bmp);
	}
}
