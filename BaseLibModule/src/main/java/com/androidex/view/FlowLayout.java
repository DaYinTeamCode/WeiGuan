package com.androidex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局，自动换行
 */
public class FlowLayout extends ViewGroup {

    //private static final String TAG = "FlowLayout";
    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();

    private int mCurrentLineNum;
    private int mLineCountLimit;

    private FlowLineNumberListener mLineNumberListener;

    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    public FlowLayout(Context context) {

        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {

        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {

        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void setLineCountLimit(int lineCountLimit) {

        this.mLineCountLimit = lineCountLimit;
    }

    public void setLineNumberListener(FlowLineNumberListener lineNumberListener) {
        this.mLineNumberListener = lineNumberListener;
    }

    public int getCurrentLineNum() {
        return mCurrentLineNum;
    }

    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;

        //记录每一行的宽度，width不断取最大宽度
        int lineWidth = 0;

        //每一行的高度，累加至height
        int lineHeight = 0;

        //子view count数
        int count = getChildCount();

        //折行数统计
        int wrapLineCount = 0;

        // 遍历每个子元素
        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (i == 0)//记录第一行高
                height = childHeight;

            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {

                width = Math.max(lineWidth, childWidth);// 取最大的

                lineWidth = childWidth; // 重新开启新行，开始记录

                if (i != 0)//折行数, 第一个元素如果超出了行宽，也不会发生折行
                    wrapLineCount += 1;

                //检测是否到了限定行数
                if (mLineCountLimit != 0 && wrapLineCount >= mLineCountLimit) {

                    width = Math.max(width, lineWidth);
                    break;
                }

                // 叠加当前高度，
                height += lineHeight;

                // 开启记录下一行的item高度 用于下一行相关数据调试对比，取最高那项做为行高
                lineHeight = childHeight;

            } else {

                // 否则累加值lineWidth,lineHeight取最大高度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int cCount = getChildCount();

        //折行数统计
        int wrapLineCount = 0;

        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {

            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {

                // 记录这一行所有的View以及最大高度
                mLineHeight.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineHeight = 0;
                lineViews = new ArrayList<View>();

                if (i != 0)//折行数, 第一个元素如果超出了行宽，也不会发生折行
                    wrapLineCount += 1;

                //检测是否超出行
                if (mLineCountLimit != 0 && wrapLineCount >= mLineCountLimit) {
                    break;
                }
            }

            // 如果不需要换行，则累加
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }

        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        mCurrentLineNum = lineNums;

        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);

            //Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
            //Log.e(TAG, "第" + i + "行， ：" + lineHeight);

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {

                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();

                //code by yhb
                if (rc > getWidth()) {

                    rc = getWidth() - lp.rightMargin;
                    //child.getLayoutParams().width = lc - rc;
                    //child.setLayoutParams(child.getLayoutParams());
                }
                //code by yhb

                int bc = tc + child.getMeasuredHeight();

                //Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="+ rc + " , b = " + bc);

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
            }

            left = 0;
            top += lineHeight;
        }

        if (lineNums > mLineCountLimit && null != mLineNumberListener) {

            mLineNumberListener.onFlowLineNumberOutOfRage();
        }
    }

    public void clearLineNums() {
        mCurrentLineNum = 0;
    }

    public interface FlowLineNumberListener {

        void onFlowLineNumberOutOfRage();
    }
}
