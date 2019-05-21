package com.androidex.util;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.androidex.textspan.FontTypefaceSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本Spannable工具类
 */
public class TextSpanUtil {

    public static SpannableString getSpannableText(String text, int textSizeDp) {

        return getSpannableText(text, textSizeDp, -1, false, null, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue) {

        return getSpannableText(text, textSizeDp, textColorValue, false, null, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, Typeface tf) {

        return getSpannableText(text, textSizeDp, -1, false, tf, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue, Typeface tf) {

        return getSpannableText(text, textSizeDp, textColorValue, false, tf, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue, ClickableSpan clickSpan) {

        return getSpannableText(text, textSizeDp, textColorValue, false, null, clickSpan);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue, boolean isBold) {

        return getSpannableText(text, textSizeDp, textColorValue, isBold, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue, boolean isBold, Typeface tf) {

        return getSpannableText(text, textSizeDp, textColorValue, isBold, tf, null);
    }

    public static SpannableString getSpannableText(String text, int textSizeDp, int textColorValue, boolean isBold, Typeface tf, ClickableSpan clickSpan) {

        text = TextUtil.filterNull(text);
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new AbsoluteSizeSpan(textSizeDp, true), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (textColorValue != -1)
            ss.setSpan(new ForegroundColorSpan(textColorValue), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (isBold)
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (tf != null)
            ss.setSpan(new FontTypefaceSpan(tf), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (clickSpan != null)
            ss.setSpan(clickSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    public static SpannableString setImageSpan(TextView textView, String content, int imageResource, int start, int end) {

        SpannableString ss = new SpannableString(content);
        ss.setSpan(new ImageSpan(textView.getContext(), imageResource), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString setPostFreeImageSpan(TextView textView, String content, int imageResource) {

        return setImageSpan(textView, "#postfree " + content, imageResource, 0, 9);
    }

    public static SpannableString setJDImageSpan(TextView textView, String content, int imageResource) {

        return setImageSpan(textView, "#post " + content, imageResource, 0, 5);
    }

    public static SpannableString setBrandImageSpan(TextView textView, String content, int imageResource) {

        return setImageSpan(textView, "#postfree" + content, imageResource, 0, 9);
    }

    /**
     * 设置文本高亮
     *
     * @param fullText
     * @param hightLightText
     * @param highLightColor
     * @param ignoreUpperLower
     * @return
     */
    public static SpannableString getHighLightSpannable(String fullText, String hightLightText, int highLightColor, boolean ignoreUpperLower) {

        if (TextUtil.isEmpty(fullText))
            return new SpannableString(TextUtil.TEXT_EMPTY);

        if (TextUtil.isEmpty(hightLightText) || hightLightText.length() > fullText.length())
            return new SpannableString(fullText);

        if (ignoreUpperLower) {

            fullText = fullText.toLowerCase();
            hightLightText = hightLightText.toLowerCase();
        }

        SpannableString spannableString = new SpannableString(fullText);
        Pattern pattern = Pattern.compile(hightLightText, Pattern.LITERAL);//启用模式的字面值解析,避免()[]{}被当成正则符号使用
        Matcher matcher = pattern.matcher(fullText);
        while (matcher.find()) {

            spannableString.setSpan(new ForegroundColorSpan(highLightColor), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }


    public static SpannableStringBuilder getFormatNumber(String msg, int numberSize, int numberColor, Typeface typeface) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        if (TextUtil.isEmpty(msg))
            return ssb;

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(msg);

        int start = 0;
        while (matcher.find()) {

            String sPre = msg.substring(start, matcher.start());
            start = matcher.end();
            ssb.append(sPre);

            ssb.append(TextSpanUtil.getSpannableText(matcher.group(), numberSize, numberColor, false, typeface));
        }

        if (start == 0)
            return ssb.append(msg);
        else
            ssb.append(msg.substring(start, msg.length()));

        return ssb;
    }
}
