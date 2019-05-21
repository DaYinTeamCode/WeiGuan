/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidex.view.pager.indicator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.util.DensityUtil;

/**
 * 修改过
 * 原名：PagerSlidingTabStrip
 *
 * @author yhb, Daisw
 */
@SuppressLint("NewApi")
public class TabTipStripIndicator extends HorizontalScrollView {

    public interface IconTabProvider {

        int getPageIconResId(int position);
    }

    public interface TipTabProvider {

        int getPageTipResId(int position);
    }

    // @formatter:off
    //#update
    // private static final int[] ATTRS = new int[] {
    // android.R.attr.textSize,
    // android.R.attr.textColor
    // };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x00000000;
    private int dividerColor = 0x00000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = false;

    private int scrollOffset = 0;//115;//82*2-41;//82;//52;
    private int indicatorHeight = 8;//8//下划线高度
    private int indicatorMarginBottom = 4;//指示器底部间距
    private int underlineHeight = 0;//2//下划线滑动区域背景高度
    private int dividerPadding = 12;
    private int tabPadding = 0;//每个tab的左右间距
    private int mUnderLineHoriPadding;
    private int dividerWidth = 1;//1

    private Typeface mTypeface;
    private int tabTextSize = 36;
    private boolean tabTextBold = false;
    private int colorTabTextDefault, colorTabTextSelected;

//	private Typeface tabTypeface = null;
//	private int tabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = 0;

    private int tabBackgroundResId = 0;//R.drawable.background_tab;

    private boolean isTabTextSingleLine = true;

    private OnTabItemClickListener mOnTabItemClickLisn;

    //private Locale locale;//#update


    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public TabTipStripIndicator(Context context) {
        this(context, null);
    }

    public TabTipStripIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTipStripIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

//		DisplayMetrics dm = getContext().getApplicationContext().getResources().getDisplayMetrics();
//
//		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
//		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
//		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
//		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
//		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
//		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
//
//		// get system attrs (android:textSize and android:textColor)
//
//		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
//
//		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
//		tabTextColor = a.getColor(1, tabTextColor);
//
//		a.recycle();
//
//		// get custom attrs
//
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.stripPagerIndicator);
        indicatorColor = a.getColor(R.styleable.stripPagerIndicator_spiIndicatorColor, 0);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiIndicatorHeight, 0);
        indicatorMarginBottom = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiindicatorMarginBottom, 0);
        tabPadding = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabPaddingLeftRight, tabPadding);
        mUnderLineHoriPadding = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabUnderLinePadding, mUnderLineHoriPadding);
        colorTabTextDefault = a.getColor(R.styleable.stripPagerIndicator_colorTabTextDefault, 0);
        colorTabTextSelected = a.getColor(R.styleable.stripPagerIndicator_colorTabTextSelected, 0);
        tabTextSize = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabTextSize, 42);
        dividerWidth = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiDividerWidth, 0);
        tabTextBold = a.getBoolean(R.styleable.stripPagerIndicator_spiTabTextBold, false);
        tabBackgroundResId = a.getResourceId(R.styleable.stripPagerIndicator_spiTabBackground, 0);

        underlineColor = a.getColor(R.styleable.stripPagerIndicator_spiUnderlineColor, 0);
        dividerColor = a.getColor(R.styleable.stripPagerIndicator_spiDividerColor, dividerColor);
        underlineHeight = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiDividerPadding, dividerPadding);
        shouldExpand = a.getBoolean(R.styleable.stripPagerIndicator_spiShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.stripPagerIndicator_spiTextAllCaps, textAllCaps);
        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        //#update
        //if (locale == null) {
        //locale = getResources().getConfiguration().locale;
        //}
    }

    public void setOnTabItemClickListener(OnTabItemClickListener lisn) {

        mOnTabItemClickLisn = lisn;
    }

    public void setTabTextSingleLine(boolean tabTextSingleLine) {

        isTabTextSingleLine = tabTextSingleLine;
    }

    public void setViewPager(ViewPager pager) {

        this.pager = pager;

        if (pager.getAdapter() == null)
            throw new IllegalStateException("ViewPager does not have adapter instance.");

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {

        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();
        tabCount = pager.getAdapter().getCount();
        currentPosition = pager.getCurrentItem();

        PagerAdapter adapter = pager.getAdapter();
        for (int i = 0; i < tabCount; i++) {

            if (adapter instanceof IconTabProvider) {

                if (((IconTabProvider) adapter).getPageIconResId(i) == 0) {

                    addTextTab(i, adapter.getPageTitle(i), 0);//d

                } else {

                    addIconTab(i, ((IconTabProvider) adapter).getPageIconResId(i));
                }

            } else if (adapter instanceof TipTabProvider) {

                addTextTab(i, adapter.getPageTitle(i), ((TipTabProvider) adapter).getPageTipResId(i));

            } else {

                addTextTab(i, adapter.getPageTitle(i), 0);
            }
        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });
    }

    public void invalidateTabText() {

        tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {

            View childView = tabsContainer.getChildAt(i);
            if (childView instanceof TextView)
                ((TextView) childView).setText(pager.getAdapter().getPageTitle(i));
        }

    }

    public void invalidateTabTextColor() {

        tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {

            View childView = tabsContainer.getChildAt(i);
            if (childView.getTag() instanceof TextView) {
                ((TextView) childView.getTag()).setTextColor(colorTabTextDefault);

                if (currentPosition == i) {

                    ((TextView) childView.getTag()).setTextColor(colorTabTextSelected);
                }
            }
        }

    }

    private void addTextTab(int position, CharSequence title, int tipResId) {

        //tab容器
        FrameLayout tab = new FrameLayout(getContext());

        //文本
        TextView tv = new TextView(getContext());
        tv.setPadding(tabPadding, 0, tabPadding, 0);
        tv.setText(title);
        tv.setTextColor(colorTabTextDefault);
        tv.setGravity(Gravity.CENTER);

        if (isTabTextSingleLine)
            tv.setSingleLine();

        //添加文本
        tab.setTag(tv);
        LayoutParams fllp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        tab.addView(tv, fllp);

        //提示icon
        if (tipResId != 0) {

            View tip = new View(getContext());
            tip.setBackgroundResource(tipResId);

            fllp = new LayoutParams(DensityUtil.dip2px(21), DensityUtil.dip2px(10), Gravity.RIGHT);
            fllp.topMargin = DensityUtil.dip2px(1);
            tab.addView(tip, fllp);
        }

        addTab(position, tab);
        if (currentPosition == position) {

            setTag(tv);
            tv.setTextColor(colorTabTextSelected);
        }
    }

    private void addIconTab(final int position, int resId) {

        ImageView tab = new ImageView(getContext());
        tab.setImageResource(resId);
        tab.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        tab.setPadding(tabPadding, 0, tabPadding, 0);

        addTab(position, tab);
        if (currentPosition == position) {

            setTag(tab);
            tab.setSelected(true);
        }
    }

    private void addTab(final int position, View tab) {

        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnTabItemClickLisn != null)
                    mOnTabItemClickLisn.onTabItemClick(v, position);

                pager.setCurrentItem(position);
            }
        });

        //tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        View v = null;
        TextView tvTab = null;
        for (int i = 0; i < tabCount; i++) {

            v = tabsContainer.getChildAt(i);

            if (tabBackgroundResId != 0)
                v.setBackgroundResource(tabBackgroundResId);

            if (v.getTag() instanceof TextView) {

                tvTab = (TextView) v.getTag();
                tvTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                if (mTypeface != null)
                    tvTab.setTypeface(mTypeface);//#update

                if (tabTextBold)
                    tvTab.getPaint().setFakeBoldText(true);
                else
                    tvTab.getPaint().setFakeBoldText(false);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                        tvTab.setAllCaps(true);
                    } else {

                        //#update
                        //tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }
    }

    public void clearTabTip(int position) {

        if (position < 0 || position >= tabsContainer.getChildCount())
            return;

        View v = tabsContainer.getChildAt(position);
        if (v instanceof FrameLayout) {

            FrameLayout fl = (FrameLayout) v;
            v = fl.getChildAt(1);
            if (v != null)
                fl.removeViewAt(1);
        }
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        View view = tabsContainer.getChildAt(position);

        int newScrollX = view.getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        int tempScrollOffset = getWidth() / 2 - currentTab.getWidth() / 2;
        if (tempScrollOffset > scrollOffset)
            scrollOffset = getWidth() / 2 - currentTab.getWidth() / 2;

        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

//            lineLeft = getAccelrate(lineLeft, nextTabLeft);
//            lineRight = getAccelerateDec(lineRight, nextTabRight);

            lineLeft = getLinearFloat(lineLeft, nextTabLeft);
            lineRight = getLinearFloat(lineRight, nextTabRight);
        }

        canvas.drawRect(lineLeft + mUnderLineHoriPadding, height - indicatorHeight - indicatorMarginBottom,
                lineRight - mUnderLineHoriPadding, height - indicatorMarginBottom, rectPaint);

        // draw underline

        rectPaint.setColor(underlineColor);
        if (underlineHeight > 0)
            canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider

        if (dividerWidth > 0) {

            dividerPaint.setColor(dividerColor);
            dividerPaint.setStrokeWidth(dividerWidth);
            for (int i = 0; i < tabCount - 1; i++) {

                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
            }
        }
    }

    private float getAccelerateDec(float lineRight, float nextTabRight) {

        AccelerateDecelerateInterpolator decelerateInterpolator = new AccelerateDecelerateInterpolator();
        float temp = decelerateInterpolator.getInterpolation(currentPositionOffset);
        lineRight = temp * (nextTabRight - lineRight) + lineRight;
        return lineRight;
    }

    private float getAccelrate(float lineLeft, float nextTabLeft) {

        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(2);
        float temp = accelerateInterpolator.getInterpolation(currentPositionOffset);
        lineLeft = temp * (nextTabLeft - lineLeft) + lineLeft;
        return lineLeft;
    }

    private float getLinearFloat(float lineLeft, float nextTabLeft) {

        return currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft;
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;
            if (tabsContainer.getChildCount() > 0) {

                //修复umeng bug:空指针
                View v = tabsContainer.getChildAt(position);
                scrollToChild(position, (int) (positionOffset * (v == null ? 1 : v.getWidth())));
                invalidate();
            }

            if (delegatePageListener != null)
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (state == ViewPager.SCROLL_STATE_IDLE)
                scrollToChild(pager.getCurrentItem(), 0);

            if (delegatePageListener != null)
                delegatePageListener.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageSelected(int position) {

            View childView = tabsContainer.getChildAt(position);
            if (childView == null)
                return;

            if (childView.getTag() instanceof TextView) {

                Object obj = getTag();
                TextView tv = null;
                if (obj instanceof TextView) {

                    tv = (TextView) obj;
                    if (tv != null)
                        startFadeOutAnim(tv);
                }

                tv = (TextView) childView.getTag();
                if (tv != null) {

                    setTag(tv);
                    startFadeInAnim(tv);
                }

            } else if (childView instanceof ImageView) {

                Object obj = getTag();
                ImageView view = null;
                if (obj instanceof ImageView) {

                    view = (ImageView) obj;
                    if (view != null)
                        view.setSelected(false);

                } else if (obj instanceof TextView) {

                    TextView textView = (TextView) obj;
                    if (textView != null) {

                        setTag(textView);
                        startFadeOutAnim(textView);
                    }
                }

                view = (ImageView) childView;
                if (view != null) {

                    setTag(view);
                    view.setSelected(true);
                }
            }

            if (delegatePageListener != null)
                delegatePageListener.onPageSelected(position);
        }
    }

    private void startFadeInAnim(TextView selectedTab) {

        if (selectedTab != null) {

            ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, "textColor", colorTabTextDefault, colorTabTextSelected);
            colorAnim.setTarget(selectedTab);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(200);
            colorAnim.start();
        }
    }

    private void startFadeOutAnim(TextView selectedTab) {

        if (selectedTab != null) {

            ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, "textColor", colorTabTextSelected, colorTabTextDefault);
            colorAnim.setTarget(selectedTab);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setDuration(200);
            colorAnim.start();
        }
    }

    public void setIndicatorColor(int indicatorColor) {

        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {

        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {

        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {

        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {

        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {

        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {

        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {

        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {

        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerWidth(int widthPx) {

        this.dividerWidth = widthPx;
        invalidate();
    }

    public void setColorTabTextDefault(int colorTabTextDefault) {

        this.colorTabTextDefault = colorTabTextDefault;
        if (tabCount > 0)
            invalidateTabTextColor();
    }

    public void setColorTabTextSelected(int colorTabTextSelected) {

        this.colorTabTextSelected = colorTabTextSelected;
        if (tabCount > 0)
            invalidateTabTextColor();
    }

    public void setDividerColorResource(int resId) {

        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {

        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {

        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public void setUnderlineHoriPadding(int margin) {

        mUnderLineHoriPadding = margin;
    }

    public int getUnderlineHeight() {

        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {

        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {

        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {

        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {

        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {

        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {

        return shouldExpand;
    }

    public boolean isTextAllCaps() {

        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {

        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {

        this.tabTextSize = textSizePx;
        if (tabCount > 0)
            updateTabStyles();
    }

    public int getTextSize() {

        return tabTextSize;
    }

    public void setTextTypeface(Typeface typeface) {

        //this.tabTypeface = typeface;//#update
        //this.tabTypefaceStyle = style;//#update
        mTypeface = typeface;
        if (tabCount > 0)
            updateTabStyles();
    }

    public void setTextBold(boolean bold) {

        tabTextBold = bold;
        if (tabCount > 0)
            updateTabStyles();
    }

    public void setTabBackground(int resId) {

        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {

        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {

        this.tabPadding = paddingPx;
        if (tabCount > 0)
            updateTabStyles();
    }

    public int getTabPaddingLeftRight() {

        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {

        int currentPosition;

        public SavedState(Parcelable superState) {

            super(superState);
        }

        private SavedState(Parcel in) {

            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {

                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {

                return new SavedState[size];
            }
        };
    }

    public interface OnTabItemClickListener {

        void onTabItemClick(View view, int position);
    }
}
