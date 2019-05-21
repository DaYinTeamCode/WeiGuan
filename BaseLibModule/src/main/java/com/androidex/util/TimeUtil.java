package com.androidex.util;

import android.text.SpannableStringBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 */
public class TimeUtil {

    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
    public static final long ONE_DAY_SEC = 24 * 60 * 60;

    /**
     * 格式化时间戳
     *
     * @param sdf
     * @param millis
     * @return
     */
    public static String format(SimpleDateFormat sdf, long millis) {

        return sdf.format(millis);
    }

    /**
     * 格式化时间戳
     *
     * @param format
     * @param millis
     * @return
     */
    public static String format(String format, long millis) {

        return new SimpleDateFormat(format).format(millis);
    }

    /**
     * 返回中文日期，不带年
     *
     * @param millis
     * @return 例如：10月8日
     */
    public static String formatCnMMDD(long millis) {

        return new SimpleDateFormat("MM月dd日").format(millis);
    }

    /**
     * 返回中文日前，分秒
     *
     * @param millis
     * @return
     */
    public static String formatMSColonmm(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return format.format(millis);
    }

    /**
     * 返回24小时制时间，格式为：18:16
     *
     * @return
     */
    public static String formatHHColonmm(long millis) {

        return new SimpleDateFormat("HH:mm").format(millis);
    }

    public static String formatYYMMDDHHmm(long millis) {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(millis);
    }

    public static String formatYYMMDDHH(long millis) {

        return new SimpleDateFormat("yyyy-MM-dd HH").format(millis);
    }

    /**
     * 返回yymmdd格式的日期字符串
     */
    public static String formatYYMMDD(long millis) {

        return new SimpleDateFormat("yyyy-MM-dd").format(millis);
    }

    /**
     * @param second 秒
     * @return
     */
    public static String formatYYMMDDBySec(long second) {

        return formatYYMMDD(second * 1000);
    }

    /**
     * 格式化为时分秒 00：00
     *
     * @param millis
     * @return
     */
    public static String formatHHMM(long millis) {

        return new SimpleDateFormat("HH:mm").format(millis);
    }

    /**
     * 格式化为分秒 00：00
     *
     * @param millis
     * @return
     */
    public static String formatMMSS(long millis) {

        return new SimpleDateFormat("mm:ss").format(millis);
    }

    /**
     * 格式化 年-月-日 时:分:秒
     *
     * @param millis
     * @return
     */

    public static String formatNormal(long millis) {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(millis);
    }

    public static String formatNormalChinaType(long mills) {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(mills);
    }

    public static String formatNormalChinaType2(long mills) {
        return new SimpleDateFormat("MM月dd号 HH:mm").format(mills);
    }

    public static String formatNormalTime(long mills) {
        return new SimpleDateFormat("yyyy.MM.dd").format(mills);
    }

    public static String formatNormalType(long mills) {
        return new SimpleDateFormat("yyyy年MM月dd日").format(mills);
    }

    /**
     * 返回yyyy格式的年份
     */
    public static String formatYYYY(long millis) {

        return new SimpleDateFormat("yyyy").format(millis);
    }

    /**
     * 返回yyyy格式的年份
     */
    public static String formatNormalHomeCouponStyle(long millis) {
        millis = millis * 1000;
        if (isSameDay(new Date(System.currentTimeMillis()), new Date(millis))) {
            return new SimpleDateFormat("HH:mm").format(millis);
        } else {
            return new SimpleDateFormat("HH:mm  MM月dd日").format(millis);
        }

    }

    /**
     * 比较两个时间是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(date1);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(date2);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameDayByLongTime(long time1, long time2) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(new Date(time1));
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(new Date(time2));

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算两个时间差值
     *
     * @return
     */
    public static String getCaculateTimeLeft(long startTime, long endTime) {

        if (LogMgr.isDebug())
            LogMgr.d("time left --- " + TimeUtil.formatNormal(endTime * 1000) + "   -   " + TimeUtil.formatNormal(startTime * 1000));

        return getCaculateTimeLeft(endTime - startTime);
    }

    public static String getCaculateTimeLeft(long timeLeft) {

        String resultTime = TextUtil.TEXT_EMPTY;

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        if (timeLeft / dayTime != 0) {

            resultTime = String.format("%s天%s时%s分%s秒", timeLeft / dayTime, (timeLeft % dayTime) / hourTime,
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if ((timeLeft % dayTime) / hourTime != 0) {

            resultTime = String.format("%s时%s分%s秒", (timeLeft % dayTime) / hourTime,
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if (((timeLeft % dayTime) % hourTime) / 60 != 0) {

            resultTime = String.format("%s分%s秒",
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if (timeLeft % 60 != 0) {

            resultTime = String.format("%s秒", timeLeft % 60);
        }

        if (LogMgr.isDebug())
            LogMgr.d("time left : " + resultTime);

        return resultTime;
    }

    public static String getCaculateTimeLeftWithSpace(long timeLeft) {

        String resultTime = TextUtil.TEXT_EMPTY;

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        if (timeLeft / dayTime != 0) {

            resultTime = String.format("%s天 %s时 %s分 %s秒", timeLeft / dayTime, (timeLeft % dayTime) / hourTime,
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if ((timeLeft % dayTime) / hourTime != 0) {

            resultTime = String.format("%s时 %s分 %s秒", (timeLeft % dayTime) / hourTime,
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if (((timeLeft % dayTime) % hourTime) / 60 != 0) {

            resultTime = String.format("%s分 %s秒",
                    ((timeLeft % dayTime) % hourTime) / 60, timeLeft % 60);

        } else if (timeLeft % 60 != 0) {

            resultTime = String.format("%s秒", timeLeft % 60);
        }

        if (LogMgr.isDebug())
            LogMgr.d("time left : " + resultTime);

        return resultTime;
    }

    public static SpannableStringBuilder getCaculateTimeLeft(long timeLeft, int textSizeDp, int numberColor, int unitColor) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        if (timeLeft / dayTime != 0) {

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(timeLeft / dayTime), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("天", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf((timeLeft % dayTime) / hourTime), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("时", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(((timeLeft % dayTime) % hourTime) / 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("分", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(timeLeft % 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("秒", textSizeDp, unitColor));

        } else if ((timeLeft % dayTime) / hourTime != 0) {

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf((timeLeft % dayTime) / hourTime), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("时", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(((timeLeft % dayTime) % hourTime) / 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("分", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(timeLeft % 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("秒", textSizeDp, unitColor));

        } else if (((timeLeft % dayTime) % hourTime) / 60 != 0) {

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(((timeLeft % dayTime) % hourTime) / 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("分", textSizeDp, unitColor));

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(timeLeft % 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("秒", textSizeDp, unitColor));

        } else if (timeLeft % 60 != 0) {

            ssb.append(TextSpanUtil.getSpannableText(String.valueOf(timeLeft % 60), textSizeDp, numberColor));
            ssb.append(TextSpanUtil.getSpannableText("秒", textSizeDp, unitColor));
        }

        if (LogMgr.isDebug())
            LogMgr.d("time left : " + ssb);

        return ssb;
    }


    public static String getCaculateTime(long timeLeft) {

        String resultTime = TextUtil.TEXT_EMPTY;

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        if (timeLeft / dayTime != 0) {

            resultTime = String.format("%s天", timeLeft / dayTime);

        } else if ((timeLeft % dayTime) / hourTime != 0) {

            resultTime = String.format("%s时", (timeLeft % dayTime) / hourTime);

        } else if (((timeLeft % dayTime) % hourTime) / 60 != 0) {

            resultTime = String.format("%s分", ((timeLeft % dayTime) % hourTime) / 60);

        } else if (timeLeft % 60 != 0) {

            resultTime = String.format("%s秒", timeLeft % 60);
        }

        if (LogMgr.isDebug())
            LogMgr.d("time left : " + resultTime);

        return resultTime;
    }

    /**
     * 计算两个时间差值
     *
     * @return
     */
    public static String getCaculateTime(long startTime, long endTime) {

        if (LogMgr.isDebug())
            LogMgr.d("time left --- " + TimeUtil.formatNormal(endTime * 1000) + "   -   " + TimeUtil.formatNormal(startTime * 1000));
        return getDayandHour(endTime - startTime);
    }

    /**
     * 计算 剩余 天，小时 等
     *
     * @param timeLeft
     * @return
     */
    public static String getDayandHour(long timeLeft) {
        int dayTime = 60 * 60 * 24;
        int value = (int) (timeLeft / dayTime);
        int hourTime = 60 * 60;
        int valueHour = (int) (timeLeft / hourTime);
        if (value > 99) {
            return "99+天后过期";
        } else if (value >= 1) {
            return String.format("%s天后过期", value);
        } else if (valueHour >= 1 && valueHour < 24) {
            return String.format("%s小时后过期", valueHour);
        } else {
            return "马上过期";
        }
    }

    /**
     * 判断优惠券是否即将过期
     *
     * @param timeLeft
     * @return
     */
    public static boolean isCouponExpire(long timeLeft) {
        int dayTime = 60 * 60 * 24;
        int value = (int) (timeLeft / dayTime);
        if (value < 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算剩余天数
     *
     * @param timeLeft
     * @return
     */
    public static String getDay(long timeLeft) {

        int dayTime = 60 * 60 * 24;
        int value = (int) (timeLeft / dayTime);
        return String.valueOf(value);
    }

    /**
     * 计算剩余小时
     *
     * @param timeLeft
     * @return
     */
    public static String getHour(long timeLeft) {

        int hourTime = 60 * 60;
        int value = (int) (timeLeft / hourTime);
        return String.format("%02d", value);
    }

    /**
     * @param timeLeft
     * @return
     */
    public static String getHourWithoutDay(long timeLeft) {

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        int value = (int) ((timeLeft % dayTime) / hourTime);
        return String.format("%02d", value);
    }

    /**
     * 计算剩余分
     *
     * @param timeLeft
     * @return
     */
    public static String getMin(long timeLeft) {

        int dayTime = 24 * 60 * 60;
        int hourTime = 60 * 60;

        int value = (int) (((timeLeft % dayTime) % hourTime) / 60);
        return String.format("%02d", value);
    }

    /**
     * 计算剩余秒
     *
     * @param timeLeft
     * @return
     */
    public static String getSecond(long timeLeft) {

        int value = (int) (timeLeft % 60);
        return String.format("%02d", value);
    }

    /**
     * 格式化为月-日
     *
     * @return 1-12
     */
    public static String formatMd(long millis) {

        return new SimpleDateFormat("MM-dd").format(millis);
    }


    /**
     * 根据指定的月份返回英文缩写
     *
     * @param month 1-12
     * @return
     */
    public static String getShortMonthNameEn(int month) {

        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * 获取该时间戳天数
     *
     * @param timeMillis
     * @return
     */
    public static long getTimeMillisDays(long timeMillis) {

        return timeMillis / (1000 * 60 * 60 * 24);
    }

    //******************************** calendar体系 *******************************

    /**
     * 返回 HH:mm 格式时间
     *
     * @param calendar
     * @return
     */
    public static String formatHHmm(Calendar calendar) {

        if (calendar == null)
            return TextUtil.TEXT_EMPTY;

        return timeAddZeroIfNeed(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + timeAddZeroIfNeed(calendar.get(Calendar.MINUTE));
    }

    /**
     * 返回 MM月dd日 格式 日期
     *
     * @param calendar
     * @return
     */
    public static String formatMMdd(Calendar calendar) {

        if (calendar == null)
            return TextUtil.TEXT_EMPTY;

        return (calendar.get(Calendar.MONTH) + 1) + "月" + NumberUtil.addZeroLessThan10(calendar.get(Calendar.DATE)) + "日";
    }

    /**
     * 返回 星期几 格式 文本
     *
     * @param calendar
     * @return
     */
    public static String formatWeekday(Calendar calendar) {

        if (calendar == null)
            return TextUtil.TEXT_EMPTY;

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return TextUtil.TEXT_EMPTY;
        }
    }

    /**
     * 检测两个日期是否为同一年
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean checkSameYear(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null)
            return false;

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * 检查两个日期是否为同一月
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean checkSameMonth(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null)
            return false;

        return checkSameYear(cal1, cal2) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * 检测是否为同一周
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean checkSameWeek(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null)
            return false;

        return checkSameMonth(cal1, cal2) && cal1.get(Calendar.WEEK_OF_MONTH) == cal2.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 检测是否为同一天
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean checkSameDate(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null)
            return false;

        return checkSameMonth(cal1, cal2) && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
    }

    /**
     * 比较两个日期是否是昨天关系
     *
     * @param today
     * @param yesterday
     * @return
     */
    public static boolean checkYesterday(Calendar today, Calendar yesterday) {

        if (today == null || yesterday == null)
            return false;

        if (checkSameYear(today, yesterday)) {

            //同年
            if (checkSameMonth(today, yesterday)) {

                //同月
                return today.get(Calendar.DATE) - 1 == yesterday.get(Calendar.DATE);
            } else {

                //不同月
                if (today.get(Calendar.MONTH) - 1 == yesterday.get(Calendar.MONTH)) {

                    //相差一月
                    return today.get(Calendar.DATE) == today.getActualMinimum(Calendar.DATE) &&
                            yesterday.get(Calendar.DATE) == yesterday.getActualMaximum(Calendar.DATE);
                } else {

                    return false;
                }
            }

        } else {

            //不同年
            if (today.get(Calendar.YEAR) - 1 == yesterday.get(Calendar.YEAR)) {

                return today.get(Calendar.MONTH) == today.getActualMinimum(Calendar.MONTH) &&
                        today.get(Calendar.DATE) == today.getActualMinimum(Calendar.DATE) &&
                        yesterday.get(Calendar.MONTH) == yesterday.getActualMaximum(Calendar.MONTH) &&
                        yesterday.get(Calendar.DATE) == yesterday.getActualMaximum(Calendar.DATE);
            } else {

                return false;
            }
        }
    }

    /**
     * 获取当天时间日期 秒为单位
     */
    public static long getCurDayInSecond() {

        return getCurDayInSecond(Calendar.getInstance());
    }

    private static long getCurDayInSecond(Calendar calendar) {

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 获取当天时间日期 毫秒为单位
     */
    public static long getCurDayInMillis() {

        return getCurDayInMillis(Calendar.getInstance());
    }

    private static long getCurDayInMillis(Calendar calendar) {

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 获取当天几点时间戳
     *
     * @return
     */
    public static long getCurDayHourInMillis(int hourTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, hourTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    private static String timeAddZeroIfNeed(int num) {

        return num >= 0 && num < 10 ? "0" + num : String.valueOf(num);
    }

    public static boolean isSameYear(long millis1, long millis2) {

        Calendar calDateA = Calendar.getInstance();
        calDateA.setTimeInMillis(millis1);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTimeInMillis(millis2);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR);
    }

    /**
     * 获取当前时间 精度为秒
     *
     * @return
     */
    public static long getCurTimeInSecond() {

        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取两个时间天数差 （按自然日）
     *
     * @param millis1
     * @param millis2
     * @return
     */
    public static int getBetweenDays(long millis1, long millis2) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis1);
        long dayInMillis1 = getCurDayInMillis(calendar);

        calendar.setTimeInMillis(millis2);
        long dayInMillis2 = getCurDayInMillis(calendar);

        return (int) ((dayInMillis1 - dayInMillis2) / ONE_DAY_MILLIS);
    }

    /**
     * 检测时间戳是否是当天
     *
     * @param time
     * @return
     */
    public static boolean isToday(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long minTimeInMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long maxTimeInMillis = calendar.getTimeInMillis();

        return (time >= minTimeInMillis && time < maxTimeInMillis);
    }

    public static boolean isTomorrow(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long minTimeInMillis = calendar.getTimeInMillis() + ONE_DAY_MILLIS;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long maxTimeInMillis = calendar.getTimeInMillis() + ONE_DAY_MILLIS;

        return (time >= minTimeInMillis && time < maxTimeInMillis);
    }

    public static boolean isIn24Hours(long nowTime, long lastTime) {

        return ((nowTime - lastTime) <= ONE_DAY_MILLIS);
    }

    /**
     * 格式化年月日，eg：2017年9月
     * @param calendar
     * @return
     */
    public static String formatYYYYM(Calendar calendar){

        if(calendar == null)
            return TextUtil.TEXT_EMPTY;
        else
            return String.format("%s年%s月", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * 返回 MM月dd日 格式 日期 数值类型
     * @param calendar
     * @return
     */
    public static int formatMMddInteger(Calendar calendar){

        if(calendar == null)
            return -1;

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return (year * 100) + month;
    }

    /**
     * 格式化年月日，eg：2017年9月
     * @param calendar
     * @return
     */
    public static String formatYYYYMD(Calendar calendar){

        if(calendar == null)
            return TextUtil.TEXT_EMPTY;
        else
            return String.format("%s年%s月%s日", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }
}
