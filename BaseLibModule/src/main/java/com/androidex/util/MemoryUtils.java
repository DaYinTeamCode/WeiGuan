package com.androidex.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class MemoryUtils {

    /**
     * 获取ROM总大小
     *
     * @return
     */
    public static String getROMTotalSize() {

        try {

            String[] romSizes = getROMSize();
            if (romSizes == null || romSizes.length == 0)
                return TextUtil.TEXT_EMPTY;
            else
                return TextUtil.filterNull(romSizes[0]);

        } catch (Exception e) {
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取ROM的信息
     *
     * @return String[], 0-总量，1-当前可用总量
     */
    private static String[] getROMSize() {

        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();

        String total = fileSize(totalBlocks * blockSize);
        String available = fileSize(availableBlocks * blockSize);
        return new String[]{total, available};
    }

    /**
     * 获取SD Card Size 信息
     *
     * @return String[], 0-总量，1-当前可用量
     */
    private static String[] getSDCardSize() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long availableBlocks = statFs.getAvailableBlocks();

            String total = fileSize(totalBlocks * blockSize);
            String available = fileSize(availableBlocks * blockSize);
            return new String[]{total, available};
        } else {

            return null;
        }
    }

    /**
     * 获取 RAM Size 信息
     *
     * @param context
     * @return String[]
     */
    private static String[] getRAMInfo(Context context) {
        if (context == null) {
            return null;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        String available = fileSize(mi.availMem);
        String total = fileSize(mi.totalMem);
        return new String[]{total, available};
    }

    /**
     * Calculates the total RAM of the device through Android API or /proc/meminfo.
     *
     * @param c - Context object for current running activity.
     * @return Total RAM that the device has, or DEVICEINFO_UNKNOWN = -1 in the event of an error.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMemory(Context c) {

        // memInfo.totalMem not supported in pre-Jelly Bean APIs.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            am.getMemoryInfo(memInfo);

            if (memInfo != null) {
                return memInfo.totalMem;
            } else {
                return -1;
            }

        } else {

            long totalMem = -1;
            FileInputStream stream = null;
            try {

                stream = new FileInputStream("/proc/meminfo");
                try {
                    totalMem = parseFileForValue("MemTotal", stream);
                    totalMem *= 1024;
                } finally {
                }
            } catch (IOException e) {

            } catch (Exception e) {

            } finally {

                IOUtil.closeInStream(stream);
            }

            return totalMem;
        }
    }

    public static String getRAMSize(Context context) {

        return fileSize(getTotalMemory(context));
    }

    private static int parseFileForValue(String textToMatch, FileInputStream stream) {

        byte[] buffer = new byte[1024];
        try {
            int length = stream.read(buffer);
            for (int i = 0; i < length; i++) {
                if (buffer[i] == '\n' || i == 0) {
                    if (buffer[i] == '\n') i++;
                    for (int j = i; j < length; j++) {
                        int textIndex = j - i;
                        //Text doesn't match query at some point.
                        if (buffer[j] != textToMatch.charAt(textIndex)) {
                            break;
                        }
                        //Text matches query here.
                        if (textIndex == textToMatch.length() - 1) {
                            return extractValue(buffer, j);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //Ignore any exceptions and fall through to return unknown value.
        } catch (NumberFormatException e) {
        } catch (Exception e) {

        }
        return -1;
    }

    private static int extractValue(byte[] buffer, int index) {
        while (index < buffer.length && buffer[index] != '\n') {
            if (buffer[index] >= '0' && buffer[index] <= '9') {
                int start = index;
                index++;
                while (index < buffer.length && buffer[index] >= '0' && buffer[index] <= '9') {
                    index++;
                }
                String str = new String(buffer, 0, start, index - start);
                return Integer.parseInt(str);
            }
            index++;
        }
        return -1;
    }

    /**
     * 格式化容量大小
     *
     * @param size
     * @return
     */
    private static String fileSize(long size) {

        String str = "";
        if (size >= 1000) {
            str = "KB";
            size /= 1000;
            if (size >= 1000) {
                str = "MB";
                size /= 1000;
                if (size >= 1000) {
                    str = "GB";
                    size /= 1000;
                }
            }
        }
        return size + str;
    }
}
