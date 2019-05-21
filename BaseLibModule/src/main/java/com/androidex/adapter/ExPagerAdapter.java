package com.androidex.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewPager适配器
 * @param <T>
 */
public abstract class ExPagerAdapter<T> extends PagerAdapter {

	private List<T> mData;
	private OnItemViewClickListener mOnItemViewClickLisn;
	private boolean mItemPositionChangedEnable;

	public ExPagerAdapter(){

	}
	
	public ExPagerAdapter(List<T> data) {

		setData(data);
	}

	@Override
	public int getCount() {
		
		return mData == null ? 0 : mData.size();
	}

	public T getItem(int position) {

		return checkPosition(position) ? mData.get(position) : null;
	}

	public void setData(List<T> data) {
		
		this.mData = data;
	}

	public void addAll(List<? extends T> list){

		if (list == null)
			return;

		if (mData == null)
			mData = new ArrayList<T>();

		mData.addAll(list);
	}

	public List<T> getData() {
		
		return mData;
	}

	public void setItemPositionChangedEnable(boolean enable){

		mItemPositionChangedEnable = enable;
	}

	@Override
	public int getItemPosition(Object object) {

		return mItemPositionChangedEnable ? POSITION_NONE : POSITION_UNCHANGED;
	}

	/**
	 * container 如果是ViewPager
	 * ViewPager内容会检测LayoutParam, w,h都会变成MATCH_PARENT
	 * @param container
	 * @param position
	 * @return
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		View view = createItem(container, position);
		container.addView(view);
		return view;
	}

	protected abstract View createItem(ViewGroup container, int position);

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		
		return view == object;
	}

	public boolean checkPosition(int position){

		return position >= 0 && position < (mData == null ? 0 : mData.size());
	}

	public boolean isEmpty() {

		return mData == null || mData.size() == 0;
	}

	public void setOnItemViewClickListener(OnItemViewClickListener lisn){

		mOnItemViewClickLisn = lisn;
	}

	protected void callbackItemViewClick(int position, View view){

		if(mOnItemViewClickLisn != null)
			mOnItemViewClickLisn.onItemViewClick(position, view);
	}
}
