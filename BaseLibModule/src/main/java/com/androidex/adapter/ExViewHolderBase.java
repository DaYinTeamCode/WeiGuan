package com.androidex.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class ExViewHolderBase implements ExViewHolder{
	
	protected int mPosition;
	private OnItemViewClickListener mClickLisn;
	private OnItemViewLongClickListener mLongClickLisn;

	@Override
	public void setOnItemViewClickListener(OnItemViewClickListener lisn) {

		mClickLisn = lisn;
	}

	@Override
	public void setOnItemViewLongClickListener(OnItemViewLongClickListener lisn) {

		mLongClickLisn = lisn;
	}

	@Override
	public int getConvertViewRid(){

		return 0;
	}

	@Override
	public View getConvertView(ViewGroup parent){

		return null;
	}

	@Override
	public void initConvertView(View convertView, int position) {

		mPosition = position;
		initConvertView(convertView);
	}

	@Override
	public void invalidateConvertView(Object obj, int position){
		
		mPosition = position;
		invalidateConvertView();
		invalidateConvertView(obj);
	}

	public void callbackOnItemViewClickListener(int position, View view) {

		if (mClickLisn != null)
			mClickLisn.onItemViewClick(position, view);
	}

	public void callbackOnItemViewLongClickListener(int position, View view) {

		if (mLongClickLisn != null)
			mLongClickLisn.onItemViewLongClick(position, view);
	}

	public void invalidateConvertView(Object obj){

	}

	public abstract void initConvertView(View convertView);
	public abstract void invalidateConvertView();

}