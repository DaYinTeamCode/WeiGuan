package com.androidex.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Phone information
 * <p>
 * Created by angelo at 2018/08/17
 */
public final class PhoneInfoUtils {
    private static String TAG = PhoneInfoUtils.class.getSimpleName();

    private static final String SIM_STATE = "getSimState";
    private static final String SIM_LINE_NUMBER = "getLine1Number";

    private static TelephonyManager getTelephonyManager(Context context) {
        if (context != null) {
            return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return null;
    }

    @SuppressWarnings("ResourceType")
    public static String getICCID(Context context) {
        if (hasReadPhoneStatePermission(context)) {
            TelephonyManager telephonyManager = getTelephonyManager(context);
            if (telephonyManager != null) {
                return telephonyManager.getSimSerialNumber();
            }
        }
        return null;
    }

    /**
     * Get phone number
     *
     * @return String
     */
    @SuppressWarnings("ResourceType")
    public static String getPhoneNumber(Context context) {
        if (hasReadPhoneStatePermission(context)) {
            TelephonyManager telephonyManager = getTelephonyManager(context);
            if (telephonyManager != null) {
                return telephonyManager.getLine1Number();
            }
        }
        return null;
    }

    public static boolean hasReadPhoneStatePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasReadPrivilegedPhoneStatePermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                "android.permission.READ_PRIVILEGED_PHONE_STATE")
                == PackageManager.PERMISSION_GRANTED;
    }

    //获取手机服务商信息

    /**
     * Get service agent info
     *
     * @return String
     */
    public static String getProvidersName(Context context) {
        String providersName = "";
        TelephonyManager telephonyManager = getTelephonyManager(context);
        if (telephonyManager == null) {
            return providersName;
        }
        String networkOperator = telephonyManager.getNetworkOperator();

        //IMSI号前面3位460是国家，
        // 紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信
        if ("46000".equals(networkOperator) || "46002".equals(networkOperator)) {
            providersName = "中国移动";//中国移动
        } else if ("46001".equals(networkOperator)) {
            providersName = "中国联通";//中国联通
        } else if ("46003".equals(networkOperator)) {
            providersName = "中国电信";//中国电信
        }
        return providersName;
    }

    public static String getPhoneNumbers(Context context) {
        StringBuilder sb = new StringBuilder();
        String sim1 = getSimPhoneNumber(context, 0);
        String sim2 = getSimPhoneNumber(context, 1);
        if (!TextUtils.isEmpty(sim1)) {
            sb.append(sim1);
        }
        if (!TextUtils.isEmpty(sim2)) {

            if(!TextUtils.isEmpty(sim1))
                sb.append(",");

            sb.append(sim2);
        }
        return sb.toString();
    }

    public static String getSimPhoneNumber(Context context, int slotIdx) {

        try{

            if (!hasReadPhoneStatePermission(context) && !hasReadPrivilegedPhoneStatePermission(context)) {
                return TextUtil.TEXT_EMPTY;
            }
            if (getSimStateBySlotIdx(context, slotIdx)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                    return TextUtil.TEXT_EMPTY;
                }

                String phone = (String) getSimByMethod(context, SIM_LINE_NUMBER,
                        getSubidBySlotId(context, slotIdx));
                return TextUtil.filterNull(phone);
            }

        }catch (Exception e){

        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * @param context
     * @param slotIdx:0(sim1),1(sim2)
     * @return
     */
    public static boolean getSimStateBySlotIdx(Context context, int slotIdx) {
        boolean isReady = false;
        Object getSimState = getSimByMethod(context, SIM_STATE, slotIdx);
        if (getSimState != null) {
            int simState = Integer.parseInt(getSimState.toString());
            if ((simState != TelephonyManager.SIM_STATE_ABSENT)
                    && (simState != TelephonyManager.SIM_STATE_UNKNOWN)) {
                isReady = true;
            }
        }
        return isReady;
    }

    public static Object getSimByMethod(Context context, String method, int param) {
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimState = telephonyClass.getMethod(method, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = param;
            Object ob_phone = getSimState.invoke(telephony, obParameter);

            if (ob_phone != null) {
                return ob_phone;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param context
     * @param slotId
     * @return int
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static int getSubidBySlotId(Context context, int slotId) {
        SubscriptionManager subscriptionManager = (SubscriptionManager)
                context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(subscriptionManager.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimState = telephonyClass.getMethod("getSubId", parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotId;
            Object ob_phone = getSimState.invoke(subscriptionManager, obParameter);

            if (ob_phone != null) {
                return ((int[]) ob_phone)[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
