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
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;

/**
 * 修改过
 * 原名：PagerSlidingTabStrip
 *
 * @author yhb, Daisw
 */
@SuppressLint("NewApi")
public class TabStripIndicator extends HorizontalScrollView {

    public interface IconTabProvider {

        int getPageIconResId(int position);
    }

    public interface IconUrlTabProvider {

        String getPageIconSelectedUrl(int position);

        String getPageIconUrl(int position);
    }

    public interface IconTabImageViewProvider {

        IconTabImageView getTabImageView(Context context, int viewWidth, int viewHeight);
    }

    public interface IconTabImageView {

        void setImageUrl(String url, String selectedUrl);

        void setSelected(boolean isSelected);

        View getView();
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
    private boolean mIndicatorEnable = true;
    private int indicatorHeight = 8;//8//下划线高度
    private boolean indicatorRoundRect;
    private int indicatorFixWidth;
    private int indicatorMarginBottom = 4;//指示器底部间距


    private int underlineHeight = 0;//2//下划线滑动区域背景高度
    private int dividerPadding = 12;
    private int tabPadding = 0;//每个tab的左右间距
    private boolean tabTextSelectScale = true;  //选中时文本放大

    private int mUnderLineHoriPadding;
    private int dividerWidth = 0;//1

    private Typeface mTypeface;
    private int tabTextSize = 36;
    private int tabTextLineSpace = 4;
    private boolean tabTextBold = false;
    private boolean tabTextSelectedBold = false;
    private int colorTabTextDefault, colorTabTextSelected;
    private Drawable colorTabTextBgDefault, colorTabTextBgSelected;

//	private Typeface tabTypeface = null;
//	private int tabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = 0;

    private int tabBackgroundResId = 0;//R.drawable.background_tab;

    private boolean isTabTextSingleLine = true;

    private OnTabItemClickListener mOnTabClickLisn;

    private Bitmap mIndicatorBitmap;//低部划块图片

    private boolean mIconUrlUseText;
    private int mIconUrlWidth;
    private int mIconUrlHeight;

    private boolean needAnim = true;        //滑动时是否开启字体动画

    private IconTabImageViewProvider mIconTabImageViewProvider;

//    private boolean mHeightIsMatchParent = true;    //设置高是否为Match_parent

    public TabStripIndicator(Context context) {
        this(context, null);
    }

    public TabStripIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabStripIndicator(Context context, AttributeSet attrs, int defStyle) {
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
        indicatorRoundRect = a.getBoolean(R.styleable.stripPagerIndicator_spiindicatorRoundRect, false);
        indicatorFixWidth = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiindicatorFixWidth, 0);
        indicatorMarginBottom = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiindicatorMarginBottom, 0);
        tabPadding = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabPaddingLeftRight, tabPadding);
        mUnderLineHoriPadding = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabUnderLinePadding, mUnderLineHoriPadding);
        colorTabTextDefault = a.getColor(R.styleable.stripPagerIndicator_colorTabTextDefault, 0);
        colorTabTextSelected = a.getColor(R.styleable.stripPagerIndicator_colorTabTextSelected, 0);

        colorTabTextBgDefault = a.getDrawable(R.styleable.stripPagerIndicator_colorTabTextBgDefault);
        colorTabTextBgSelected = a.getDrawable(R.styleable.stripPagerIndicator_colorTabTextBgSelected);
//        mHeightIsMatchParent = a.getBoolean(R.styleable.stripPagerIndicator_spiIndicatorHeightMatchParent,true);

        tabTextSize = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabTextSize, 42);
        tabTextLineSpace = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiTabTextLineSpacing, 4);
        dividerWidth = a.getDimensionPixelSize(R.styleable.stripPagerIndicator_spiDividerWidth, 0);
        tabTextBold = a.getBoolean(R.styleable.stripPagerIndicator_spiTabTextBold, false);
        tabBackgroundResId = a.getResourceId(R.styleable.stripPagerIndicator_spiTabBackground, 0);
        tabTextSelectScale = a.getBoolean(R.styleable.stripPagerIndicator_spiTabTextSelectScale, true);
        tabTextSelectedBold = a.getBoolean(R.styleable.stripPagerIndicator_spiTabTextSelectedBold, false);
        needAnim = a.getBoolean(R.styleable.stripPagerIndicator_spiTabTextAnim, true);

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
        expandedTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);

        //#update
        //if (locale == null) {
        //locale = getResources().getConfiguration().locale;
        //}
    }

    public void setOnTabItemClickListener(OnTabItemClickListener lisn) {

        mOnTabClickLisn = lisn;
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

                addIconTab(i, ((IconTabProvider) adapter).getPageIconResId(i));

            } else if (adapter instanceof IconUrlTabProvider) {

                if (mIconUrlUseText) {

                    addTextTab(i, pager.getAdapter().getPageTitle(i));
                } else {

                    addIconUrlTab(i, ((IconUrlTabProvider) adapter).getPageIconUrl(i),
                            ((IconUrlTabProvider) adapter).getPageIconSelectedUrl(i));
                }

            } else {

                addTextTab(i, pager.getAdapter().getPageTitle(i));
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
            if (childView instanceof TextView) {

                if (currentPosition == i) {

                    ((TextView) childView).setTextColor(colorTabTextSelected);
                } else {

                    ((TextView) childView).setTextColor(colorTabTextDefault);
                }
            }
        }
    }

    private void addTextTab(int position, CharSequence title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setTextColor(colorTabTextDefault);
        tab.setGravity(Gravity.CENTER);

        if (isTabTextSingleLine) {

            tab.setSingleLine();

        } else {

            tab.setLineSpacing(tabTextLineSpace, 0f);
        }

        addTab(position, tab);
        if (currentPosition == position) {

            setTag(tab);
            tab.setTextColor(colorTabTextSelected);
        }
    }

    private void addIconTab(final int position, int resId) {

        ImageView tab = new ImageView(getContext());
        tab.setImageResource(resId);
        tab.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        addTab(position, tab);
        if (currentPosition == position) {

            setTag(tab);
            tab.setSelected(true);
        }
    }

    private void addIconUrlTab(final int position, String url, String selectedUrl) {

        IconTabImageView iconTabImageView = null;
        if (mIconTabImageViewProvider != null) {

            iconTabImageView = mIconTabImageViewProvider.getTabImageView(getContext()
                    , mIconUrlWidth, mIconUrlHeight);
        }
        if (iconTabImageView == null || iconTabImageView.getView() == null) {

            return;
        }
        addTab(position, iconTabImageView.getView());
        iconTabImageView.setImageUrl(url, selectedUrl);
        if (currentPosition == position) {

            setTag(iconTabImageView);
            iconTabImageView.setSelected(true);
        }
    }

    private void addTab(final int position, View tab) {

        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnTabClickLisn != null)
                    mOnTabClickLisn.onTabItemClick(v, position);

                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }


    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            if (tabBackgroundResId != 0)
                v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                if (mTypeface != null)
                    tab.setTypeface(mTypeface);//#update

                if (tabTextBold)
                    tab.getPaint().setFakeBoldText(true);
                else
                    tab.getPaint().setFakeBoldText(false);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                        tab.setAllCaps(true);
                    } else {

                        //#update
                        //tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }

                if (i == currentPosition)
                    setTabViewSelectState(tab, false);
            }
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

        if (isInEditMode() || tabCount == 0)
            return;

        final int height = getHeight();
        // draw indicator line

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        int tempScrollOffset = getWidth() / 2 - currentTab.getWidth() / 2;
        if (tempScrollOffset > scrollOffset)
            scrollOffset = getWidth() / 2 - currentTab.getWidth() / 2;

        if (!mIndicatorEnable)
            return;

        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (mIndicatorBitmap != null) {

            int indicatorHoriPadding = (int) ((lineRight - lineLeft - mIndicatorBitmap.getWidth()) / 2f);
            canvas.drawBitmap(mIndicatorBitmap, lineLeft + indicatorHoriPadding, height - indicatorMarginBottom - mIndicatorBitmap.getHeight(), rectPaint);

        } else {

            rectPaint.setColor(indicatorColor);
            int indicatorHoriPadding = indicatorFixWidth > 0 ? (int) ((lineRight - lineLeft - indicatorFixWidth) / 2f) : mUnderLineHoriPadding;
            if (indicatorRoundRect) {

                canvas.drawRoundRect(new RectF(lineLeft + indicatorHoriPadding, height - indicatorHeight - indicatorMarginBottom,
                        lineRight - indicatorHoriPadding, height - indicatorMarginBottom), indicatorHeight / 1.5f, indicatorHeight / 1.5f, rectPaint);
            } else {

                canvas.drawRect(lineLeft + indicatorHoriPadding, height - indicatorHeight - indicatorMarginBottom,
                        lineRight - indicatorHoriPadding, height - indicatorMarginBottom, rectPaint);
            }

        }

        // draw underline
        if (underlineHeight > 0) {

            rectPaint.setColor(underlineColor);
            canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
        }

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

            Object viewObj = getTag();
            if (viewObj instanceof TextView) {

                TextView view = (TextView) viewObj;
                if (view != null)
                    setTabViewUnSelectedState(view, needAnim);

                view = (TextView) tabsContainer.getChildAt(position);
                if (view != null) {

                    setTag(view);
                    setTabViewSelectState(view, needAnim);
                }

            } else if (viewObj instanceof ImageView) {

                ImageView view = (ImageView) viewObj;
                if (view != null)
                    view.setSelected(false);

                view = (ImageView) tabsContainer.getChildAt(position);
                if (view != null) {

                    setTag(view);
                    view.setSelected(true);
                }

            } else if (viewObj instanceof IconTabImageView) {

                IconTabImageView ativ = (IconTabImageView) viewObj;
                if (ativ != null)
                    ativ.setSelected(false);

                ativ = (IconTabImageView) tabsContainer.getChildAt(position);
                if (ativ != null) {

                    setTag(ativ);
                    ativ.setSelected(true);
                }
            }

            if (delegatePageListener != null)
                delegatePageListener.onPageSelected(position);
        }
    }

    private void setTabViewSelectState(final TextView tv, boolean needAnim) {

        if (tv == null)
            return;

        if (needAnim) {

            final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
            final ValueAnimator va = new ValueAnimator();
            va.setDuration(200);
            va.setFloatValues(0f, 1f);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float val = (float) animation.getAnimatedValue();
                    int textColor = (int) argbEvaluator.evaluate(val, colorTabTextDefault, colorTabTextSelected);
                    tv.setTextColor(textColor);
                    if (tabTextSelectScale) {
                        ViewCompat.setScaleX(tv, 1 + (val * 0.1f));
                        ViewCompat.setScaleY(tv, 1 + (val * 0.1f));
                    }
                }
            });
            va.start();
        } else {

            tv.setTextColor(colorTabTextSelected);
            if (tabTextSelectScale) {
                ViewCompat.setScaleX(tv, 1.1f);
                ViewCompat.setScaleY(tv, 1.1f);
            }
        }

        tv.setBackground(colorTabTextBgSelected);

        if (!tabTextBold && tabTextSelectedBold) {
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

    }

    private void setTabViewUnSelectedState(final TextView tv, boolean needAnim) {

        if (tv == null)
            return;

        if (needAnim) {

            final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
            ValueAnimator va = new ValueAnimator();
            va.setDuration(200);
            va.setFloatValues(1f, 0f);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float val = (float) animation.getAnimatedValue();
                    int textColor = (int) argbEvaluator.evaluate(val, colorTabTextDefault, colorTabTextSelected);
                    tv.setTextColor(textColor);
                    if (tabTextSelectScale) {
                        ViewCompat.setScaleX(tv, 1 + (val * 0.1f));
                        ViewCompat.setScaleY(tv, 1 + (val * 0.1f));
                    }
                }
            });
            va.start();
        } else {

            tv.setTextColor(colorTabTextDefault);
            if (tabTextSelectScale) {
                ViewCompat.setScaleX(tv, 1f);
                ViewCompat.setScaleY(tv, 1f);
            }
        }

        tv.setBackground(colorTabTextBgDefault);

        if (!tabTextBold && tabTextSelectedBold) {
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
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

    public void setIndicatorFixWidth(int indicatorFixWidthPx) {
        this.indicatorFixWidth = indicatorFixWidthPx;
        invalidate();
    }

    public void setIndicatorRoundRect(boolean roundRect) {

        this.indicatorRoundRect = roundRect;
        invalidate();
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

    public void setIndicatorEnable(boolean enable) {

        mIndicatorEnable = enable;
        invalidate();
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

    public void setIndicatorMarginBottom(int marginBottom) {

        indicatorMarginBottom = marginBottom;
    }

    public int getIndicatorMarginBottom() {

        return indicatorMarginBottom;
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
    }

    public int getTabPaddingLeftRight() {

        return tabPadding;
    }

    public void setResIndicator(int resIndicator) {

        mIndicatorBitmap = BitmapFactory.decodeResource(getResources(), resIndicator);
        invalidate();
    }

    public void setTabTextSingleLine(boolean tabTextSingleLine) {

        isTabTextSingleLine = tabTextSingleLine;
    }

    public void setIconUrlLength(int width, int height) {

        mIconUrlWidth = width;
        mIconUrlHeight = height;
    }

    public void setIconUrlUseText(boolean useText) {

        mIconUrlUseText = useText;
    }

    /***
     *  设置Tab图片观察者
     *
     * @param iconTabImageViewProvider
     */
    public void setIconTabImageViewProvider(IconTabImageViewProvider iconTabImageViewProvider) {

        mIconTabImageViewProvider = iconTabImageViewProvider;
    }

    //private Locale locale;//#update

    public void setCurrentPosition(int currentPosition) {

        this.currentPosition = currentPosition;
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

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

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
