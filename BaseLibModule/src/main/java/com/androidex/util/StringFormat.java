package com.androidex.util;

/**
 * 格式化字符串
 * <p/>
 * User: pzwwei
 * Date: 16/3/25
 * Time: 下午4:44
 */
public class StringFormat {

    /**
     * 格式化数字 超出一万用w 表示保留1位小数点 小于一万保持原数据
     *
     * @param count
     * @return
     */
    public static String formatNumToW(int count) {

        if (count < 10000)
            return String.valueOf(count);
        else
            return String.format("%.1f", ((float) count) / 10000) + "w";
    }

    public static String formatNumToW(String count) {

        return formatNumToW(NumberUtil.parseInt(count, 0));
    }

    /**
     * 格式化数字 超出一万用k 表示保留1位小数点 小于一万保持原数据
     *
     * @param count
     * @return
     */
    public static String formatNumToK(int count) {

        if (count < 10000)
            return String.valueOf(count);
        else
            return String.format("%.1f", ((float) count) / 1000) + "k";
    }

    public static String formatNumToK(String count) {

        return formatNumToK(NumberUtil.parseInt(count, 0));
    }

    public static String format(String format, Object... args) {

        try {
            return String.format(format, args);
        } catch (Exception e) {
            //do nothing
        }
        return TextUtil.TEXT_EMPTY;
    }
}
