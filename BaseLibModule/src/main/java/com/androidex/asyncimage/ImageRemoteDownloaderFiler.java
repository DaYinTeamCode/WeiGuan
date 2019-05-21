package com.androidex.asyncimage;

import android.text.TextUtils;

import com.androidex.util.FileUtil;
import com.androidex.util.LogMgr;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 图片下载器文件管理器
 * Created by yihaibin on 16/9/13.
 */
public class ImageRemoteDownloaderFiler {

    private Object lockObj = new Object();
    private int mMaxFileCount = 100;
    private int fileCount = -1;

    public ImageRemoteDownloaderFiler(int maxFileCount){

        mMaxFileCount = maxFileCount;
    }

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

        if(fileCount >= mMaxFileCount){

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

    public String simpleTag(){

        return getClass().getSimpleName();
    }
}
