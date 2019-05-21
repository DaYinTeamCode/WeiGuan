package com.androidex.view.calendar;

import android.graphics.Bitmap;

public class CalendarDayEvent {

    private long timeInMillis;
    private int color;
    private Bitmap mBitmap;

    public CalendarDayEvent(final long timeInMillis, final int color) {

        this.timeInMillis = timeInMillis;
        this.color = color;
    }

    public CalendarDayEvent(final long timeInMillis, Bitmap bitmap) {

        this.timeInMillis = timeInMillis;
        this.mBitmap = bitmap;
    }

    public long getTimeInMillis() {

        return timeInMillis;
    }

    public int getColor() {

        return color;
    }

    public Bitmap getBitmap() {

        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {

        this.mBitmap = bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDayEvent event = (CalendarDayEvent) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + color;
        return result;
    }

    @Override
    public String
    toString() {
        return "CalendarDayEvent{" +
                "timeInMillis=" + timeInMillis +
                ", color=" + color +
                '}';
    }
}
