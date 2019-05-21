package com.androidex.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Root permission detection utils
 * <p>
 * Created by angelo at 2018/08/17
 */
public final class RootUtils {
    private static final String TAG = RootUtils.class.getSimpleName();

    private static final String PATH_SU_FILE_BIN = "/system/bin/su";
    private static final String PATH_SU_FILE_X_BIN = "/system/xbin/su";
    private static final String[] ROOT_FILE_LIST =
            {
                    "/sbin/su",
                    "/system/bin/su",
                    "/system/xbin/su",
                    "/data/local/xbin/su",
                    "/data/local/bin/su",
                    "/system/sd/xbin/su",
                    "/system/bin/failsafe/su",
                    "/data/local/su"
            };

    public static boolean isRoot() {

        try{

            return checkSuFile()
                    || (checkRootFile() != null)
                    || isSuFileCanExecute(PATH_SU_FILE_BIN)
                    || isSuFileCanExecute(PATH_SU_FILE_X_BIN);

        }catch (Exception e){

        }

        return false;
    }


    private static boolean checkSuFile() {
        Process process = null;
        BufferedReader in = null;
        try {
            //Path1:/system/xbin/which
            //Path2:/system/bin/which
            process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = in.readLine();
            return line != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }

            IOUtil.closeReader(in);
        }
    }

    private static File checkRootFile() {
        File file;
        for (String path : ROOT_FILE_LIST) {
            file = new File(path);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    /**
     * Description: base on Linux check permission
     * Check the su file, whether or not has x or s permission.
     *
     * @param filePath su file path，eg: /system/bin/su or /system/xbin/su
     * @return boolean
     */
    private static boolean isSuFileCanExecute(String filePath) {

        java.lang.Process process = null;
        BufferedReader in = null;
        try {
            process = Runtime.getRuntime().exec("ls -l " + filePath);
            in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x') {
                    Runtime.getRuntime().exec("su ");
                    return true;
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            if (process != null) {
                process.destroy();
            }

            IOUtil.closeReader(in);
        }
        return false;
    }
}
