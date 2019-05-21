package com.androidex.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 获取 Mac 地址
 * <p>
 * 获取 Mac 地址比较困难，需要根据不同的系统版本使用不同的方法：
 * <p>
 * 1. Android 6.0(M) 以下
 * 2. Android 6.0(M)(包括) 以上, Android 7.0(N) 以下
 * 3. Android 7.0(N)(包括) 以上
 * <p>
 * Created by angelo. at 2018/08/16
 */
public final class WifiUtils {
    private static final String TAG = WifiUtils.class.getSimpleName();

    /**
     * Get mac address.
     *
     * @param context Context
     * @return String
     */
    public static String getMacAddress(Context context) {

        try{

            int currentSDK = Build.VERSION.SDK_INT;
            String macAddress;
            if (currentSDK < Build.VERSION_CODES.M) {
                macAddress = getMacAddressByWifiManager(context);
            } else if (currentSDK < Build.VERSION_CODES.N) {
                macAddress = getMacAddressByWlan0OrEth0File(context);
            } else {
                String ipMac = getMacAddressByIp();
                String scanNetInterfaces = getMachineHardwareAddress();
                if (!TextUtils.isEmpty(ipMac)) {
                    macAddress = ipMac;
                } else if (!TextUtils.isEmpty(scanNetInterfaces)) {
                    macAddress = scanNetInterfaces;
                } else {
                    macAddress = getMacAddressByBusybox();
                }
            }
            if (!TextUtils.isEmpty(macAddress))
                return macAddress;

        }catch (Exception e){

        }

        return "02:00:00:00:00:00";
    }


    /**
     * android 6.0及以上、7.0以下 获取mac地址
     *
     * @param context Context
     * @return String
     */
    private static String getMacAddressByWlan0OrEth0File(Context context) {
        // 如果是6.0以下，直接通过wifimanager获取
        String str = "";
        String macSerial = "";
        InputStreamReader ir = null;
        try {

            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {

        }finally {

            IOUtil.closeReader(ir);
        }

        //if wlan0 is null, read eth0 file.
        if (TextUtils.isEmpty(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return macSerial;
    }

    /**
     * 根据 wifi 信息获取本地 mac address
     * <p>
     * NOTE:
     * <p>
     * 1. Only used for Android 6.0 and below
     * 2. Need permissions:android.permission.ACCESS_WIFI_STATE
     *
     * @param context Context
     * @return String
     */
    @SuppressWarnings("ResourceType")
    private static String getMacAddressByWifiManager(Context context) {
        if (hasWifiAccessPermission(context)) {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;
            try {
                wifiInfo = wifiMgr.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } catch (Exception e) {
                //DO
            }
        }
        return "";
    }

    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context Context
     * @return boolean
     */
    public static boolean hasWifiAccessPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                "android.permission.ACCESS_WIFI_STATE")
                == PackageManager.PERMISSION_GRANTED;
    }

    private static String loadFileAsString(String fileName) throws Exception {

        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
            String text = loadReaderAsString(reader);
            return text;
        } catch (Exception e) {

        } finally {

            IOUtil.closeReader(reader);
        }

        return TextUtil.TEXT_EMPTY;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {

        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {

            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }


    /**
     * 根据IP地址获取MAC地址
     *
     * @return String Mac Address
     */
    private static String getMacAddressByIp() {
        String macAddress = null;
        try {
            // Get ip address
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    sb.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                sb.append(str.length() == 1 ? 0 + str : str);
            }
            macAddress = sb.toString().toUpperCase();
        } catch (Exception e) {
            //DO
        }
        return macAddress;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return InetAddress
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = netInterfaces.nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        break;
                    } else {
                        ip = null;
                    }
                }
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 获取本地IP
     *
     * @return String
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * android 7.0及以上，获取设备HardwareAddress地址
     * <p>
     * 方法二：扫描各个网络接口获取mac地址
     * <p>
     *
     * @return String
     */
    private static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }

    /***
     * byte to String
     *
     * @param bytes byte[]
     * @return String
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * android 7.0 and later，by HardwareAddress.
     * <p>
     * Way 3：by busybox file
     *
     * @return String
     */
    private static String getMacAddressByBusybox() {

        String result = "";
        String macAddress = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        if (TextUtils.isEmpty(result)) {
            return "network error";
        }

        // Parse the line.
        // Line data example：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr")) {
            macAddress = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            result = macAddress;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {

        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            br = new BufferedReader(is);
            while ((line = br.readLine()) != null && line.contains(filter)) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            
            IOUtil.closeReader(br);
        }
        return sb.toString();
    }


    /**
     * Get wifi SSIDList
     *
     * @param context Context
     * @return List<String>
     */
    @SuppressWarnings("ResourceType")
    public static List<String> getWifiList(Context context) {
        List<String> SSIDList = new ArrayList<>();
        if (hasWifiAccessPermission(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return SSIDList;
            }
            List<ScanResult> results = wifiManager.getScanResults();
            if (results != null) {
                for (ScanResult result : results) {
                    if(!SSIDList.contains(result.SSID)) {
                        SSIDList.add(result.SSID);
                    }
                }
            }
        }
        return SSIDList;
    }

    /**
     * Get wifi SSIDList
     *
     * @param context Context
     * @return String
     */
    @SuppressWarnings("ResourceType")
    public static String getWifiListStr(Context context) {

        StringBuilder sb = new StringBuilder();
        List<String> SSIDList = getWifiList(context);
        for(int i = 0; i < SSIDList.size(); i ++) {
            sb.append(SSIDList.get(i));
            if(i != SSIDList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
