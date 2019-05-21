package com.androidex.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.androidex.asyncimage.AsyncImageView;
import com.androidex.context.ExApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {

    public static final String TAG = "ImageUtil";

    public static interface Constants {

        int IMAGE_CONNECT_TIMEOUT = 10000;
        int IMAGE_READ_TIMEOUT = 10000;
//        int IMAGE_MAX_PIXELS_NONE = -1;

        String SCHEME_FILE = "file";
        String SCHEME_HTTP = "http";
        String SCHEME_HTTPS = "https";
        String SCHEME_ASSETS = "assets";
        String SCHEME_ASSETS_FULL = "assets://";
        String SCHEME_RESOURCE = "resource";
        String SCHEME_RESOURCE_FULL = "resource://";
        String SCHEME_THUMBNAILS = "thumbnails";
        String SCHEME_THUMBNAILS_FULL = "thumbnails://";
    }

    public static Bitmap loadBitmap(File imageFile) {

        if (imageFile == null)
            return null;

        return loadBitmap(imageFile.getAbsolutePath());
    }

    public static Bitmap loadBitmap(String uri) {

        if (TextUtils.isEmpty(uri))
            return null;

        return loadBitmap(Uri.parse(uri));
    }

    public static Bitmap loadBitmap(Uri uri) {

        return loadBitmapCommon(uri, 0, 0, true, false);
    }

    public static Bitmap loadBitmapFast(File imageFile) {

        if (imageFile == null)
            return null;

        return loadBitmapFast(imageFile.getAbsolutePath());
    }

    public static Bitmap loadBitmapFast(String uri) {

        if (TextUtils.isEmpty(uri))
            return null;

        return loadBitmapFast(Uri.parse(uri));
    }

    public static Bitmap loadBitmapFast(Uri uri) {

        if (uri == null)
            return null;

        return loadBitmapCommon(uri, 0, 0, true, true);
    }

    public static Bitmap loadBitmap(File imageFile, int destW, int destH) {

        if (imageFile == null)
            return null;

        return loadBitmap(imageFile.getAbsolutePath(), destW, destH);
    }

    public static Bitmap loadBitmap(String uri, int destW, int destH) {

        if (TextUtils.isEmpty(uri))
            return null;

        return loadBitmap(Uri.parse(uri), destW, destH);
    }

    public static Bitmap loadBitmap(Uri uri, int destW, int destH) {

        return loadBitmapCommon(uri, destW, destH, true, false);
    }

    public static Bitmap loadBitmapFast(File imageFile, int destW, int destH) {

        if (imageFile == null)
            return null;

        return loadBitmapFast(imageFile.getAbsolutePath(), destW, destH);
    }

    public static Bitmap loadBitmapFast(String uri, int destW, int destH) {

        if (TextUtils.isEmpty(uri))
            return null;

        return loadBitmapFast(Uri.parse(uri), destW, destH);
    }

    public static Bitmap loadBitmapFast(Uri uri, int destW, int destH) {

        return loadBitmapCommon(uri, destW, destH, true, true);
    }

    public static Bitmap loadBitmapCommon(Uri uri, int destW, int destH, boolean isHQ, boolean fast) {

        InputStream input = null;
        Bitmap bmp = null;
        try {

            input = openInputStream(uri);
            if (input == null)
                return null;

            BitmapFactory.Options options = getOptions(fast);
            if(destW > 0 && destH > 0){

                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, options);
                IOUtil.closeInStream(input);

                if(!isHQ || options.outWidth > 4096 || options.outHeight > 4096)
                    options.inSampleSize = computeSampleSize(options, -1, destW * destH);
                else
                    options.inSampleSize = calculateInSampleSize(options, destW, destH);

                options.inJustDecodeBounds = false;
                input = openInputStream(uri);
            }

            bmp = BitmapFactory.decodeStream(input, null, options);

            if(LogMgr.isDebug()){

                if(bmp == null)
                    LogMgr.d("bmp", TAG + " loadBitmapCommon bmp = null, isHHHQ = "+isHQ);
                else
                    LogMgr.d("bmp", TAG + " loadBitmapCommon bmp w="+bmp.getWidth()+", h="+bmp.getHeight()+", isHHHQ = "+isHQ);
            }

        }catch (Throwable t) {

            handleThrowable(t, "loadBitmapCommon error="+t.getMessage()+", w="+destW+", h="+destH+" pixels=" + (destW*destH) + ", fast=" + fast + ", uri=" + uriToString(uri));
        } finally {

            IOUtil.closeInStream(input);
        }

        return bmp;
    }

    public static Bitmap loadBitmapThumbnail(String thumbnailUri, int kind) {

        if (TextUtils.isEmpty(thumbnailUri))
            return null;

        Bitmap bmp = null;
        try {

            Uri uri = Uri.parse(thumbnailUri);
            bmp = MediaStore.Images.Thumbnails.getThumbnail(ExApplication.getExContentResolver(),
                    Long.parseLong(uri.getHost()),
                    kind, null);

        }catch (Throwable t) {

            handleThrowable(t, "loadBitmapThumbnail error="+t.getMessage()+", kind=" + kind + ", uri=" + thumbnailUri);
        }

        return bmp;
    }

    //下载图片至本地保存
    public static boolean saveAsImageFile(String uri, File destFile, ImageProgressCallback callback) {

        if (TextUtils.isEmpty(uri) || destFile == null)
            return false;

        boolean result = false;
        InputStream input = null;
        FileOutputStream output = null;

        try {

            input = openInputStream(Uri.parse(uri));
            if (input == null)
                return false;

            double streamLen = 0;
            if (input instanceof RemoteInputStream) {
                streamLen = ((RemoteInputStream) input).getContentLength();
            } else {
                streamLen = input.available();
            }

            output = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int len = -1;
            int countLen = 0;
            int oldProgress = 0;
            int newProgress = 0;

            if (callback != null)
                callback.onProgressUpdate(0);

            while ((len = input.read(buffer)) != -1) {    //读取流内容

//				//网络不好时加载图片测试效果
//				ThreadUtil.sleep(100);

                output.write(buffer, 0, len);
                countLen += len;

                newProgress = (int) ((countLen / streamLen) * 100);
                if (callback != null && oldProgress != newProgress) {
                    callback.onProgressUpdate(newProgress);
                    oldProgress = newProgress;
                }
            }

            result = true;

        } catch (Throwable t) {

            handleThrowable(t, "saveAsImageFile error="+t.getMessage()+", uri="+uri+", destFile="+fileToString(destFile));

        } finally {

            IOUtil.closeInStream(input);
            IOUtil.closeOutStream(output);
        }

        return result;
    }

    public interface ImageProgressCallback {

        public void onProgressUpdate(int progress);
    }

    public static InputStream openInputStream(Uri uri) {

        if (uri == null)
            return null;

        String scheme = uri.getScheme();
        InputStream stream = null;

        if (scheme == null){

            stream = openFileInputStream(uri.toString());

        } else if(ContentResolver.SCHEME_FILE.equals(scheme)) {

            //from file
            stream = openFileInputStream(uri.getPath());

        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            // from content
            stream = openContentInputStream(uri);

        } else if (Constants.SCHEME_HTTP.equals(scheme) || Constants.SCHEME_HTTPS.equals(scheme)) {

            // from remote uri
            stream = openRemoteInputStream(uri);

        } else if (Constants.SCHEME_RESOURCE.equals(scheme)) {

            stream = openResourceStream(uri);

        } else if (Constants.SCHEME_ASSETS.equals(scheme)) {

            stream = openAssetsStream(uri);
        }

        return stream;
    }

    private static InputStream openFileInputStream(String path) {

        try {

            return new FileInputStream(path);

        } catch (Throwable t) {

            handleThrowable(t, "openFileInputStream error="+t.getMessage()+", uri=" + path);
        }

        return null;
    }

    private static InputStream openContentInputStream(Uri uri) {

        try {

            return ExApplication.getContext().getContentResolver().openInputStream(uri);

        } catch (Throwable t) {

            handleThrowable(t, "openContentInputStream error="+t.getMessage()+", uri=" + uriToString(uri));
        }

        return null;
    }

    public static InputStream openRemoteInputStream(Uri uri) {

        try {

            URLConnection conn = new URL(uri.toString()).openConnection();
            conn.setConnectTimeout(Constants.IMAGE_CONNECT_TIMEOUT);
            conn.setReadTimeout(Constants.IMAGE_READ_TIMEOUT);
            return new RemoteInputStream((InputStream) conn.getContent(), conn.getContentLength());

        } catch (Throwable t) {

            handleThrowable(t, "openRemoteInputStream error="+t.getMessage()+", uri=" + uriToString(uri));
        }

        return null;
    }

    public static class RemoteInputStream extends InputStream {

        private InputStream input;
        private int contentLength;

        public RemoteInputStream(InputStream input, int contentLength) {
            this.input = input;
            this.contentLength = contentLength;
        }

        @Override
        public int read() throws IOException {

            return input.read();
        }

        public int getContentLength() {

            return contentLength;
        }
    }

    public static InputStream openAssetsStream(Uri uri) {

        try {

            return ExApplication.getContext().getAssets().open(uri.getPath().substring(1));

        } catch (Throwable t) {

            handleThrowable(t, "openAssetsStream error=" + t.getMessage() + ", uri=" + uriToString(uri));
        }

        return null;
    }

    public static InputStream openResourceStream(Uri uri) {

        try {

            return ExApplication.getContext().getResources().openRawResource(Integer.parseInt(uri.getHost()));

        } catch (Throwable t) {

            handleThrowable(t, "openResourceStream error=" + t.getMessage() + ", uri=" + uriToString(uri));
        }

        return null;
    }

    public static Options getOptions(boolean fast) {

        if (fast) {
            return getFastOptions();
        } else {
            return new BitmapFactory.Options();
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

//    public static int calculateInSampleSize(BitmapFactory.Options options, int destW, int destH) {
//
//        // 原始图片的宽高
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > destH || width > destW) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
//            while ((halfHeight / inSampleSize) > destH
//                    && (halfWidth / inSampleSize) > destW) {
//
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }

    public static BitmapFactory.Options getFastOptions() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        //options.inTempStorage = new byte[16 * 1024];
        return options;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static boolean compressPng(Bitmap bmp, File destFile, int quality) {

        return compress(bmp, destFile, quality, CompressFormat.PNG);
    }

    public static boolean compressJpeg(Bitmap bmp, File destFile, int quality) {

        return compress(bmp, destFile, quality, CompressFormat.JPEG);
    }

    public static boolean compress(Bitmap bmp, File destFile, int quality, CompressFormat format) {

        FileOutputStream fos = null;
        boolean result = false;

        try {

            fos = new FileOutputStream(destFile);
            result = bmp.compress(format, quality, fos);

        }catch (Throwable t) {

            handleThrowable(t, "compress error="+t.getMessage()+", destFile = " + fileToString(destFile));
        } finally {

            IOUtil.closeOutStream(fos);
        }

        return result;
    }

    public static byte[] compressPngBytes(Bitmap bitmap, int quality) {

        return compressBytes(bitmap, quality, CompressFormat.PNG);
    }

    public static byte[] compressJpegBytes(Bitmap bitmap, int quality) {

        return compressBytes(bitmap, quality, CompressFormat.JPEG);
    }

    public static byte[] compressBytes(Bitmap bitmap, int quality, CompressFormat format) {

        byte[] bytes = null;
        try {

            if (bitmap != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(format, quality, baos);
                return baos.toByteArray();
            }

        } catch (Throwable t) {

            handleThrowable(t, "compressBytes error="+t.getMessage()+", bmp="+bitmap+", quality="+quality+", format="+format);
        }

        return bytes;
    }

    public static Bitmap scaleBitmap(Bitmap source, int destWidth, int destHeight) {

        Bitmap scaleBitmap = null;
        try {

            if (source != null) {

                Matrix matrix = new Matrix();
                matrix.postScale((float) destWidth / source.getWidth(), (float) destHeight / source.getHeight());
                scaleBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            }

        } catch (Throwable throwable) {

            if (LogMgr.isDebug())
                throwable.printStackTrace();
        }

        return scaleBitmap;
    }

    public static int[] getBitmapWH(String filePath) {

        return getBitmapWH(filePath, null);
    }

    public static int[] getBitmapWH(String filePath, int[] bound){

        InputStream input = null;
        try {

            if(bound == null || bound.length < 1)
                bound = new int[2];

            input = openInputStream(Uri.parse(filePath));
            BitmapFactory.Options options = getOptions(false);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            bound[0] = options.outWidth;
            bound[1] = options.outHeight;

        }catch (Throwable t) {

            if(LogMgr.isDebug())
                t.printStackTrace();

        } finally {

            IOUtil.closeInStream(input);
        }

        return bound;
    }

    public static Bitmap getScaleBitmap(Bitmap source, int destWidth, int destHeight){

        Bitmap scaleBitmap = null;
        try {

            if (source != null) {

                Matrix matrix = new Matrix();
                matrix.postScale((float) destWidth / source.getWidth(), (float) destHeight / source.getHeight());
                scaleBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            }

        } catch (Throwable throwable) {

            if (LogMgr.isDebug())
                throwable.printStackTrace();
        }

        return scaleBitmap;
    }

    public static byte[] loadBitmapByteArray(String uri, int quality) {

        if (TextUtils.isEmpty(uri))
            return null;

        return loadBitmapByteArray(Uri.parse(uri), quality);
    }

    public static byte[] loadBitmapByteArray(Uri uri, int quality) {

        if (uri == null)
            return null;

        return loadBitmapByteArray(uri, 0, 0, quality);
    }

    public static byte[] loadBitmapByteArray(Uri uri, int destW, int destH, int quality) {

        ByteArrayOutputStream baos = null;

        try {

            Bitmap bmp = loadBitmapCommon(uri, destW, destH, true, false);
            if (bmp == null)
                return null;

            baos = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.JPEG, quality, baos);
            bmp.recycle();
            return baos.toByteArray();

        }catch (Throwable t) {

            handleThrowable(t, "loadBitmapByteArray error="+t.getMessage()+", uri=" + uriToString(uri));
        } finally {

            IOUtil.closeOutStream(baos);
        }

        return null;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, int quality, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, quality, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    public static boolean isRemoteUri(String uri) {

        if (TextUtils.isEmpty(uri))
            return false;

        return uri.startsWith(Constants.SCHEME_HTTP) || uri.startsWith(Constants.SCHEME_HTTPS);
    }

    public static boolean isRemoteUri(Uri uri) {

        if (uri == null)
            return false;

        return Constants.SCHEME_HTTP.equals(uri.getScheme())|| Constants.SCHEME_HTTPS.equals(uri.getScheme());
    }

    public static boolean isThumbnailsUri(String uri) {

        if (TextUtils.isEmpty(uri))
            return false;

        return uri.startsWith(Constants.SCHEME_THUMBNAILS);
    }

    public static void recycleBitmap(Bitmap bmp) {

        if (bmp != null && !bmp.isRecycled())
            bmp.recycle();
    }

    private static void handleThrowable(Throwable t, String msg){

        if(LogMgr.isDebug()){

            LogMgr.e(TAG, t.getClass().getName()+": "+msg);
            LogMgr.writeThrowableLog(new Throwable(msg, t), TAG);
        }

        if(t instanceof OutOfMemoryError){

            AsyncImageView.clearCache();
            System.gc();
        }
    }

    private static String uriToString(Uri uri){

        return uri == null ? TextUtil.TEXT_NULL : uri.toString();
    }

    private static String fileToString(File f){

        return f == null ? TextUtil.TEXT_NULL : f.getAbsolutePath();
    }

//    /**
//     * 根据最长边获取压缩后的图片(压缩出来的图片最长边一定是>=maxSideLength)
//     *
//     * @param srcFile       图片的路径
//     * @param maxSideLength 长轴的长度，单位px
//     * @return
//     */
//    public static Bitmap getSampleSizeImage(File srcFile, int maxSideLength) {
//
//        return srcFile == null ? null : getSampleSizeImage(Uri.fromFile(srcFile), maxSideLength);
//    }
//
//    public static Bitmap getSampleSizeImage(Uri uri, int maxSideLength) {
//
//        if (uri == null)
//            return null;
//
//        try {
//
//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            newOpts.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
//            int w = newOpts.outWidth;
//            int h = newOpts.outHeight;
//
//            if (LogMgr.isDebug())
//                LogMgr.e(TAG, "getImage original ## w: " + w + " # h: " + h + " # maxSize: " + maxSideLength);
//
//            if (w <= maxSideLength && h <= maxSideLength) {
//                newOpts.inJustDecodeBounds = false;
//                return BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
//            }
//
//            int minSideLength = -1;
//            if (w > h && w > maxSideLength) {// 横图
//                w = maxSideLength;
//                h = h * w / maxSideLength;
//                minSideLength = h;
//            } else if (w < h && h > maxSideLength) {// 竖图
//                h = maxSideLength;
//                w = w * h / maxSideLength;
//                minSideLength = w;
//            } else if (w == h && w > maxSideLength) {// 方图
//                w = maxSideLength;
//                h = maxSideLength;
//                minSideLength = w;
//            }
//            int rate = computeSampleSize(newOpts, minSideLength, w * h);
//
//            if (LogMgr.isDebug())
//                LogMgr.e(TAG, "getImage compress ## rate: " + rate);
//
//            newOpts.inSampleSize = rate;
//            newOpts.inJustDecodeBounds = false;
//
//            Bitmap bitmap = BitmapFactory.decodeStream(openInputStream(uri), null, newOpts);
//
//            if (LogMgr.isDebug())
//                LogMgr.e(TAG, "getImage changed ## width: " + bitmap.getWidth() + " # height: " + bitmap.getHeight());
//
//            return bitmap;
//
//        } catch (Throwable t) {
//
//            if (LogMgr.isDebug()) {
//
//                t.printStackTrace();
//                System.gc();
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * 缩放图片，保持图片的宽高比，最长边 = maxSideLength
//     *
//     * @param bitmap        源
//     * @param maxSideLength 长轴的长度，单位px
//     * @param filePath      图片的路径，用来检验图片是否被旋转
//     * @return
//     */
//
//    public static Bitmap getScaleImage(Bitmap bitmap, int maxSideLength, File filePath) {
//
//        return getScaleImage(bitmap, maxSideLength, filePath == null ? null : filePath.getAbsolutePath());
//    }
//
//    public static Bitmap getScaleImage(Bitmap bitmap, int maxSideLength, String filePath) {
//
//        if (bitmap == null)
//            return null;
//
//        try {
//
//            int width = bitmap.getWidth();
//            int height = bitmap.getHeight();
//
//            if (LogMgr.isDebug())
//                LogMgr.e(TAG, "getImage original ## width: " + width + " # height: " + height);
//
//            Matrix matrix = new Matrix();
//            if (width > height) {
//
//                float rate = (float) maxSideLength / width;
//                matrix.postScale(rate, rate);
//            } else {
//
//                float rate = (float) maxSideLength / height;
//                matrix.postScale(rate, rate);
//            }
//
//            if (filePath != null)
//                matrix.postRotate(getImageDegree(filePath));
//
//            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//
//            if (LogMgr.isDebug())
//                LogMgr.e(TAG, "getImage changed ## width: " + newBitmap.getWidth() + " # height: " + newBitmap.getHeight());
//
//            return newBitmap;
//
//        } catch (Throwable t) {
//
//            if (LogMgr.isDebug()) {
//
//                t.printStackTrace();
//                System.gc();
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * 读取图片属性：旋转的角度
//     *
//     * @param srcPath 图片绝对路径
//     * @return degree 旋转的角度
//     */
//    public static int getImageDegree(String srcPath) {
//
//        int degree = 0;
//
//        try {
//
//            ExifInterface exifInterface = new ExifInterface(srcPath);
//            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    degree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    degree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    degree = 270;
//                    break;
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//
//        return degree;
//    }
//
//    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
//        try {
//            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//                    bitmap.getHeight(), Config.ARGB_8888);
//            Canvas canvas = new Canvas(output);
//            final Paint paint = new Paint();
//            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
//                    bitmap.getHeight());
//            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
//                    bitmap.getHeight()));
//            final float roundPx = DensityUtil.dip2px(4f);
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(Color.BLACK);
//            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//
//            final Rect src = new Rect(0, 0, bitmap.getWidth(),
//                    bitmap.getHeight());
//
//            canvas.drawBitmap(bitmap, src, rect, paint);
//            return output;
//        } catch (Exception e) {
//            return bitmap;
//        }
//    }
//
//    public static Bitmap toRoundBitmap(Bitmap bitmap) {
//
//        Bitmap roundCornerBmp = null;
//        try {
//
//            int width = bitmap.getWidth();
//            int height = bitmap.getHeight();
//            float roundPx;
//            float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
//            if (width <= height) {
//                roundPx = width / 2;
//                top = 0;
//                bottom = width;
//                left = 0;
//                right = width;
//                height = width;
//                dst_left = 0;
//                dst_top = 0;
//                dst_right = width;
//                dst_bottom = width;
//            } else {
//                roundPx = height / 2;
//                float clip = (width - height) / 2;
//                left = clip;
//                right = width - clip;
//                top = 0;
//                bottom = height;
//                width = height;
//                dst_left = 0;
//                dst_top = 0;
//                dst_right = height;
//                dst_bottom = height;
//            }
//
//            roundCornerBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//            Canvas canvas = new Canvas(roundCornerBmp);
//            final int color = 0xff424242;
//            final Paint paint = new Paint();
//            final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
//            final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
//            final RectF rectF = new RectF(dst);
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(color);
//            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//            canvas.drawBitmap(bitmap, src, dst, paint);
//
//        } catch (Exception e) {
//
//            if (LogMgr.isDebug())
//                e.printStackTrace();
//        } catch (Throwable t) {
//
//            if (LogMgr.isDebug())
//                t.printStackTrace();
//        }
//
//        return roundCornerBmp;
//    }
//
//    /**
//     * 柔化效果(高斯模糊)
//     *
//     * @param bmp
//     * @return
//     */
//    public static Bitmap blurImageAmeliorate(Bitmap bmp) {
//        long start = System.currentTimeMillis();
//        // 高斯矩阵
////        int[] gauss = new int[] { 1,2,4, 8,4,2,1,
////        						  2,4,8,16,8,4,2,
////        						  4,8,16,32,16,8,4,
////        						  8,16,32,64,32,16,8,
////        						  1,2,4, 8,4,2,1,
////        						  2,4,8,16,8,4,2,
////        						  4,8,16,32,16,8,4,
////        						  8,16,32,64,32,16,8};
////        int[] gauss = new int[] { 1, 2, 4,2,1,2,4,8,4,2,4,8,16,8,4,2,4,8,4,2,1,2,4,2,1};
//        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
//
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//
//        int pixR = 0;
//        int pixG = 0;
//        int pixB = 0;
//
//        int pixColor = 0;
//
//        int newR = 0;
//        int newG = 0;
//        int newB = 0;
//
//        int delta = 16; // 值越小图片会越亮，越大则越暗
//
//        int idx = 0;
//        int[] pixels = new int[width * height];
//        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
//        for (int i = 1, length = height - 1; i < length; i++) {
//            for (int k = 1, len = width - 1; k < len; k++) {
//                idx = 0;
//                for (int m = -1; m <= 1; m++) {
//                    for (int n = -1; n <= 1; n++) {
//                        pixColor = pixels[(i + m) * width + k + n];
//                        pixR = Color.red(pixColor);
//                        pixG = Color.green(pixColor);
//                        pixB = Color.blue(pixColor);
//
//                        newR = newR + (int) (pixR * gauss[idx]);
//                        newG = newG + (int) (pixG * gauss[idx]);
//                        newB = newB + (int) (pixB * gauss[idx]);
//                        idx++;
//                    }
//                }
//
//                newR /= delta;
//                newG /= delta;
//                newB /= delta;
//
//                newR = Math.min(255, Math.max(0, newR));
//                newG = Math.min(255, Math.max(0, newG));
//                newB = Math.min(255, Math.max(0, newB));
//
//                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
//
//                newR = 0;
//                newG = 0;
//                newB = 0;
//            }
//        }
//
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        long end = System.currentTimeMillis();
//        LogMgr.d("used time=" + (end - start));
//        return bitmap;
//    }
//
//    //编辑处理图片--------start----------
//    /**
//     * 水平方向模糊度
//     */
//    private static float hRadius = 10;
//    /**
//     * 竖直方向模糊度
//     */
//    private static float vRadius = 10;
//    /**
//     * 模糊迭代度
//     */
//    private static int iterations = 7;
//
//    /**
//     * 高斯模糊
//     */
//    public static Bitmap BoxBlurFilter(Bitmap bmp) {
//        int width
//                = bmp.getWidth();
//        int height
//                = bmp.getHeight();
//        int[]
//                inPixels = new int[width
//                * height];
//        int[]
//                outPixels = new int[width
//                * height];
//        Bitmap
//                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        bmp.getPixels(inPixels,
//                0,
//                width, 0,
//                0,
//                width, height);
//        for (int i
//                     = 0;
//             i < iterations; i++) {
//            blur(inPixels,
//                    outPixels, width, height, hRadius);
//            blur(outPixels,
//                    inPixels, height, width, vRadius);
//        }
//        blurFractional(inPixels,
//                outPixels, width, height, hRadius);
//        blurFractional(outPixels,
//                inPixels, height, width, vRadius);
//        bitmap.setPixels(inPixels,
//                0,
//                width, 0,
//                0,
//                width, height);
////        Drawable
////                drawable = new BitmapDrawable(bitmap);
//        return bitmap;
//    }
//
//    public static void blur(int[]
//                                    in, int[]
//                                    out, int width,
//                            int height,
//                            float radius) {
//        int widthMinus1
//                = width - 1;
//        int r
//                = (int)
//                radius;
//        int tableSize
//                = 2 *
//                r + 1;
//        int divide[]
//                = new int[256 *
//                tableSize];
//
//        for (int i
//                     = 0;
//             i < 256 *
//                     tableSize; i++)
//            divide[i]
//                    = i / tableSize;
//
//        int inIndex
//                = 0;
//
//        for (int y
//                     = 0;
//             y < height; y++) {
//            int outIndex
//                    = y;
//            int ta
//                    = 0,
//                    tr = 0,
//                    tg = 0,
//                    tb = 0;
//
//            for (int i
//                         = -r; i <= r; i++) {
//                int rgb
//                        = in[inIndex + clamp(i, 0,
//                        width - 1)];
//                ta
//                        += (rgb >> 24)
//                        & 0xff;
//                tr
//                        += (rgb >> 16)
//                        & 0xff;
//                tg
//                        += (rgb >> 8)
//                        & 0xff;
//                tb
//                        += rgb & 0xff;
//            }
//
//            for (int x
//                         = 0;
//                 x < width; x++) {
//                out[outIndex]
//                        = (divide[ta] << 24)
//                        | (divide[tr] << 16)
//                        |
//                        (divide[tg] << 8)
//                        | divide[tb];
//
//                int i1
//                        = x + r + 1;
//                if (i1
//                        > widthMinus1)
//                    i1
//                            = widthMinus1;
//                int i2
//                        = x - r;
//                if (i2
//                        < 0)
//                    i2
//                            = 0;
//                int rgb1
//                        = in[inIndex + i1];
//                int rgb2
//                        = in[inIndex + i2];
//
//                ta
//                        += ((rgb1 >> 24)
//                        & 0xff)
//                        - ((rgb2 >> 24)
//                        & 0xff);
//                tr
//                        += ((rgb1 & 0xff0000)
//                        - (rgb2 & 0xff0000))
//                        >> 16;
//                tg
//                        += ((rgb1 & 0xff00)
//                        - (rgb2 & 0xff00))
//                        >> 8;
//                tb
//                        += (rgb1 & 0xff)
//                        - (rgb2 & 0xff);
//                outIndex
//                        += height;
//            }
//            inIndex
//                    += width;
//        }
//    }
//
//    public static void blurFractional(int[]
//                                              in, int[]
//                                              out, int width,
//                                      int height,
//                                      float radius) {
//        radius
//                -= (int)
//                radius;
//        float f
//                = 1.0f
//                / (1 +
//                2 *
//                        radius);
//        int inIndex
//                = 0;
//
//        for (int y
//                     = 0;
//             y < height; y++) {
//            int outIndex
//                    = y;
//
//            out[outIndex]
//                    = in[0];
//            outIndex
//                    += height;
//            for (int x
//                         = 1;
//                 x < width - 1;
//                 x++) {
//                int i
//                        = inIndex + x;
//                int rgb1
//                        = in[i - 1];
//                int rgb2
//                        = in[i];
//                int rgb3
//                        = in[i + 1];
//
//                int a1
//                        = (rgb1 >> 24)
//                        & 0xff;
//                int r1
//                        = (rgb1 >> 16)
//                        & 0xff;
//                int g1
//                        = (rgb1 >> 8)
//                        & 0xff;
//                int b1
//                        = rgb1 & 0xff;
//                int a2
//                        = (rgb2 >> 24)
//                        & 0xff;
//                int r2
//                        = (rgb2 >> 16)
//                        & 0xff;
//                int g2
//                        = (rgb2 >> 8)
//                        & 0xff;
//                int b2
//                        = rgb2 & 0xff;
//                int a3
//                        = (rgb3 >> 24)
//                        & 0xff;
//                int r3
//                        = (rgb3 >> 16)
//                        & 0xff;
//                int g3
//                        = (rgb3 >> 8)
//                        & 0xff;
//                int b3
//                        = rgb3 & 0xff;
//                a1
//                        = a2 + (int)
//                        ((a1 + a3) * radius);
//                r1
//                        = r2 + (int)
//                        ((r1 + r3) * radius);
//                g1
//                        = g2 + (int)
//                        ((g1 + g3) * radius);
//                b1
//                        = b2 + (int)
//                        ((b1 + b3) * radius);
//                a1
//                        *= f;
//                r1
//                        *= f;
//                g1
//                        *= f;
//                b1
//                        *= f;
//                out[outIndex]
//                        = (a1 << 24)
//                        | (r1 << 16)
//                        | (g1 << 8)
//                        | b1;
//                outIndex
//                        += height;
//            }
//            out[outIndex]
//                    = in[width - 1];
//            inIndex
//                    += width;
//        }
//    }
//
//    public static int clamp(int x,
//                            int a,
//                            int b) {
//        return (x
//                < a) ? a : (x > b) ? b : x;
//    }
//
//
//    public static final int LEFT = 0;
//    public static final int RIGHT = 1;
//    public static final int TOP = 3;
//    public static final int BOTTOM = 4;
//
//    /**
//     * 图片去色,返回灰度图片
//     *
//     * @param bmpOriginal 传入的图片
//     * @return 去色后的图片
//     */
//    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
//        int width, height;
//        height = bmpOriginal.getHeight();
//        width = bmpOriginal.getWidth();
//        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(bmpGrayscale);
//        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        paint.setColorFilter(f);
//        c.drawBitmap(bmpOriginal, 0, 0, paint);
//        return bmpGrayscale;
//    }
//
//
//    /**
//     * 把图片变成圆角
//     *
//     * @param bitmap 需要修改的图片
//     * @param pixels 圆角的弧度
//     * @return 圆角图片
//     */
//    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
//        Bitmap output = Bitmap
//                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        final RectF rectF = new RectF(rect);
//        final float roundPx = pixels;
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        return output;
//    }
//
//    /**
//     * 水印
//     *
//     * @param
//     * @return
//     */
//    public static Bitmap createBitmapForWatermark(Bitmap src, Bitmap watermark) {
//        if (src == null) {
//            return null;
//        }
//        int w = src.getWidth();
//        int h = src.getHeight();
//        int ww = watermark.getWidth();
//        int wh = watermark.getHeight();
//        // create the new blank bitmap
//        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
//        Canvas cv = new Canvas(newb);
//        // draw src into
//        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
//        // draw watermark into
//        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
//        // save all clip
//        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
//        // store
//        cv.restore();// 存储
//        return newb;
//    }
//
//    /**
//     * 图片合成
//     *
//     * @return
//     */
//    public static Bitmap potoMix(int direction, Bitmap... bitmaps) {
//        if (bitmaps.length <= 0) {
//            return null;
//        }
//        if (bitmaps.length == 1) {
//            return bitmaps[0];
//        }
//        Bitmap newBitmap = bitmaps[0];
//// newBitmap = createBitmapForFotoMix(bitmaps[0],bitmaps[1],direction);
//        for (int i = 1; i < bitmaps.length; i++) {
//            newBitmap = createBitmapForFotoMix(newBitmap, bitmaps[i], direction);
//        }
//        return newBitmap;
//    }
//
//    private static Bitmap createBitmapForFotoMix(Bitmap first, Bitmap second, int direction) {
//        if (first == null) {
//            return null;
//        }
//        if (second == null) {
//            return first;
//        }
//        int fw = first.getWidth();
//        int fh = first.getHeight();
//        int sw = second.getWidth();
//        int sh = second.getHeight();
//        Bitmap newBitmap = null;
//        if (direction == LEFT) {
//            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(newBitmap);
//            canvas.drawBitmap(first, sw, 0, null);
//            canvas.drawBitmap(second, 0, 0, null);
//        } else if (direction == RIGHT) {
//            newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(newBitmap);
//            canvas.drawBitmap(first, 0, 0, null);
//            canvas.drawBitmap(second, fw, 0, null);
//        } else if (direction == TOP) {
//            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(newBitmap);
//            canvas.drawBitmap(first, 0, sh, null);
//            canvas.drawBitmap(second, 0, 0, null);
//        } else if (direction == BOTTOM) {
//            newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(newBitmap);
//            canvas.drawBitmap(first, 0, 0, null);
//            canvas.drawBitmap(second, 0, fh, null);
//        }
//        return newBitmap;
//    }
//
//    // 对分辨率较大的图片进行缩放
//    public static Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {
//
//        int w = bitmap.getWidth();
//
//        int h = bitmap.getHeight();
//
//        Matrix matrix = new Matrix();
//
//        float scaleWidth = ((float) width / w);
//
//        float scaleHeight = ((float) height / h);
//
//        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
//
//        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
//
//        return newbmp;
//
//    }
//
//    /**
//     * 保存图片为PNG
//     *
//     * @param bitmap
//     * @param name
//     */
//    public static void savePNG_After(Bitmap bitmap, String name) {
//        File file = new File(name);
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
//                out.flush();
//                out.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 保存图片为JPEG
//     *
//     * @param bitmap
//     * @param path
//     */
//    public static void saveJPGE_After(Bitmap bitmap, String path) {
//        File file = new File(path);
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
//                out.flush();
//                out.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }
//
//    public static Bitmap revitionImageSize(String path, int width) throws IOException {
//
//        return revitionImageSize(path, width, width);
//    }
//
//    public static Bitmap revitionImageSize(String path) throws IOException {
//
//        return revitionImageSize(path, 1000, 1000);
//    }
//
//    public static Bitmap revitionImageSize(String path, int width, int height) throws IOException {
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
//                new File(path)));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(in, null, options);
//        in.close();
//        int i = 0;
//        Bitmap bitmap = null;
//        while (true) {
//            if ((options.outWidth >> i <= width)
//                    && (options.outHeight >> i <= height)) {
//                in = new BufferedInputStream(
//                        new FileInputStream(new File(path)));
//                options.inSampleSize = (int) Math.pow(2.0D, i);
//                options.inJustDecodeBounds = false;
//                bitmap = BitmapFactory.decodeStream(in, null, options);
//                break;
//            }
//            i += 1;
//        }
//        return bitmap;
//    }
//
//    public static Bitmap convertViewToBitmap(View view) {
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();
//        return bitmap;
//    }
//
//    public static Bitmap convertViewToBitmapWithCanvas(View view, int bitmapWidth, int bitmapHeight) {
//        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//        view.draw(new Canvas(bitmap));
//        return bitmap;
//    }
}
