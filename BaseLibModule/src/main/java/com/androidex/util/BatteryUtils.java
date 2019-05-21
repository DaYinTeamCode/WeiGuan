package com.androidex.util;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by angelo at 2018/08/17
 */
public final class BatteryUtils {
    private static final String TAG = BatteryUtils.class.getSimpleName();

    private static final String CMD_STR_GET_BATTERY_INFO = "adb shell dumpsys battery";

    // The return value when get error.
    private static final String ERROR_STATUS = "-1";


    /**
     * Get battery
     *
     * @param context Context
     * @return String
     */
    public static String getBattery(Context context) {

        String b = getBatteryForLollipop(context);
        if (ERROR_STATUS.equals(b)) {
            return getBatteryCurrentLevelByCmd();
        }
        return b;
    }

    /**
     * 获取电量百分比
     * Only for L(API21) and above
     *
     * @param context Context
     * @return int
     */
    private static String getBatteryForLollipop(Context context) {

        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                BatteryManager manager = (BatteryManager) context.getApplicationContext()
                        .getSystemService(android.content.Context.BATTERY_SERVICE);
                //当前电量百分比
                return String.valueOf(manager.getIntProperty(
                        BatteryManager.BATTERY_PROPERTY_CAPACITY));
            } catch (Exception e) {
                //DO NOTHING
            }
        }
        return ERROR_STATUS;
    }

    /**
     * CMD: adb shell dumpsys battery
     * <p>
     * Result:
     * Current Battery Service state:
     * AC powered: false //有线充电器状态
     * USB powered: true //USB连接状态
     * Wireless powered: false //无线充电状态
     * Max charging current: 500000 //最大充电电流，单位微安(uA)
     * Max charging voltage: 5000000 //最大充电电压，单位微伏(uV)
     * Charge counter: 4149000 //
     * status: 2 //充电状态，UNKNOWN=1，CHARGING=2，DISCHARGING=3，NOT_CHARGING=4，FULL=5
     * health: 2 //电池健康状态
     * present: true //
     * level: 95 //当前95%
     * scale: 100 //满电100%
     * voltage: 4244 //电压
     * temperature: 250 //温度
     * technology: Li-ion
     *
     * @return int
     */
    private static String getBatteryCurrentLevelByCmd() {

        Process process = null;
        String line = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec(CMD_STR_GET_BATTERY_INFO);
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null) {
                if (line.contains("level")) {
                    sb.append(line);
                    break;
                }
            }

            String level = sb.toString();
            if (TextUtils.isEmpty(level)) {
                return ERROR_STATUS;
            }

            String[] ls = level.split(":");
            return ls[1].trim();
        } catch (Exception e) {
            //DO NOTHING
        } finally {

            if (process != null) {
                process.destroy();
            }

            IOUtil.closeReader(br);
        }

        return ERROR_STATUS;
    }

    /**
     * CMD: adb shell dumpsys battery
     * <p>
     * Result:
     * Current Battery Service state:
     * AC powered: false //有线充电器状态
     * USB powered: true //USB连接状态
     * Wireless powered: false //无线充电状态
     * Max charging current: 500000 //最大充电电流，单位微安(uA)
     * Max charging voltage: 5000000 //最大充电电压，单位微伏(uV)
     * Charge counter: 4149000 //
     * status: 2 //充电状态，UNKNOWN=1，CHARGING=2，DISCHARGING=3，NOT_CHARGING=4，FULL=5
     * health: 2 //电池健康状态
     * present: true //
     * level: 95 //当前95%
     * scale: 100 //满电100%
     * voltage: 4244 //电压
     * temperature: 250 //温度
     * technology: Li-ion
     *
     * @return int
     */
    private static String getBatteryMaxCapacityByCmd() {
        Process process = null;
        String line = "";
        StringBuilder sb = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(CMD_STR_GET_BATTERY_INFO);
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null) {
                if (line.contains("level")) {
                    sb.append(line);
                    break;
                }
            }

            String level = sb.toString();
            if (TextUtils.isEmpty(level)) {
                return ERROR_STATUS;
            }

            String[] ls = level.split(":");
            return ls[1].trim();
        } catch (Exception e) {
            //DO NOTHING
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return ERROR_STATUS;
    }
}
