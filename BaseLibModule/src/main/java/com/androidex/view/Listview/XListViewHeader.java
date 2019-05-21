package com.androidex.view.Listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.R;
import com.androidex.view.progress.ProgressWheel;

public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressWheel mPwLoading;
	private TextView mHintTextView;
	private String mPullRefreshingText;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	private Model model;

	public void setModel(Model model) {
		this.model = model;

		if(mArrowImageView != null)
			mArrowImageView.setImageResource(model.arrow);

		if(mHintTextView != null) {

			mHintTextView.setText(model.remindPull);
			mHintTextView.setTextColor(model.textColor);
			mHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,model.textDpSize);
		}

	}

	public Model getModel() {
		return model;
	}

	public XListViewHeader(Context context) {
		super(context);
		model = new Model();
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		
		setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
		mArrowImageView.setImageResource(model.arrow);
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
		mPwLoading = (ProgressWheel)findViewById(R.id.xlistview_header_progressbar);
		//mPwLoading.setVisibility(View.INVISIBLE);//布局文件中就是隐藏
		
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
	}

	public void setState(int state) {
		if (state == mState) return ;
		
		if (state == STATE_REFRESHING) {
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mPwLoading.setVisibility(View.VISIBLE);
		} else {
			mArrowImageView.setVisibility(View.VISIBLE);
			mPwLoading.setVisibility(View.INVISIBLE);
		}
		
		switch(state){
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(model.remindPull);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(model.remindRelease);
			}
			break;
		case STATE_REFRESHING:
			if(mPullRefreshingText != null && mPullRefreshingText.length() != 0){
				mHintTextView.setText(mPullRefreshingText);
			}else{
				mHintTextView.setText(model.doing);
			}
			break;
			default:
		}
		
		mState = state;
	}
	
	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
//		return mContainer.getHeight();
		return mContainer.getLayoutParams().height;
	}

	public void setPullRefreshingText(String text){
		
		mPullRefreshingText = text;
	}

	public TextView getTipTextView(){

		return mHintTextView;
	}

	public ProgressWheel getTipLoadingView(){

		return mPwLoading;
	}

	public class Model{

		public String remindPull = getContext().getString(R.string.xlistview_header_hint_normal);
		public String remindRelease = getContext().getString(R.string.xlistview_header_hint_ready);
		public String doing = getContext().getString(R.string.xlistview_header_hint_loading);

		public int arrow = R.drawable.ex_ic_xlv_pull_refresh_arrow;

		public boolean sliptLine;

		public int textColor = 0xFF999999;
		public int textDpSize = 14;
	}
}
