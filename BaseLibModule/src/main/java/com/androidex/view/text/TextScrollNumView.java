package com.androidex.view.text;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androidex.util.TextUtil;

/**
 * Created by yihaibin on 2017/4/14.
 */
public class TextScrollNumView extends AppCompatTextView
        implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private OnScrollNumListener mLisn;
    private ValueAnimator mScrollAnim;
    private int mCurrentNum;
    private int mEndNum;

    public TextScrollNumView(Context context) {

        super(context);
    }

    public TextScrollNumView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public TextScrollNumView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollNumListener(OnScrollNumListener lisn){

        mLisn = lisn;
    }

    public void setTextNum(int number, boolean needAnim){

        if(mScrollAnim != null && mScrollAnim.isRunning())
            mScrollAnim.cancel();

        if(needAnim){

            mEndNum = number;
            mScrollAnim = new ValueAnimator();
            mScrollAnim.setIntValues(mCurrentNum, number);
            mScrollAnim.setDuration(500);
            mScrollAnim.addUpdateListener(this);
            mScrollAnim.addListener(this);
            mScrollAnim.start();
        }else{

            mCurrentNum = number;
            mEndNum = number;
            setNumText(number);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        mCurrentNum = (int) animation.getAnimatedValue();
        setNumText(mCurrentNum);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

        mScrollAnim = null;
        setNumText(mEndNum);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

        mScrollAnim = null;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private void setNumText(int number) {

        CharSequence cs = null;
        if(mLisn != null)
            cs = mLisn.onScrollNumTextSet(number);

        if(TextUtil.isEmpty(cs))
            cs = String.valueOf(number);

        setText(cs);
    }

    public interface OnScrollNumListener {

        CharSequence onScrollNumTextSet(int num);
    }

    //格式化数字
//    private DecimalFormat mFormat;

//    public void setTextNum(float number, boolean needAnim, DecimalFormat format) {
//
//        if(format != null) {
//
//            mFormat = format;
//            number = NumberUtil.parseFloat(mFormat.format(number), 0);
//        }
//
//        if (mScrollAnim != null && mScrollAnim.isRunning())
//            mScrollAnim.cancel();
//
//        if (needAnim) {
//
//            mScrollAnim = new ValueAnimator();
//            mScrollAnim.setFloatValues(mCurrentNum, number);
//            mScrollAnim.setDuration(500);
//            mScrollAnim.addUpdateListener(this);
//            mScrollAnim.addListener(this);
//            mScrollAnim.start();
//
//        } else {
//
//            setText(String.valueOf(number));
//        }
//    }
//
//    @Override
//    public void onAnimationUpdate(ValueAnimator animation) {
//
//        mCurrentNum = (float) animation.getAnimatedValue();
//        if (mFormat != null) {
//
//            mCurrentNum = NumberUtil.parseFloat(mFormat.format(mCurrentNum), 0);
//            setText(String.valueOf(mCurrentNum));
//
//        } else {
//
//            setText(String.valueOf((int) mCurrentNum));
//        }
//    }
}
