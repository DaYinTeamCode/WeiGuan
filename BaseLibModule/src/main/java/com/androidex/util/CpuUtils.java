package com.androidex.util;

import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public final class CpuUtils {

    /**
     * 获取 CPU information
     * Processor	: AArch64 Processor rev 4 (aarch64)
     * Hardware	: Qualcomm Technologies, Inc MSM8953
     *
     * @return String
     */
    public static String getCpuInfo() {

        BufferedReader reader = null;
        try {

            String cpuInfo;
            reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            while ((cpuInfo = reader.readLine()) != null) {

                if (!TextUtil.isEmpty(cpuInfo) && cpuInfo.contains("Hardware")) {

                    String[] array = cpuInfo.split(":");
                    return array != null && array.length > 1 ? TextUtil.filterNull(array[1]).trim() : TextUtil.TEXT_EMPTY;
                }
            }

        }catch (Exception e) {

        } finally {

            IOUtil.closeReader(reader);
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取cpu主频 GHZ
     *
     * @return
     */
    public static String getCPUMaxFreqGHz() {

        return getCPUMaxFreqKHz() * 1f / (1000 * 1000) + "GHZ";
    }

    /**
     * 获取cpu主频 KHZ
     * @return
     */
    public static int getCPUMaxFreqKHz() {

        int maxFreq = -1;
        try {

            for (int i = 0; i < getNumberOfCPUCores(); i++) {

                String filename = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq";
                File cpuInfoMaxFreqFile = new File(filename);
                if (cpuInfoMaxFreqFile.exists()) {

                    byte[] buffer = new byte[128];
                    FileInputStream stream = null;
                    try {

                        stream = new FileInputStream(cpuInfoMaxFreqFile);
                        stream.read(buffer);
                        int endIndex = 0;
                        //Trim the first number out of the byte buffer.
                        while (buffer[endIndex] >= '0' && buffer[endIndex] <= '9'
                                && endIndex < buffer.length) endIndex++;
                        String str = new String(buffer, 0, endIndex);
                        Integer freqBound = Integer.parseInt(str);
                        if (freqBound > maxFreq)
                            maxFreq = freqBound;

                    } catch (Exception e){

                    } finally {

                        IOUtil.closeInStream(stream);
                    }
                }
            }

            if (maxFreq == -1) {

                FileInputStream stream = null;
                try {

                    stream = new FileInputStream("/proc/cpuinfo");
                    int freqBound = parseFileForValue("cpu MHz", stream);
                    freqBound *= 1000; //MHz -> kHz
                    if (freqBound > maxFreq) {
                        maxFreq = freqBound;
                    }

                } catch (Exception e){

                }finally {

                    IOUtil.closeInStream(stream);
                }
            }
        } catch (Exception e) {

            maxFreq = -1;
        }

        return maxFreq;
    }

    /**
     * 读取CPU核心数
     * 读取文件位置：/sys/devices/system/cpu/
     *
     * @return int
     */
    public static int getNumberOfCPUCores() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(getCpuFilter()).length;
        } catch (SecurityException e) {
            cores = -1;
        } catch (NullPointerException e) {
            cores = -1;
        }
        return cores;
    }

    private static FileFilter getCpuFilter() {

        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {

                String path = pathname == null ? TextUtil.TEXT_EMPTY : TextUtil.filterNull(pathname.getName());
                //regex is slow, so checking char by char.
                if (path.startsWith("cpu")) {
                    for (int i = 3; i < path.length(); i++) {
                        if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
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
}
