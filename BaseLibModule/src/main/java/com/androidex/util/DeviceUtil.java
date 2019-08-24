package com.androidex.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.androidex.context.ExApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * 设备信息获取工具类
 */
public class DeviceUtil {

    public static final int STATUS_BAR_HEIGHT = getStatusBarHeight();
    private static int mScreenWidth;
    private static int mScreenHeight;

    /**
     * 获取设备屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {

        if (mScreenWidth == 0)
            mScreenWidth = ExApplication.getContext().getResources().getDisplayMetrics().widthPixels;

        return mScreenWidth;
    }

    /**
     * 获取设备屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {

        if (mScreenHeight == 0)
            mScreenHeight = ExApplication.getContext().getResources().getDisplayMetrics().heightPixels;

        return mScreenHeight;
    }

    /**
     * 利用反射机制获取状态栏高度，不一定有效哦
     *
     * @return 状态栏的高度，如果没有获取到，则返回0
     */
    public static int getStatusBarHeight() {

        try {

            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            return ExApplication.getContext().getResources().getDimensionPixelSize(id);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return DensityUtil.dip2px(25);
    }

    /**
     * 获取当前activity 状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeightByFrame(Activity activity) {

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(Activity activity, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.getWindow().setStatusBarColor(color);
    }

    /**
     * 设置状态栏颜色资源
     *
     * @param activity
     * @param colorResId
     */
    public static void setStatusBarColorResource(Activity activity, int colorResId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(colorResId));
    }

    /**
     * 设置状态栏透明
     *
     * @param activity
     * @param translucent
     * @return
     */
    public static boolean setStatusBarTranslucent(Activity activity, boolean translucent, boolean kitkatEnable) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return false;

        try {

            //umeng日志中，在某些机型上，addFlags函数会报
            // java.lang.SecurityException: No permission to prevent power key:
            // Neither user 10069 nor current process has android.permission.PREVENT_POWER_KEY
            //所以这里做了简单的try catch 处理
            if (kitkatEnable || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (translucent) {

                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else {

                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                }

                return true;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceModel() {

        return TextUtil.filterNull(Build.MODEL);
    }

    /**
     * 获取系统版本信息
     *
     * @return
     */
    public static String getOsVersion() {

        return TextUtil.filterNull(android.os.Build.VERSION.RELEASE);
    }

    /**
     * 获取产商自己的应用商店
     *
     * @return
     */
    public static String getBrandMarketPkg() {

        if (DeviceUtil.isOppoBrand())
            return "com.oppo.market";
        else if (DeviceUtil.isXiaoMiBrand())
            return "com.xiaomi.market";
        else if (DeviceUtil.isHuaWei())
            return "com.huawei.appmarket";
        else if (DeviceUtil.isVivoBrand())
            return "com.bbk.appstore";
        else if (DeviceUtil.isMeizu())
            return "com.meizu.mstore";
        else
            return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取系统能给当前能给app分配的最大内存
     *
     * @return
     */
    public static long getRuntimeMaxMemory() {

        return Runtime.getRuntime().maxMemory();
    }

    /**
     * 优先获取imei号
     * imei号为空获取android id
     *
     * @return
     */
    public static String getDeviceId() {

        String deviceId = TextUtil.filterNull(getIMEI());
        if (!TextUtil.isEmptyTrim(deviceId))
            return deviceId;

        deviceId = TextUtil.filterNull(getAndroidId());
        if (!TextUtil.isEmptyTrim(deviceId))
            return deviceId;

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取设备IMEI号
     *
     * @return
     */
    public static String getIMEI() {

        String imei = TextUtil.TEXT_EMPTY;
        try {

            Context ctx = ExApplication.getContext();
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //26 api 开始，android 公开了这个函数
                imei = telephonyManager.getImei(0);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //21(5.0) - 25(7.1) getImei函数是隐藏的,但是在一些机型上也可以正常调用该函数:
                //4儿子可以正常调用该函数，但是返回为空
                //oppo手机可以正常调用该函数，也有返回值
                //为防止在一些机型上崩溃，还是try一下
                try {
                    imei = telephonyManager.getImei(0);
                } catch (Exception e) {
                }
            }

            //默认取deviceId
            if (TextUtil.isEmpty(imei))
                imei = telephonyManager.getDeviceId();

        } catch (Exception e) {

            e.printStackTrace();
        }

        //在一些设备上无imei号返回的是0，而不是空串
        //这里认为只要imei号大于1的长度，就认为是合法的
        if (TextUtil.trimSize(imei) > 1)
            return imei;
        else
            return TextUtil.TEXT_EMPTY;
    }

    public static String getIMEI2() {

        String imei = TextUtil.TEXT_EMPTY;
        try {

            Context ctx = ExApplication.getContext();
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //26 api 开始，android 公开了这个函数
                if (getPhoneCount(telephonyManager) > 1)
                    imei = telephonyManager.getImei(1);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //21(5.0) - 25(7.1) getImei函数是隐藏的,但是在一些机型上也可以正常调用该函数:
                //4儿子可以正常调用该函数，但是返回为空
                //oppo手机可以正常调用该函数，也有返回值
                //为防止在一些机型上崩溃，还是try一下
                try {

                    if (getPhoneCount(telephonyManager) > 1)
                        imei = telephonyManager.getImei(1);

                } catch (Exception e) {
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        //在一些设备上无imei号返回的是0，而不是空串
        //这里认为只要imei号大于1的长度，就认为是合法的
        if (TextUtil.trimSize(imei) > 1)
            return imei;
        else
            return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取sim卡数量
     *
     * @param telephonyManager
     * @return
     */
    public static int getPhoneCount(TelephonyManager telephonyManager) {

        if (telephonyManager == null)
            return 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return telephonyManager.getPhoneCount();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            try {

                Class clazz = telephonyManager.getClass();
                Method method = clazz.getDeclaredMethod("getPhoneCount");
                method.setAccessible(true);
                return (Integer) method.invoke(telephonyManager);

            } catch (Throwable e) {

                return 1;
            }

        } else {

            return 1;
        }
    }

    /**
     * 获取android id
     *
     * @return
     */
    public static String getAndroidId() {

        try {

            Context ctx = ExApplication.getContext();
            return TextUtil.filterNull(Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID));

        } catch (Exception e) {

            e.printStackTrace();
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 判断当前设备是否有电话功能
     *
     * @return
     */
    public static boolean hasPhone() {

        try {

            TelephonyManager telephony = (TelephonyManager) ExApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            return telephony.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * 检测当前设备是否安装淘宝App
     *
     * @return
     */
    public static boolean isInstallTaoBao() {

        return hasApp("com.taobao.taobao");
    }

    /**
     * 检测当前设备是否安装支付宝App
     *
     * @return
     */
    public static boolean isInstallAlipay() {

        return hasApp("com.eg.android.AlipayGphone");
    }

    /**
     * 检测当前设备上是否安装指定包名的app
     *
     * @param packageName
     * @return
     */
    public static boolean hasApp(String packageName) {

        try {

            if (TextUtil.isEmpty(packageName))
                return false;

            return ExApplication.getContext().getPackageManager().getPackageInfo(packageName, 0) != null;

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return false;
    }

    /**
     * 判断是否安装新浪微博
     *
     * @return
     */
    public static boolean hasSinaWeiboClient() {

        try {

            PackageInfo packageInfo = ExApplication.getContext().getPackageManager().getPackageInfo("com.sina.weibo", 0);
            if (packageInfo == null)
                return false;

            int highBit = packageInfo.versionName.charAt(0);
            return highBit > 50 ? true : false;// 50 = 2

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断设备当前网络是否可用
     *
     * @return
     */
    public static boolean isNetworkEnable() {

        try {

            ConnectivityManager conManager = (ConnectivityManager) ExApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();

        } catch (Throwable e) {
        }

        return true;
    }

    /**
     * 判断设备当前网络是否可用
     *
     * @return
     */
    public static boolean isNetworkDisable() {

        try {

            ConnectivityManager conManager = (ConnectivityManager) ExApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            return networkInfo == null || !networkInfo.isAvailable();

        } catch (Throwable e) {
        }

        return false;
    }

    /**
     * 判断设备链接的网络是否是wifi
     *
     * @return
     */
    public static boolean isWifiNetWork() {

        return ConnectivityManager.TYPE_WIFI == getNetworkState();
    }

    /**
     * 判断设备链接的网络是否是手机网络
     *
     * @return
     */
    public static boolean isMobileNetWork() {

        return ConnectivityManager.TYPE_MOBILE == getNetworkState();
    }

    /**
     * 获取当前设备的网络类型
     *
     * @return ConnectivityManager.TYPE_XX 各种类型，如果未获取到返回 －1;
     */
    public static int getNetworkState() {

        ConnectivityManager cm = (ConnectivityManager) ExApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info == null ? -1 : info.getType();//ConnectivityManager.TYPE_NONE = -1，但不知道为何无法引用
    }

    /**
     * 获取指定进程id的进程名称
     *
     * @param pid
     * @return
     */
    public static String getProcessName(int pid) {

        ActivityManager am = (ActivityManager) ExApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (CollectionUtil.isEmpty(runningApps))
            return TextUtil.TEXT_EMPTY;

        RunningAppProcessInfo rapi;
        for (int i = 0; i < runningApps.size(); i++) {

            rapi = runningApps.get(i);
            if (rapi.pid == pid)
                return TextUtil.filterNull(rapi.processName);
        }

        return TextUtil.TEXT_EMPTY;
    }


    /**
     * 扫描图片
     *
     * @param imagePath
     * @param lisn
     */
    public static void scanImageFile(String imagePath, MediaScannerConnection.OnScanCompletedListener lisn) {

        try {

            MediaScannerConnection.scanFile(ExApplication.getContext(), new String[]{imagePath}, null, lisn);

        } catch (Throwable t) {

            if (LogMgr.isDebug())
                t.printStackTrace();
        }
    }

    /**
     * 获取网络代理主机host
     *
     * @return
     */
    public static String getNetworkProxyHost() {

        if (CompatUtil.isIcsOrLater())
            return System.getProperty("http.proxyHost");
        else
            return android.net.Proxy.getHost(ExApplication.getContext());
    }

    /**
     * 获取网络代理端口号
     *
     * @return
     */
    public static String getNetworkProxyPort() {

        if (CompatUtil.isIcsOrLater())
            return System.getProperty("http.proxyPort");
        else
            return String.valueOf(android.net.Proxy.getPort(ExApplication.getContext()));
    }

    /**
     * 获取运营商信息 需要加入权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/> <BR>
     *
     * @return 1, 代表中国移动，2，代表中国联通，3，代表中国电信，0，代表未知
     */
    public static int getOperators() {

        String imsi = getIMSI();
        int operatorCode = 0;

        // 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）（也称为“MCC /
        // MNC”）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
        // IMSI号前面3位460是国家，紧接着后面2位00 运营商代码

        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
            operatorCode = 1;
        } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
            operatorCode = 2;
        } else if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
            operatorCode = 3;
        }
        return operatorCode;
    }

    /**
     * 获取运营商imsi号
     *
     * @return
     */
    public static String getIMSI() {

        try {

            Context ctx = ExApplication.getContext();
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return TextUtil.filterNull(telephonyManager.getSubscriberId());

        } catch (Exception e) {
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取本机ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {

                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Throwable t) {

        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    public static String getBrand() {

        return TextUtil.filterNull(Build.BRAND);
    }

    public static String getBrandAndModel() {

        return Build.BRAND + "_" + Build.MODEL;
    }

    public static boolean isOppoBrand() {

        return isBrand("oppo");
    }

    public static boolean isVivoBrand() {

        return isBrand("vivo");
    }

    public static boolean isXiaoMiBrand() {

        return isBrand("xiaomi");
    }

    public static boolean isHuaWei() {

        return isBrand("huawei") || isBrand("honor");
    }

    public static boolean isMeizu() {

        return isBrand("meizu");
    }

    private static boolean isBrand(String brand) {

        return TextUtil.filterNull(Build.BRAND).toLowerCase().contains(brand);
    }

    public static String getNetworkType() {

        String type = TextUtil.TEXT_EMPTY;

        try {

            ConnectivityManager manager = ((ConnectivityManager) ExApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
            if (manager == null)
                return type;

            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    type = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            type = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            type = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            type = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {

                                type = "3G";

                            } else {

                                type = "Unknown";
                            }
                            break;
                    }

                    if (LogMgr.isDebug())
                        LogMgr.d("Network getSubtype : " + Integer.valueOf(networkType).toString());
                }
            }

            if (LogMgr.isDebug())
                LogMgr.d("Network Type : " + type);

        } catch (Exception e) {

            return TextUtil.TEXT_EMPTY;
        }

        return type;
    }

    public static String getNetworkTypeCompat() {

        String type = getNetworkType();
        if (TextUtil.isEmpty(type)) {

            return "none";
        } else {

            return type;
        }
    }

    /**
     * @param context
     * @return false：表示未锁屏
     */
    public static boolean isLockScreen(Context context) {

        try {

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.inKeyguardRestrictedInputMode();

        } catch (Throwable t) {

            return false;
        }
    }

    /**
     * 比较app 当前版本
     *
     * @param newVersionName
     * @return true 版本号比当前应用版本号要高，false 则相反
     */
    public static boolean compareCurVersionNames(String newVersionName) {

        try {

            int res = 0;

            String[] curVersionName = AppInfoUtil.getVersionName().split("\\.");
            String[] newNumbers = newVersionName.split("\\.");

            // To avoid IndexOutOfBounds
            int maxIndex = Math.min(curVersionName.length, newNumbers.length);

            for (int i = 0; i < maxIndex; i++) {
                int curVersionPart = NumberUtil.parseInt(curVersionName[i], 0);
                int newVersionPart = NumberUtil.parseInt(newNumbers[i], 0);

                if (curVersionPart < newVersionPart) {
                    res = -1;
                    break;
                } else if (curVersionPart > newVersionPart) {
                    res = 1;
                    break;
                }
            }

            // If versions are the same so far, but they have different length...
            if (res == 0 && curVersionName.length != newNumbers.length) {
                res = (curVersionName.length > newNumbers.length) ? 1 : -1;
            }

            return res == -1;

        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 判断应用是否是在后台
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (TextUtils.equals(appProcess.processName, context.getPackageName())) {
                boolean isBackground = (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                return isBackground || isLockedState;
            }
        }
        return false;
    }

    /**
     * 检查设备是否有摄像头
     *
     * @return
     */
    public static boolean hasCamera() {

        try {

            PackageManager pm = ExApplication.getContext().getPackageManager();
            if (pm == null) {

                return false;
            } else {

                return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                        || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
            }

        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 判断是否有蓝牙适配器
     *
     * @return
     */
    public static boolean hasBluetoothAdapter() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }

    public static List<String> getInstalledAppList(Context context) {
        List<String> apps = new ArrayList<>();
        if (context == null) {
            return apps;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return apps;
        }

        List<PackageInfo> packages = packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA);
        if (CollectionUtil.isEmpty(packages)) {
            return apps;
        }
        for (PackageInfo info : packages) {
            if (info == null || TextUtils.isEmpty(info.packageName)) {
                continue;
            }
            apps.add(info.packageName);
        }
        return apps;
    }

    public static String getInstalledAppListStr(Context context) {

        StringBuilder sb = new StringBuilder();
        List<String> apps = getInstalledAppList(context);
        if (CollectionUtil.isEmpty(apps)) {
            return sb.toString();
        }

        for (String s : apps) {
            sb.append(s).append(",");
        }

        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public static String getMacAddress() {

        try {

            WifiManager wifi = (WifiManager) ExApplication.getContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            return TextUtil.filterNull(info.getMacAddress());

        } catch (Throwable t) {

        }

        return TextUtil.TEXT_EMPTY;
    }
}
