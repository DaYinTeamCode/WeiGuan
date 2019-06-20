package com.sjteam.weiguan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.util.TextUtil;
import com.androidex.util.ViewUtil;
import com.sjteam.weiguan.R;

/**
 * 确认对话框
 * 夜间模式已改完
 */
public class CpConfirmDialog extends CpBaseDialog {

	private int mDrawableResId;
	private String mLeftBtnText = TextUtil.TEXT_EMPTY;
	private String mRightBtnText = TextUtil.TEXT_EMPTY;
	private int mContentMaxLines = 2;
	private OnDialogClickListener mLeftBtnLisn, mRightBtnlisn;

	public CpConfirmDialog(Context context) {

		super(context);
	}

	@Override
	protected void onDialogCreate(Bundle savedInstanceState) {

		super.onDialogCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);
		initContentView();
	}

	private void initContentView() {

		ImageView ivTip = (ImageView) findViewById(R.id.ivTip);
		if(mDrawableResId != 0){

			ivTip.setImageResource(mDrawableResId);
		}else{

			ViewUtil.goneView(ivTip);
		}

		TextView tv = (TextView) findViewById(R.id.tvTitle);
		tv.setText(getTitleText());
		if(tv.length() == 0)
			ViewUtil.goneView(tv);

		tv = (TextView) findViewById(R.id.tvContent);
		tv.setText(getContentText());
		tv.setMaxLines(mContentMaxLines);

		TextView btn = null;
		btn = (TextView) findViewById(R.id.tvBtnLeft);
		btn.setText(mLeftBtnText);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				callbackOnLeftButtonClickListener();
			}
		});

		btn = (TextView) findViewById(R.id.tvBtnRight);
		btn.setText(mRightBtnText);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				callbackOnRightButtonClickListener();
			}
		});
	}

	public void setContentTextOnShowing(String text){

		TextView tvContent = (TextView) findViewById(R.id.tvContent);
		if(tvContent != null)
			tvContent.setText(text);
	}

	public void setContentMaxLins(int maxLins){

		mContentMaxLines = maxLins;
	}

	public void setLeftButtonText(int rid){

		setLeftButtonText(getContext().getString(rid));
	}

	public void setLeftButtonText(String text){

		mLeftBtnText = TextUtil.filterNull(text);
	}

	public void setRightButtonText(int rid){

		setRightButtonText(getContext().getString(rid));
	}

	public void setRightButtonText(String text){

		mRightBtnText = TextUtil.filterNull(text);
	}

	public String getLeftButtonText(OnDialogClickListener onDialogClickListener){

		return mLeftBtnText;
	}

	public String getRightButtonText(){

		return mRightBtnText;
	}

	public void setLeftButtonClickListener(OnDialogClickListener lisn){

		mLeftBtnLisn = lisn;
	}

	public void setRightButtonClickListener(OnDialogClickListener lisn){

		mRightBtnlisn = lisn;
	}

	public void setTipDrawableResId(int resId) {

		this.mDrawableResId = resId;
	}

	protected void callbackOnLeftButtonClickListener(){

		if(mLeftBtnLisn != null)
			mLeftBtnLisn.onClick(this);
	}

	protected void callbackOnRightButtonClickListener(){

		if(mRightBtnlisn != null)
			mRightBtnlisn.onClick(this);
	}
}
