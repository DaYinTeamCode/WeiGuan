package com.androidex.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.util.DeviceUtil;
import com.androidex.util.VglpUtil;

/**
 * Ex框架的view，实现了titlebar的view
 * @author yhb
 */
public class ExDecorView extends FrameLayout {

    private FrameLayout mFlTitleView;
    private LinearLayout mLlTitleLeftView, mLlTitleMiddleView, mLlTitleRightView;
    private boolean mStatusBarIsTrans;
    private Style mStyle;

    public ExDecorView(Context context) {

        super(context);
        initStyle(context.obtainStyledAttributes(R.styleable.ExDecorView));
        initExDecorView();
    }

    public ExDecorView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initStyle(context.obtainStyledAttributes(attrs, R.styleable.ExDecorView));
        initExDecorView();
    }

    public ExDecorView(Context context, int styleResId){

        super(context);
        initStyle(context.obtainStyledAttributes(styleResId, R.styleable.ExDecorView));
        initExDecorView();
    }

    public ExDecorView(Context context, Style style) {

        super(context);
        initStyle(style);
        initExDecorView();
    }

    private void initStyle(TypedArray typedArray) {

        initStyle(new Style(typedArray));
        typedArray.recycle();
    }

    private void initStyle(Style style) {

        if (style == null) {

            mStyle = new Style();
        } else {

            mStyle = style;
        }
    }

    private void initExDecorView() {

        // 初始化整个ContentView
        if (getId() == NO_ID)
            setId(R.id.ex_decor_view_root);

        if (mStyle.mBackgroundResId != 0)
            setBackgroundResource(mStyle.mBackgroundResId);
    }

    public Style getStyle() {

        return mStyle;
    }

    public boolean titleViewIsAdd(){

        return mFlTitleView != null;
    }

    public FrameLayout getTitleViewMaybeNull(){

        return mFlTitleView;
    }

    public FrameLayout getTitleView() {

        addTitleViewIfNotAdd();
        return mFlTitleView;
    }

    public LinearLayout getTitleLeftView() {

        addTitleViewIfNotAdd();
        return mLlTitleLeftView;
    }

    public LinearLayout getTitleMiddleView() {

        addTitleViewIfNotAdd();
        return mLlTitleMiddleView;
    }

    public LinearLayout getTitleRightView() {

        addTitleViewIfNotAdd();
        return mLlTitleRightView;
    }

    /**
     * 获取标题栏高度
     * @return
     */
    public int getTitleHeight(){

        return mStatusBarIsTrans ? mStyle.mTitleHeight + DeviceUtil.STATUS_BAR_HEIGHT : mStyle.mTitleHeight;
    }

    public boolean isTitleViewSupportStatusBarTrans(){

        return mStatusBarIsTrans;
    }

    public boolean setTitleViewSupportStatusBarTrans(boolean support, boolean kitkatEnable){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return false;

        if(kitkatEnable || Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){

            mStatusBarIsTrans = support;
            if(mFlTitleView == null)
                return true;

            resetTitleViewHeight(getTitleViewHeight());
            resetTitleContentViewsTopMargin(getTitleContentViewTopMargin());
            resetConentViewsTopMargin(getContentViewTopMargin());
            return true;
        }else{

            return false;
        }
    }

    public void goneTitleView(){

        if(mFlTitleView == null)
            return;

        //隐藏title view
        if(mFlTitleView.getVisibility() != GONE)
            mFlTitleView.setVisibility(GONE);

        //重设content views top margin
        if(!mStyle.mTitleIsFloat)
            resetConentViewsTopMargin(0);
    }

    public void showTitleView(){

        if(mFlTitleView == null)
            return;

        //显示title view
        if(mFlTitleView.getVisibility() != VISIBLE)
            mFlTitleView.setVisibility(VISIBLE);

        //重设content views top margin
        if(!mStyle.mTitleIsFloat)
            resetConentViewsTopMargin(getContentViewTopMargin());
    }

	/*
	 * add title view left part
	 */

    public ImageView addTitleLeftImageViewBack(OnClickListener lisn) {

        return addTitleLeftImageView(mStyle.mTitleBackIconResId, lisn);
    }

    public ImageView addTitleLeftImageView(int icResId, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, lisn);
        mLlTitleLeftView.addView(iv, getLinearLayoutParamsByTitleHeight());
        return iv;
    }

    public ImageView addTitleLeftImageViewHoriWrap(int icResId, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, lisn);
        mLlTitleLeftView.addView(iv, getLinearHoriWrapLayoutParamsByTitleHeight(0));
        return iv;
    }

    public TextView addTitleLeftTextView(int textRid, OnClickListener lisn) {

        return addTitleLeftTextView(getResources().getText(textRid), lisn);
    }

    public TextView addTitleLeftTextView(CharSequence text, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        TextView tv = getTitleClickerTextView(text, mStyle.mTitleClickerHoriPadding, lisn);
        mLlTitleLeftView.addView(tv, getLinearHoriWrapLayoutParamsByTitleHeight(0));
        return tv;
    }

    public void addTitleLeftView(View v) {

        addTitleViewIfNotAdd();
        mLlTitleLeftView.addView(v);
    }

    public void addTitleLeftView(View v, LinearLayout.LayoutParams lllp) {

        addTitleViewIfNotAdd();
        mLlTitleLeftView.addView(v, lllp);
    }

    public void setContentView(View v) {

        setContentView(v, null);
    }

    public void setContentView(View v, LayoutParams fllp) {

        if (fllp == null)
            fllp = VglpUtil.getFllpMM();

        if(mFlTitleView != null)
            fllp.topMargin += fllp.topMargin + getContentViewTopMargin();

        addView(v, 0, fllp);
    }

    public void addContentView(View v) {

        addContentView(v, null);
    }

    public void addContentView(View v, LayoutParams fllp) {

        if (fllp == null)
            fllp = VglpUtil.getFllpMM();

        if(mFlTitleView == null){

            addView(v, fllp);
        }else{

            if(fllp.gravity != Gravity.CENTER)
                fllp.topMargin += getContentViewTopMargin();

            addView(v, getChildCount() - 1, fllp);
        }
    }

    /*
     * add title view middle part
     */
    public ImageView addTitleMiddleImageView(int icResId) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, null);
        mLlTitleMiddleView.addView(iv, getLinearWrapLayoutParams());
//        resetTitleMiddleViewIfNeed();
        return iv;
    }

    public ImageView addTitleMiddleImageViewHoriWrap(int icResId) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, null);
        mLlTitleMiddleView.addView(iv, getLinearHoriWrapLayoutParamsByTitleHeight(0));
//        resetTitleMiddleViewIfNeed();
        return iv;
    }

    public TextView addTitleMiddleTextView(int textRid) {

        return addTitleMiddleTextView(getResources().getText(textRid));
    }

    public TextView addTitleMiddleTextView(CharSequence text) {

        addTitleViewIfNotAdd();
        TextView tv = getTitleTitleTextView(text, 0, mStyle.mTitleTextIsBold, null);
        mLlTitleMiddleView.addView(tv, getLinearWrapLayoutParams());
//        resetTitleMiddleViewIfNeed();
        return tv;
    }

    public TextView addTitleMiddleTextViewMainStyle(int textRid) {

        return addTitleMiddleTextViewMainStyle(getResources().getString(textRid));
    }

    public TextView addTitleMiddleTextViewMainStyle(CharSequence text) {

        addTitleViewIfNotAdd();
        TextView tv = getTitleMainTextView(text, null);
        mLlTitleMiddleView.addView(tv, getLinearWrapLayoutParams());
//        resetTitleMiddleViewIfNeed();
        return tv;
    }

    public TextView addTitleMiddleTextViewSubStyle(int textRid) {

        return addTitleMiddleTextViewSubStyle(getResources().getText(textRid));
    }

    public TextView addTitleMiddleTextViewSubStyle(CharSequence text) {

        addTitleViewIfNotAdd();
        TextView tv = getTitleSubTextView(text, null);
        mLlTitleMiddleView.addView(tv, getLinearWrapLayoutParams());
//        resetTitleMiddleViewIfNeed();
        return tv;
    }

    public void addTitleMiddleView(View v) {

        addTitleViewIfNotAdd();
        mLlTitleMiddleView.addView(v);
    }

    public void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp) {

        addTitleMiddleView(v, lllp, true);
    }

    public void addTitleMiddleView(View v, LinearLayout.LayoutParams lllp, boolean horiCenter) {

        addTitleViewIfNotAdd();
        mLlTitleMiddleView.addView(v, lllp);
//        if(horiCenter)
//            resetTitleMiddleViewIfNeed();
    }

    /*
     * add title view right part
     */
    public ImageView addTitleRightImageView(int icResId, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, lisn);
        mLlTitleRightView.addView(iv, getLinearLayoutParamsByTitleHeight());
        return iv;
    }

    public ImageView addTitleRightImageViewHoriWrap(int icResId, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        ImageView iv = getTitleImageView(icResId, lisn);
        mLlTitleRightView.addView(iv, getLinearHoriWrapLayoutParamsByTitleHeight(0));
        return iv;
    }

    public TextView addTitleRightTextView(int textRid, OnClickListener lisn) {

        return addTitleRightTextView(getResources().getText(textRid), lisn);
    }

    public TextView addTitleRightTextView(CharSequence text, OnClickListener lisn) {

        addTitleViewIfNotAdd();
        TextView tv = getTitleClickerTextView(text, mStyle.mTitleClickerHoriPadding, lisn);
        mLlTitleRightView.addView(tv, getLinearHoriWrapLayoutParamsByTitleHeight(0));
        return tv;
    }

    public void addTitleRightView(View v) {

        addTitleViewIfNotAdd();
        mLlTitleRightView.addView(v);
    }

    public void addTitleRightView(View v, LinearLayout.LayoutParams lllp) {

        addTitleViewIfNotAdd();
        mLlTitleRightView.addView(v, lllp);
    }

    private void addTitleViewIfNotAdd() {

        if(mFlTitleView != null)
            return;

        mFlTitleView = new FrameLayout(getContext());
        mFlTitleView.setId(R.id.ex_decor_view_title);
        mFlTitleView.getViewTreeObserver().addOnGlobalLayoutListener(createMiddleContainerViewLayoutObserver());
        mFlTitleView.setClickable(true);
        if (mStyle.mTitleBackgroundResId != 0)
            mFlTitleView.setBackgroundResource(mStyle.mTitleBackgroundResId);

        // 初始化标题栏左边布局
        mLlTitleLeftView = new LinearLayout(getContext());
        mLlTitleLeftView.setOrientation(LinearLayout.HORIZONTAL);
        mLlTitleLeftView.setGravity(Gravity.CENTER_VERTICAL);

        // 初始化标题栏中间标题布局
        mLlTitleMiddleView = new LinearLayout(getContext());
        mLlTitleMiddleView.setOrientation(LinearLayout.VERTICAL);
        if (mStyle.mTitleIsAndroidStyle)
            mLlTitleMiddleView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        else
            mLlTitleMiddleView.setGravity(Gravity.CENTER);

        // 标题栏右边布局
        mLlTitleRightView = new LinearLayout(getContext());
        mLlTitleRightView.setOrientation(LinearLayout.HORIZONTAL);
        mLlTitleRightView.setGravity(Gravity.CENTER_VERTICAL);

        int topMargin = getTitleContentViewTopMargin();
        mFlTitleView.addView(mLlTitleLeftView, getTitleContentViewLayoutParams(VglpUtil.W, Gravity.LEFT, topMargin));
        mFlTitleView.addView(mLlTitleMiddleView, getTitleContentViewLayoutParams(VglpUtil.M, Gravity.NO_GRAVITY, topMargin));
        mFlTitleView.addView(mLlTitleRightView, getTitleContentViewLayoutParams(VglpUtil.W, Gravity.RIGHT, topMargin));

        addView(mFlTitleView, getTitleViewLayoutParams(getTitleViewHeight()));
        resetConentViewsTopMargin(getContentViewTopMargin());
    }

    /*
     * title view common part
     */

    private void resetConentViewsTopMargin(int topMargin){

        View child = null;
        LayoutParams fllp = null;
        for(int i=0; i<getChildCount(); i++){

            child = getChildAt(i);
            if(child != mFlTitleView){

                fllp = (LayoutParams) child.getLayoutParams();
                if(fllp.gravity != Gravity.CENTER){

                    fllp.topMargin = topMargin;
                    child.setLayoutParams(fllp);
                }
            }
        }
    }

    private void resetTitleViewHeight(int height){

        mFlTitleView.getLayoutParams().height = height;
        mFlTitleView.requestLayout();
    }

    private void resetTitleContentViewsTopMargin(int topMargin){

        LinearLayout.LayoutParams lllp = null;
        View v = null;
        for(int i=0; i<mFlTitleView.getChildCount(); i++){

            v = mFlTitleView.getChildAt(i);
            lllp = (LinearLayout.LayoutParams) v.getLayoutParams();
            lllp.topMargin = topMargin;
            v.setLayoutParams(lllp);
        }
    }

//    private void resetTitleMiddleViewIfNeed() {
//
//        if (mStyle.mTitleIsAndroidStyle || mLlTitleMiddleView.getChildCount() > 1)
//            return;
//
//        mLlTitleView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onGlobalLayout() {
//
//                if (mLlTitleView.getWidth() != 0) {
//
//                    int leftSpace = mLlTitleMiddleView.getLeft();
//                    int rightSpace = mLlTitleView.getWidth() - mLlTitleMiddleView.getRight();
//                    if (leftSpace > rightSpace) {
//
//                        LinearLayout.LayoutParams lllp = (LinearLayout.LayoutParams) mLlTitleMiddleView.getLayoutParams();
//                        lllp.rightMargin = leftSpace - rightSpace;
//                    } else if (leftSpace < rightSpace) {
//
//                        LinearLayout.LayoutParams lllp = (LinearLayout.LayoutParams) mLlTitleMiddleView.getLayoutParams();
//                        lllp.leftMargin = rightSpace - leftSpace;
//                    }
//
////                    mLlTitleView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    mLlTitleMiddleView.requestLayout();
//                }
//
//            }
//        });
//    }

    /**
     * 中布局观察者
     * 用来重置中布局的左右间距
     *
     * @return 中部布局变化的观察者
     */
    private ViewTreeObserver.OnGlobalLayoutListener createMiddleContainerViewLayoutObserver() {

        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (mLlTitleMiddleView == null || mLlTitleMiddleView.getWidth() <= 0)
                    return;

                int leftSpace = mLlTitleLeftView == null ? 0 : mLlTitleLeftView.getWidth();
                int rightSpace = mLlTitleRightView == null ? 0 : mLlTitleRightView.getWidth();
                int horiMargin = leftSpace > rightSpace ? leftSpace : rightSpace;

                FrameLayout.LayoutParams lllp = (FrameLayout.LayoutParams) mLlTitleMiddleView.getLayoutParams();
                if (lllp.leftMargin != horiMargin || lllp.rightMargin != horiMargin) {

                    lllp.leftMargin = horiMargin;
                    lllp.rightMargin = horiMargin;
                    mLlTitleMiddleView.requestLayout();
                }
            }
        };
    }

    private ImageView getTitleImageView(int icResId, OnClickListener lisn) {

        ImageView iv = new ImageView(getContext());
        iv.setScaleType(ScaleType.CENTER_INSIDE);// system default is scaleType.FIT_CENTER

        if (icResId != 0)
            iv.setImageResource(icResId);

        if (mStyle.mTitleClickerBackgroundResId != 0)
            iv.setBackgroundResource(mStyle.mTitleClickerBackgroundResId);

        if (lisn != null)
            iv.setOnClickListener(lisn);

        return iv;
    }

    private TextView getTitleTitleTextView(CharSequence text, int textHoriPaddingDp, boolean textIsBold, OnClickListener lisn) {

        return getTitleTextView(text, mStyle.mTitleTextColor, mStyle.mTitleTextSize, textHoriPaddingDp, textIsBold, lisn);
    }

    private TextView getTitleClickerTextView(CharSequence text, int textHoriPaddingDp, OnClickListener lisn) {

        return getTitleTextView(text, mStyle.mTitleClickerTextColor, mStyle.mTitleClickerTextSize, textHoriPaddingDp, false, lisn);
    }

    private TextView getTitleMainTextView(CharSequence text, OnClickListener lisn) {

        return getTitleTextView(text, mStyle.mTitleTextColor, mStyle.mTitleMainTextSize, 0, false, lisn);
    }

    private TextView getTitleSubTextView(CharSequence text, OnClickListener lisn) {

        return getTitleTextView(text, mStyle.mTitleTextColor, mStyle.mTitleSubTextSize, 0, false, lisn);
    }

    private TextView getTitleTextView(CharSequence text, int textColor, int textSizeDp, int textHoriPaddingDp, boolean isBold, OnClickListener lisn) {

        TextView tv = new TextView(getContext());
        if (isBold)
            tv.getPaint().setFakeBoldText(true);

        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tv.setEllipsize(TruncateAt.END);
        tv.setText(text);

        if (textHoriPaddingDp > 0)
            tv.setPadding(textHoriPaddingDp, 0, textHoriPaddingDp, 0);

        if (textSizeDp > 0)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDp);

        if (textColor != 0)
            tv.setTextColor(textColor);

        if (mStyle.mTitleClickerBackgroundResId != 0)
            tv.setBackgroundResource(mStyle.mTitleClickerBackgroundResId);

        if (lisn != null)
            tv.setOnClickListener(lisn);

        return tv;
    }

    private int getTitleViewHeight(){

        return mStatusBarIsTrans ? mStyle.mTitleHeight + DeviceUtil.STATUS_BAR_HEIGHT : mStyle.mTitleHeight;
    }

    private int getTitleContentViewTopMargin(){

        return mStatusBarIsTrans ? DeviceUtil.STATUS_BAR_HEIGHT : 0;
    }

    private int getContentViewTopMargin(){

        if(mStyle.mTitleIsFloat)
            return mStyle.mTitleFloatContentTopMargin;
        else
            return mStatusBarIsTrans ? mStyle.mTitleHeight + DeviceUtil.STATUS_BAR_HEIGHT : mStyle.mTitleHeight;
    }

    private LayoutParams getTitleViewLayoutParams(int height) {

        if(mStyle.mTitleHeight > 0)
            return VglpUtil.getFllpSS(VglpUtil.M, height);
        else
            return VglpUtil.getFllpMW();
    }

    private FrameLayout.LayoutParams getTitleContentViewLayoutParams(int width, int gravity, int topMargin) {

        FrameLayout.LayoutParams fllp = null;

        if(mStyle.mTitleHeight > 0)
            fllp = VglpUtil.getFllpSS(width, mStyle.mTitleHeight, gravity);
        else
            fllp = VglpUtil.getFllpSS(width, VglpUtil.W, gravity);

        fllp.topMargin = topMargin;
        return fllp;
    }

    private LinearLayout.LayoutParams getLinearHoriWrapLayoutParamsByTitleHeight(int weight) {

        if(mStyle.mTitleHeight > 0)
            return VglpUtil.getLllpSS(VglpUtil.W, mStyle.mTitleHeight, weight);
        else
            return VglpUtil.getLllpSS(VglpUtil.W, VglpUtil.W, weight);
    }

    private LinearLayout.LayoutParams getLinearLayoutParamsByTitleHeight() {

        if (mStyle.mTitleHeight > 0)
            return VglpUtil.getLllpSS(mStyle.mTitleHeight, mStyle.mTitleHeight);
        else
            return VglpUtil.getLllpWW();
    }

    private LinearLayout.LayoutParams getLinearWrapLayoutParams() {

        return VglpUtil.getLllpWW();
    }

    public static class Style {

        public int mBackgroundResId;
        public boolean mTitleIsFloat;
        public boolean mTitleIsAndroidStyle;
        public int mTitleFloatContentTopMargin;
        public int mTitleHeight;

        public int mTitleBackgroundResId;
        public int mTitleTextColor;
        public int mTitleTextSize;
        public boolean mTitleTextIsBold;

        public int mTitleMainTextSize; // 主标题大小
        public int mTitleSubTextSize; // 副标题大小

        public int mTitleClickerBackgroundResId;
        public int mTitleClickerTextSize;
        public int mTitleClickerTextColor;

        public int mTitleBackIconResId;
        public int mTitleClickerHoriPadding;

        public Style() {

        }

        protected Style(TypedArray typedArray) {

            mBackgroundResId = typedArray.getResourceId(R.styleable.ExDecorView_exBackground, 0);
            mTitleIsFloat = typedArray.getBoolean(R.styleable.ExDecorView_exTitleFloat, false);
            mTitleIsAndroidStyle = typedArray.getBoolean(R.styleable.ExDecorView_exTitleAndroidStyle, false);
            mTitleFloatContentTopMargin = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleFloatContentTopMargin, 0);

            mTitleHeight = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleHeight, 0);
            mTitleBackgroundResId = typedArray.getResourceId(R.styleable.ExDecorView_exTitleBackground, 0);
            mTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleTextSize, 0);
            mTitleTextColor = typedArray.getColor(R.styleable.ExDecorView_exTitleTextColor, Color.WHITE);

            mTitleTextIsBold = typedArray.getBoolean(R.styleable.ExDecorView_exTitleTextBold, false);
            mTitleMainTextSize = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleMainTextSize, mTitleTextSize);
            mTitleSubTextSize = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleSubTextSize, mTitleTextSize);

            mTitleClickerBackgroundResId = typedArray.getResourceId(R.styleable.ExDecorView_exTitleClickerBackground, 0);
            mTitleClickerTextSize = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleClickerTextSize, mTitleTextSize);
            mTitleClickerTextColor = typedArray.getColor(R.styleable.ExDecorView_exTitleClickerTextColor, mTitleTextColor);


            mTitleBackIconResId = typedArray.getResourceId(R.styleable.ExDecorView_exTitleBackIcon, 0);
            mTitleClickerHoriPadding = typedArray.getDimensionPixelSize(R.styleable.ExDecorView_exTitleClickerHoriPadding, 0);
        }
    }
}
