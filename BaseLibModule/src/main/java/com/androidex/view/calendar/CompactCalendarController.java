package com.androidex.view.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.OverScroller;

import com.androidex.R;
import com.androidex.util.DensityUtil;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class CompactCalendarController {

    private int mRowCounts = 8;//绘制行数

    private int paddingWidth = 40;
    private int paddingHeight = 40;
    private Paint dayPaint = new Paint();
    private Rect rect;
    private int textHeight;
    private int textWidth;
    private static final int DAYS_IN_WEEK = 7;
    private int widthPerDay;
    private String[] dayColumnNames;
    private float distanceX;
    private PointF accumulatedScrollOffset = new PointF();
    private OverScroller scroller;
    private int monthsScrolledSoFar;
    private Date currentDate = new Date();
    private Locale locale = Locale.CHINA;
    private Calendar currentCalender = Calendar.getInstance(locale);
    private Calendar todayCalender = Calendar.getInstance(locale);
    private Calendar calendarWithFirstDayOfMonth = Calendar.getInstance(locale);
    private Calendar eventsCalendar = Calendar.getInstance(locale);
    private Direction currentDirection = Direction.NONE;
    private int heightPerDay;
    private int currentDayBackgroundColor;
    private int calendarTextTitleColor;
    private int calendarWeekTitleColor;
    private int calenderTextDateColor;
    private int currentSelectedDayBackgroundColor;
    private int calenderBackgroundColor = Color.WHITE;
    private int calenderCurrentDayTextColor = Color.WHITE;

    private Typeface mTypeface;
    private boolean mDayFormartPreZero;//格式化日期不足2位是前面补零

    private int textTitleSize = 13;
    private int textWeekTitleSize = 14;
    private int textDaySize = 12;

    private int width;
    private int height;
    private int paddingRight;
    private int paddingLeft;
    private boolean shouldDrawYearHeader = true;
    private Map<String, List<CalendarDayEvent>> events = new HashMap<>();
    private boolean showSmallIndicator;
    private float smallIndicatorRadius;

    private String[] monthsShort = {"Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"};

    public void setDayFormartPreZero() {

        mDayFormartPreZero = true;
    }



    private enum Direction {
        NONE, HORIZONTAL, VERTICAL;
    }

    CompactCalendarController(Paint dayPaint, OverScroller scroller, Rect rect, AttributeSet attrs,
                              Context context, int currentDayBackgroundColor, int calenderTextColor, int currentSelectedDayBackgroundColor) {
        this.dayPaint = dayPaint;
        this.scroller = scroller;
        this.rect = rect;
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.calenderTextDateColor = calenderTextColor;
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
        loadAttributes(attrs, context);
        init(context);
    }

    public Typeface getTypeface() {

        return mTypeface;
    }

    public void setTypeface(Typeface mTypeface) {

        this.mTypeface = mTypeface;
        dayPaint.setTypeface(mTypeface);

    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CompactCalendarView, 0, 0);
            try {
                currentDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
                calendarTextTitleColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextTitleColor, calendarTextTitleColor);
                calendarWeekTitleColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarWeekTitleColor, calendarWeekTitleColor);
                calenderTextDateColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextDateColor, calenderTextDateColor);
                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor);
                calenderBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarBackgroundColor, calenderBackgroundColor);

                calenderCurrentDayTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayTextColor, calenderBackgroundColor);

                textTitleSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTitleTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textTitleSize, context.getResources().getDisplayMetrics()));

                textWeekTitleSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarWeekTitleTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textWeekTitleSize, context.getResources().getDisplayMetrics()));

                textDaySize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarDayTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textDaySize, context.getResources().getDisplayMetrics()));
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(Context context) {
        setUseWeekDayAbbreviation(false);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(mTypeface);
        dayPaint.setTextSize(textDaySize);
        dayPaint.setColor(calenderTextDateColor);
        dayPaint.getTextBounds("31", 0, "31".length(), rect);
        textHeight = rect.height() * 3;
        textWidth = rect.width() * 2;

        todayCalender.setTime(currentDate);
        setToMidnight(todayCalender);

        currentCalender.setTime(currentDate);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);

        eventsCalendar.setFirstDayOfWeek(Calendar.SUNDAY);

        float screenDensity = 1;
        if (context != null) {
            screenDensity = context.getResources().getDisplayMetrics().density;
        }

        //scale small indicator by screen density
        smallIndicatorRadius = DensityUtil.dip2px(2);
    }

    /**
     * 设置指定时间戳的月份和当日
     * @param millis
     */
    public void setCurrentCalendar(long millis){

        currentDate = new Date(millis);
        todayCalender.setTime(currentDate);
        setToMidnight(todayCalender);

        calendarWithFirstDayOfMonth.setTimeInMillis(millis);

        currentCalender.setTime(currentDate);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);

        eventsCalendar.setTimeInMillis(millis);
        eventsCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
    }


    private void setCalenderToFirstDayOfMonth(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        setMonthOffset(calendarWithFirstDayOfMonth, currentDate, scrollOffset, monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
    }

    private void setMonthOffset(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        calendarWithFirstDayOfMonth.setTime(currentDate);
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, scrollOffset + monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MINUTE, 0);
        calendarWithFirstDayOfMonth.set(Calendar.SECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MILLISECOND, 0);
    }

    void showNextMonth() {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.getTime(), 0, 1);
        setCurrentDate(calendarWithFirstDayOfMonth.getTime());
    }

    void showPreviousMonth() {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.getTime(), 0, -1);
        setCurrentDate(calendarWithFirstDayOfMonth.getTime());
    }

    public void setRowCounts(int rowCounts) {

        this.mRowCounts = rowCounts;
    }

    void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null");
        }
        this.locale = locale;
    }

    void setUseWeekDayAbbreviation(boolean useThreeLetterAbbreviation) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        if (dayNames == null) {
            throw new IllegalStateException("Unable to determine weekday names from default locale");
        }
        if (dayNames.length != 8) {
            throw new IllegalStateException("Expected weekday names from default locale to be of size 7 but: "
                    + Arrays.toString(dayNames) + " with size " + dayNames.length + " was returned.");
        }
        if (useThreeLetterAbbreviation) {
            this.dayColumnNames = new String[]{dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7], dayNames[1]};
        } else {
            this.dayColumnNames = new String[]{dayNames[2].substring(0, 1), dayNames[3].substring(0, 1),
                    dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1), dayNames[1].substring(0, 1)};
        }
    }

    void setDayColumnNames(String[] dayColumnNames) {
        if (dayColumnNames == null || dayColumnNames.length != 7) {
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }

    void setShouldDrawYearHeader(boolean shouldDrawYearHeader) {

        this.shouldDrawYearHeader = shouldDrawYearHeader;
    }


    void showSmallIndicator(boolean showSmallIndicator) {
        this.showSmallIndicator = showSmallIndicator;
    }

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
        widthPerDay = (width) / DAYS_IN_WEEK;
        heightPerDay = height / mRowCounts;
        this.width = width;
        this.height = height;
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;
    }

    void onDraw(Canvas canvas) {
        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        calculateXPositionOffset();

        drawCalenderBackground(canvas);

        drawScrollableCalender(canvas);
    }

    boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentDirection == Direction.HORIZONTAL) {
                monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
                float remainingScrollAfterFingerLifted = (accumulatedScrollOffset.x - monthsScrolledSoFar * width);
                scroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) -remainingScrollAfterFingerLifted, 0);
                currentDirection = Direction.NONE;
                setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);

                if (calendarWithFirstDayOfMonth.get(Calendar.MONTH) != currentCalender.get(Calendar.MONTH)) {
                    setCalenderToFirstDayOfMonth(currentCalender, currentDate, -monthsScrolledSoFar, 0);
                }

                return true;
            }
            currentDirection = Direction.NONE;
        }
        return false;
    }

    int getHeightPerDay() {
        return heightPerDay;
    }

    int getWeekNumberForCurrentMonth() {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentDate);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -monthsScrolledSoFar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setToMidnight(calendar);
        return calendar.getTime();
    }

    void setCurrentDate(Date dateTimeMonth) {
        currentDate = new Date(dateTimeMonth.getTime());
        currentCalender.setTime(currentDate);
        setToMidnight(currentCalender);
        monthsScrolledSoFar = 0;
        accumulatedScrollOffset.x = 0;
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    void addEvent(CalendarDayEvent event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<CalendarDayEvent> uniqCalendarDayEvents = events.get(key);
        if (uniqCalendarDayEvents == null) {
            uniqCalendarDayEvents = new ArrayList<>();
        }
        if (!uniqCalendarDayEvents.contains(event)) {
            uniqCalendarDayEvents.add(event);
        }
        events.put(key, uniqCalendarDayEvents);
    }

    void addEvents(List<CalendarDayEvent> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            addEvent(events.get(i));
        }
    }

    void removeEvent(CalendarDayEvent event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<CalendarDayEvent> uniqCalendarDayEvents = events.get(key);
        if (uniqCalendarDayEvents != null) {
            uniqCalendarDayEvents.remove(event);
        }
    }

    void removeEvents(List<CalendarDayEvent> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            removeEvent(events.get(i));
        }
    }

    public void clearEvent() {

        if(events != null && !events.isEmpty())
            events.clear();
    }

    List<CalendarDayEvent> getEvents(Date date) {
        eventsCalendar.setTimeInMillis(date.getTime());
        String key = getKeyForCalendarEvent(eventsCalendar);

        if (events != null) {
            List<CalendarDayEvent> uniqEvents = events.get(key);
            return uniqEvents;
        } else {
            return new ArrayList<>();
        }
    }

    //E.g. 4 2016 becomes 2016_4
    private String getKeyForCalendarEvent(Calendar cal) {
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }

    Date onSingleTapConfirmed(MotionEvent e) {
        monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
        int dayColumn = Math.round((paddingLeft + e.getX() - paddingWidth - paddingRight) / widthPerDay);
        int dayRow = Math.round((e.getY() - paddingHeight) / heightPerDay);

        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);

        //Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
        int firstDayOfMonth = calendarWithFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1;
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

        if (dayOfMonth < calendarWithFirstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                && dayOfMonth >= 0) {
            calendarWithFirstDayOfMonth.add(Calendar.DATE, dayOfMonth);

            currentCalender.setTimeInMillis(calendarWithFirstDayOfMonth.getTimeInMillis());
            return currentCalender.getTime();
        } else {
            return null;
        }
    }

    boolean onDown(MotionEvent e) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (currentDirection == Direction.NONE) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                currentDirection = Direction.HORIZONTAL;
            } else {
                currentDirection = Direction.VERTICAL;
            }
        }

        this.distanceX = distanceX;
        return true;
    }

    boolean computeScroll() {
        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.getCurrX();
            return true;
        }
        return false;
    }

    private void drawScrollableCalender(Canvas canvas) {
        monthsScrolledSoFar = (int) (accumulatedScrollOffset.x / width);


        drawPreviousMonth(canvas);

        drawCurrentMonth(canvas);

        drawNextMonth(canvas);
    }

    private void drawNextMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar + 1)));
    }

    private void drawCurrentMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar);
    }

    private void drawPreviousMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, -1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void calculateXPositionOffset() {
        if (currentDirection == Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX;
        }
    }

    private void drawCalenderBackground(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextDateColor);
    }

    void drawEvents(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        List<CalendarDayEvent> uniqCalendarDayEvents =
                events.get(getKeyForCalendarEvent(currentMonthToDrawCalender));

        boolean shouldDrawCurrentDayCircle = currentMonthToDrawCalender.get(Calendar.MONTH) == todayCalender.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);

        if (uniqCalendarDayEvents != null) {

            for (int i = 0; i < uniqCalendarDayEvents.size(); i++) {
                CalendarDayEvent event = uniqCalendarDayEvents.get(i);
                long timeMillis = event.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = eventsCalendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek = dayOfWeek <= 0 ? 7 : dayOfWeek;
                dayOfWeek = dayOfWeek - 1;

                int temp = shouldDrawYearHeader ? 2 : 1;
                int weekNumberForMonth = eventsCalendar.get(Calendar.WEEK_OF_MONTH) - temp;

                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
                float yPosition = weekNumberForMonth * heightPerDay + paddingHeight + heightPerDay;

                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = (todayDayOfMonth == dayOfMonth && shouldDrawCurrentDayCircle);
                if (!isSameDayAsCurrentDay) {
                    if (showSmallIndicator) {
                        //draw small indicators below the day in the calendar
                        drawSmallIndicatorCircle(canvas, xPosition, yPosition + DensityUtil.dip2px(8), event.getColor());
                    } else {
                        drawCircle(canvas, xPosition, yPosition, event.getColor());
                    }
                }

                Bitmap bitmap = event.getBitmap();
                if(bitmap != null)
                    canvas.drawBitmap(bitmap, xPosition - bitmap.getWidth() / 2, yPosition + DensityUtil.dip2px(6), dayPaint);
            }
        }
    }

    void drawMonth(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        drawEvents(canvas, currentMonthToDrawCalender, offset);

        //offset by one because we want to start from Monday
//        int firstDayOfMonth = currentMonthToDrawCalender.get(Calendar.DAY_OF_WEEK) - 1;
        int firstDayOfMonth = currentMonthToDrawCalender.get(Calendar.DAY_OF_WEEK);
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        //offset by one because of 0 index based calculations
        firstDayOfMonth = firstDayOfMonth - 1;
        boolean isSameMonth = currentMonthToDrawCalender.get(Calendar.MONTH) == todayCalender.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);

        for (int dayColumn = 0, dayRow = 0; dayColumn <= 7; dayRow++) {
            if (dayRow == 8) {
                dayRow = 0;
                if (dayColumn <= 7) {
                    dayColumn++;
                }
            }
            if (dayColumn == dayColumnNames.length) {
                break;
            }
            float xPosition = widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
            if (dayRow == 0) {

                if (shouldDrawYearHeader) {

                    dayPaint.setColor(calendarTextTitleColor);
                    dayPaint.setTextSize(textTitleSize);
                    canvas.drawText(monthsShort[currentCalender.get(Calendar.MONTH)] + "." + currentCalender.get(Calendar.YEAR),
                            (paddingWidth + paddingLeft + accumulatedScrollOffset.x + DensityUtil.dip2px(10f) + offset - paddingRight), paddingHeight, dayPaint);
                    dayPaint.setColor(calenderTextDateColor);

                    dayRow ++;

                    drawWeekTitle(canvas, dayColumn ,dayColumnNames[dayColumn], dayRow, xPosition);

                } else {

                    drawWeekTitle(canvas, dayColumn ,dayColumnNames[dayColumn], dayRow, xPosition);
                }

            } else {

                int starRow = shouldDrawYearHeader ? 2 : 1;
                int day = ((dayRow - starRow) * 7 + dayColumn + 1) - firstDayOfMonth;
                float yPosition = dayRow * heightPerDay + paddingHeight;

                dayPaint.setTextSize(textDaySize);

                if (isSameMonth && todayDayOfMonth == day) {

                    drawCircle(canvas, xPosition, yPosition, currentDayBackgroundColor);

                } else if (currentCalender.get(Calendar.DAY_OF_MONTH) == day) {

                    drawCircle(canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                }

                if (day <= currentMonthToDrawCalender.getActualMaximum(Calendar.DAY_OF_MONTH) && day > 0) {

                    if(isSameMonth && day < todayDayOfMonth){

                        dayPaint.setColor(0xFFbfbfbf);////  2017/4/14 过去时间字体颜色

                        if (mDayFormartPreZero)
                            canvas.drawText(String.format("%02d", day), xPosition, yPosition, dayPaint);
                        else
                            canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);

                        dayPaint.setColor(calenderTextDateColor);

                    } else if (isSameMonth && todayDayOfMonth == day) {

                        dayPaint.setColor(calenderCurrentDayTextColor);

                        if (mDayFormartPreZero)
                            canvas.drawText(String.format("%02d", day), xPosition, yPosition, dayPaint);
                        else
                            canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);

                        dayPaint.setColor(calenderTextDateColor);

                    } else {

                        if (mDayFormartPreZero)
                            canvas.drawText(String.format("%02d", day), xPosition, yPosition, dayPaint);
                        else
                            canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                    }
                }
            }
        }
    }

    private void drawWeekTitle(Canvas canvas, int dayColumn ,String dayColumnName, int dayRow, float xPosition) {

        if(dayColumn == 0 || dayColumn == 6)////  2017/4/14 待整理周六日不同样式
            dayPaint.setColor(0xFFbfbfbf);
        else
            dayPaint.setColor(calendarWeekTitleColor);

        dayPaint.setTextSize(textWeekTitleSize);
        canvas.drawText(dayColumnName, xPosition, dayRow * heightPerDay + paddingHeight, dayPaint);
        dayPaint.setColor(calenderTextDateColor);
    }

    // Draw Circle on certain days to highlight them
    private void drawCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        float radius = (float) (0.5 * Math.sqrt(widthPerDay * widthPerDay + heightPerDay * heightPerDay));
        // add some padding to height
        drawCircle(canvas, radius / 2, x, y - (textHeight / 6));
    }

    private void drawSmallIndicatorCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        drawCircle(canvas, smallIndicatorRadius, x, y);
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        if (radius >= 34) {
            radius = 34;
        }
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextDateColor);
    }

}
