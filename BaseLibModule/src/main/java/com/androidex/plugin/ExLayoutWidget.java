package com.androidex.plugin;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用来加载一个单独的View
 * @author yhb
 *
 */
public abstract class ExLayoutWidget extends ExBaseWidget{

	public ExLayoutWidget(Activity activity){

		this(activity, (Object[])null);
	}

	public ExLayoutWidget(Activity activity, Object... args){

		this(activity, null, args);
	}

	public ExLayoutWidget(Activity activity, ViewGroup parent, Object... args){

		super(activity);
		View contentView = onCreateView(activity, parent, args);
		setContentView(contentView);
	}

	protected abstract View onCreateView(Activity activity, ViewGroup parent, Object... args);
}
