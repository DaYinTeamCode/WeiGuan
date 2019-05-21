package com.androidex.util;

import java.io.FileInputStream;

public class ProcessUtils {
    /**
     * 返回当前的进程名
     *
     * @return
     */
    public static String getCurrentProcessName() {
        FileInputStream in = null;
        try {
            String fn = "/proc/self/cmdline";
            in = new FileInputStream(fn);
            byte[] buffer = new byte[256];
            int len = 0;
            int b;
            while ((b = in.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }
            if (len > 0) {
                String s = new String(buffer, 0, len, "UTF-8");
                return s;
            }
        } catch (Throwable e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e) {
                }
            }
        }
        return null;
    }
}
