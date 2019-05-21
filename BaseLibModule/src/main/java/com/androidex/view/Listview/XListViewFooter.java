package com.androidex.view.Listview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.util.TextUtil;
import com.androidex.view.progress.ProgressWheel;

public class XListViewFooter extends LinearLayout {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_FAILED = 3;
    public final static int STATE_REMIND = 4;

    private View mContentView, mFlLoadingContent;
    private ProgressWheel mPwLoading;
    private ImageView mArrowImageView;
    private TextView mHintView, mBtnReLoadMore;
    private RetryLoadClickListener mRetryClickLisn;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    private int mState = STATE_NORMAL;

    /**
     * 手机加载提示文字
     */
    private String remindLoad;

    private boolean isLoadingHideText;

    private Typeface mTypeface;

    public String getRemindLoad() {
        return remindLoad;
    }

    public void setRemindLoad(String remindLoad) {
        this.remindLoad = remindLoad;
    }

    private Model model;

    public void setModel(Model model) {
        this.model = model;


        if (mArrowImageView != null)
            mArrowImageView.setImageResource(model.arrow);

        if (mHintView != null) {

            mHintView.setText(model.remindPull);
            mHintView.setTextColor(model.textColor);
            mHintView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, model.textDpSize);
        }
    }

    public Model getModel() {
        return model;
    }

    public void setTypeface(Typeface typeface) {
        this.mTypeface = typeface;

        if (mHintView != null)
            mHintView.setTypeface(mTypeface);

        if (mBtnReLoadMore != null)
            mBtnReLoadMore.setTypeface(mTypeface);
    }

    public XListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public XListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

        model = new Model();
        setOrientation(VERTICAL);

        FrameLayout moreView = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        mFlLoadingContent = moreView.findViewById(R.id.flLoadingContent);
        mPwLoading = (ProgressWheel) moreView.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_hint_textview);

        mArrowImageView = (ImageView) findViewById(R.id.xlistview_footer_arrow);
        mArrowImageView.setImageResource(model.arrow);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mBtnReLoadMore = (TextView) moreView.findViewById(R.id.btnReLoadMore);
        mBtnReLoadMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRetryClickLisn != null) {

                    boolean result = mRetryClickLisn.onRetryLoadClickListener();
                    if (result)
                        setState(STATE_LOADING);
                }
            }
        });
    }

    public TextView getmHintView() {
        return mHintView;
    }

    public void setHideHintView(boolean state) {
        isLoadingHideText = state;
    }

    private void hideHintView() {
        mHintView.setText(model.remindPull);
    }

    public boolean isFailedState() {

        return mBtnReLoadMore.getVisibility() == View.VISIBLE;
    }

    public void setState(int state) {
//		mHintView.setVisibility(View.INVISIBLE);
//		mProgressBar.setVisibility(View.INVISIBLE);
//		mHintView.setVisibility(View.INVISIBLE);
        if (state == mState) return;

        if (state == STATE_LOADING) {
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mPwLoading.setVisibility(View.VISIBLE);
        } else {
            mArrowImageView.setVisibility(View.VISIBLE);
            mPwLoading.setVisibility(View.INVISIBLE);
        }

        if (state == STATE_READY) {

            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(model.remindRelease);

            if (mState != STATE_READY) {
                mArrowImageView.clearAnimation();
                mArrowImageView.startAnimation(mRotateUpAnim);
            }
        } else if (state == STATE_LOADING) {

            if (mFlLoadingContent.getVisibility() != View.VISIBLE) {
                mFlLoadingContent.setVisibility(View.VISIBLE);
            }

            if (mBtnReLoadMore.getVisibility() != View.INVISIBLE) {
                mBtnReLoadMore.setVisibility(View.INVISIBLE);
            }

            mPwLoading.setVisibility(View.VISIBLE);
            mHintView.setVisibility(View.VISIBLE);

            mHintView.setText(model.doing);
            if (isLoadingHideText) {
                hideHintView();
            }

        } else if (state == STATE_FAILED) {

            if (mFlLoadingContent.getVisibility() != View.INVISIBLE) {
                mFlLoadingContent.setVisibility(View.INVISIBLE);
            }

            if (mBtnReLoadMore.getVisibility() != View.VISIBLE) {
                mBtnReLoadMore.setVisibility(View.VISIBLE);
            }

        } else if (state == STATE_REMIND) {

            if (mFlLoadingContent.getVisibility() != View.VISIBLE) {
                mFlLoadingContent.setVisibility(View.VISIBLE);
            }

            if (mBtnReLoadMore.getVisibility() != View.INVISIBLE) {
                mBtnReLoadMore.setVisibility(View.INVISIBLE);
            }

            normal();
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setTextColor(Color.GRAY);
            mHintView.setText(remindLoad);

        } else {

            if (mState == STATE_READY) {
                mArrowImageView.startAnimation(mRotateDownAnim);
            }
            if (mState == STATE_LOADING) {
                mArrowImageView.clearAnimation();
            }

            if (mFlLoadingContent.getVisibility() != View.VISIBLE) {
                mFlLoadingContent.setVisibility(View.VISIBLE);
            }

            mHintView.setVisibility(View.VISIBLE);
            if (TextUtil.isEmpty(remindLoad)) {

                mHintView.setText(model.remindPull);

            } else {

                mHintView.setText(remindLoad);
            }
        }
        mState = state;
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    /**
     * normal status
     */
    public void normal() {

        mHintView.setVisibility(View.VISIBLE);
        mPwLoading.setVisibility(View.INVISIBLE);
    }


    /**
     * loading status
     */
//    public void loading() {
//        mHintView.setVisibility(View.GONE);
//        mPwLoading.setVisibility(View.VISIBLE);
//    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        mContentView.setVisibility(View.GONE);
//		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
//		lp.height = 0;
//		mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        mContentView.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

    public void resetHeight(int height) {

        mBtnReLoadMore.setPadding(0, 0, 0, 0);
        mFlLoadingContent.setPadding(0, 0, 0, 0);
        mFlLoadingContent.getLayoutParams().height = height;
        mContentView.getLayoutParams().height = height;
        mContentView.requestLayout();
    }

    public void setRetryLoadClickListener(RetryLoadClickListener lisn) {

        mRetryClickLisn = lisn;
    }

    public TextView getTipFailedTextView() {

        return mBtnReLoadMore;
    }

    public TextView getTipLoadingTextView() {

        return mHintView;
    }

    public ProgressWheel getTipLoadingView() {

        return mPwLoading;
    }

    public interface RetryLoadClickListener {

        boolean onRetryLoadClickListener();
    }

    public class Model {

        public String remindPull = getContext().getString(R.string.xlistview_footer_hint_normal);
        public String remindRelease = getContext().getString(R.string.xlistview_footer_hint_ready);
        public String doing = getContext().getString(R.string.xlistview_footer_hint_loading);

        public int arrow = R.drawable.ex_ic_xlv_pull_refresh_arrow;

        public boolean sliptLine;

        public int textColor = 0xFF999999;
        public int textDpSize = 14;
    }

}
