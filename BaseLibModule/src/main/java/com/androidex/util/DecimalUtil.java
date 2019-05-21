package com.androidex.util;

import java.math.BigDecimal;

/**
 * 精确计算工具类
 * Created by yihaibin on 2017/10/16.
 */

public class DecimalUtil {

    private static final int DEF_DIV_SCALE = 2;

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String addStr(long v1, long v2){

        return addStr(Long.toString(v1), Long.toString(v2)).toString();
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String addStr(double v1, double v2) {

        return addStr(Double.toString(v1), Double.toString(v2));
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String addStr(String v1, String v2) {

        try {
            return add(new BigDecimal(v1), new BigDecimal(v2)).toString();
        } catch (Exception e) {
            return TextUtil.TEXT_EMPTY;
        }
    }


    //********************************减法***************************************


    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static String subStr(double v1, double v2) {

        return subStr(Double.toString(v1), Double.toString(v2));
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static String subStr(String v1, String v2) {

        try {
            return sub(new BigDecimal(v1), new BigDecimal(v2)).toString();
        } catch (Exception e) {
            return TextUtil.TEXT_EMPTY;
        }
    }


    //********************************乘法***************************************


    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mulStr(double v1, double v2) {

        return mulStr(Double.toString(v1), Double.toString(v2));
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mulStr(String v1, String v2) {

        try {
            return mul(new BigDecimal(v1), new BigDecimal(v2)).toString();
        } catch (Exception e) {
            return TextUtil.TEXT_EMPTY;
        }
    }


    //********************************除法***************************************


    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String divStr(double v1, double v2) {

        return divStr(Double.toString(v1), Double.toString(v2));
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String divStr(String v1, String v2) {

        try {
            return div(new BigDecimal(v1), new BigDecimal(v2)).toString();
        } catch (Exception e) {
            return TextUtil.TEXT_EMPTY;
        }
    }


    //******************************** base ***************************************


    /**
     * 提供精确的加法运算。
     *
     * @param b1 被加数
     * @param b2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {

        if(b1 == null || b2 == null)
            return null;
        else
            return b1.add(b2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param b1 被减数
     * @param b2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal b1, BigDecimal b2) {

        if(b1 == null || b2 == null)
            return null;
        else
            return b1.subtract(b2);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param b1 被乘数
     * @param b2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(BigDecimal b1, BigDecimal b2) {

        if(b1 == null || b2 == null)
            return null;
        else
            return b1.multiply(b2);
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param b1 被除数
     * @param b2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal b1, BigDecimal b2) {

        if(b1 == null || b2 == null)
            return null;
        else
            return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }






//暂时只提供字符串返回, 该函数先屏蔽
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(double v1, double v2) {
//
//        return div(Double.toString(v1), Double.toString(v2));
//    }
//
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(String v1, String v2) {
//
//        return div(new BigDecimal(v1), new BigDecimal(v2)).doubleValue();
//    }

//暂时只提供字符串返回, 该函数先屏蔽
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(double v1, double v2) {
//
//        return mul(Double.toString(v1), Double.toString(v2));
//    }
//
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(String v1, String v2) {
//
//        return mul(new BigDecimal(v1), new BigDecimal(v2)).doubleValue();
//    }

//暂时只提供字符串返回, 该函数先屏蔽
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(double v1, double v2) {
//
//        return add(Double.toString(v1), Double.toString(v2));
//    }
//
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(String v1, String v2) {
//
//        return add(new BigDecimal(v1), new BigDecimal(v2)).doubleValue();
//    }

//暂时只提供字符串返回, 该函数先屏蔽
//    /**
//     * 提供精确的减法运算。
//     *
//     * @param v1 被减数
//     * @param v2 减数
//     * @return 两个参数的差
//     */
//    public static double sub(double v1, double v2) {
//
//        return sub(Double.toString(v1), Double.toString(v2));
//    }
//
//    /**
//     * 提供精确的减法运算。
//     *
//     * @param v1 被减数
//     * @param v2 减数
//     * @return 两个参数的差
//     */
//    public static double sub(String v1, String v2) {
//
//        return sub(new BigDecimal(v1), new BigDecimal(v2)).doubleValue();
//    }


    //    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(double v1, double v2) {
//
//        return div(v1, v2, DEF_DIV_SCALE);
//    }
//
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(double v1, double v2) {
//
//        return div(v1, v2, DEF_DIV_SCALE);
//    }
//
//
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(float v1, float v2) {
//
//        return mul((double) v1, (double) v2);
//    }
//
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(float v1, float v2) {
//
//        return mul((double) v1, (double) v2);
//    }
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static String subStr(String v1, String v2) {
//
//        BigDecimal b1 = new BigDecimal(v1);
//        BigDecimal b2 = new BigDecimal(v2);
//        return b1.subtract(b2).toString();
//    }
//
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(String v1, String v2) {
//
//        BigDecimal b1 = new BigDecimal(v1);
//        BigDecimal b2 = new BigDecimal(v2);
//        return b1.add(b2).doubleValue();
//    }
//
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(float v1, float v2) {
//
//        return add((double) v1, (double) v2);
//    }
//
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(double v1, double v2) {
//
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.add(b2).doubleValue();
//    }
//
//    /**
//     * 提供精确的加法运算。
//     *
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double sub(String v1, String v2) {
//
//        BigDecimal b1 = new BigDecimal(v1);
//        BigDecimal b2 = new BigDecimal(v2);
//        return b1.subtract(b2).doubleValue();
//    }
//
//    /**
//     * 提供精确的减法运算。
//     *
//     * @param v1 被减数
//     * @param v2 减数
//     * @return 两个参数的差
//     */
//    public static double sub(float v1, float v2) {
//
//        return sub((double) v1, (double) v2);
//    }
//
//    /**
//     * 提供精确的减法运算。
//     *
//     * @param v1 被减数
//     * @param v2 减数
//     * @return 两个参数的差
//     */
//    public static double sub(double v1, double v2) {
//
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.subtract(b2).doubleValue();
//    }
//
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(float v1, float v2) {
//
//        return mul((double) v1, (double) v2);
//    }
//
//    /**
//     * 提供精确的乘法运算。
//     *
//     * @param v1 被乘数
//     * @param v2 乘数
//     * @return 两个参数的积
//     */
//    public static double mul(double v1, double v2) {
//
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.multiply(b2).doubleValue();
//    }
//
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(float v1, float v2) {
//
//        return div((double) v1, (double) v2, DEF_DIV_SCALE);
//    }
//
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
//     * 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1 被除数
//     * @param v2 除数
//     * @return 两个参数的商
//     */
//    public static double div(double v1, double v2) {
//
//        return div(v1, v2, DEF_DIV_SCALE);
//    }
//
//    /**
//     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
//     * 定精度，以后的数字四舍五入。
//     *
//     * @param v1    被除数
//     * @param v2    除数
//     * @param scale 表示表示需要精确到小数点以后几位。
//     * @return 两个参数的商
//     */
//    public static double div(double v1, double v2, int scale) {
//
//        if (scale < 0)
//            throw new IllegalArgumentException("The scale must be a positive integer or zero");
//
//
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//
//        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_EVEN).doubleValue();
//    }
}
