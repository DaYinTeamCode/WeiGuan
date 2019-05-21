package com.androidex.textspan;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * 自定义字体的 text span
 * Created by yihaibin on 16/5/24.
 */
public class FontTypefaceSpan extends MetricAffectingSpan {

    private Typeface mTf;

    public FontTypefaceSpan(Typeface tf){

        mTf = tf;
    }

    @Override
    public void updateMeasureState(TextPaint tp) {

        apply(tp, mTf);
    }

    @Override
    public void updateDrawState(TextPaint tp) {

        apply(tp, mTf);
    }

    private void apply(TextPaint tp, Typeface tf){

        if(tp != null)
            tp.setTypeface(tf);
    }
}
