package com.androidex.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 文字加粗的TextView
 */
public class BoldTextView extends TextView {

	public BoldTextView(Context context) {

		super(context);
		init();
	}

	public BoldTextView(Context context, @Nullable AttributeSet attrs) {

		super(context, attrs);
		init();
	}

	public BoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public BoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {

		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init(){

		if(getPaint() != null)
			getPaint().setFakeBoldText(true);

		setTypeface(Typeface.DEFAULT_BOLD);
	}
}
