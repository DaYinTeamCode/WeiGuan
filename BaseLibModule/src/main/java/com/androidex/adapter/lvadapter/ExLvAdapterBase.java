package com.androidex.adapter.lvadapter;

import android.widget.BaseAdapter;

import com.androidex.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView适配器基类
 * @param <T>
 */
public abstract class ExLvAdapterBase<T> extends BaseAdapter {

	private List<T> mData;

	protected ExLvAdapterBase() {}

	protected ExLvAdapterBase(List<T> data) {

		setData(data);
	}

	@Override
	public int getCount() {

		return mData == null ? 0 : mData.size();
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public T getItem(int position) {

		return checkPosition(position) ? mData.get(position) : null;
	}

	public List<T> getData() {

		return mData;
	}

	public void setData(List<T> data) {

		mData = data;
	}

	public void add(T item) {

		if(item == null)
			return;

		if(mData == null)
			mData = new ArrayList<T>();

		mData.add(item);
	}

	public void add(int position, T item) {

		if(item == null)
			return;

		if(mData == null)
			mData = new ArrayList<T>();

		if(checkAddPosition(position))
			mData.add(position, item);
	}

	public void addAll(List<? extends T> data) {

		if (CollectionUtil.isEmpty(data))
			return;

		if (mData == null)
			mData = new ArrayList<T>();

		mData.addAll(data);
	}

	public void addAll(int position, List<? extends T> item) {

		if(item == null)
			return;

		if(mData == null)
			mData = new ArrayList<T>();

		if(checkAddPosition(position))
			mData.addAll(position, item);
	}

	public boolean remove(T item) {

		if (mData != null && item != null)
			return mData.remove(item);
		else
			return false;
	}

	public void remove(int position) {

		if(checkPosition(position))
			mData.remove(position);
	}

	public void clear(){

		if(mData != null)
			mData.clear();
	}

	public boolean checkPosition(int position){

		return position >= 0 && position < (mData == null ? 0 : mData.size());
	}

	public boolean checkAddPosition(int position){

		return position >= 0 && position <= (mData == null ? 0 : mData.size());
	}

	public boolean isEmpty() {

		return mData == null || mData.size() == 0;
	}

	/**
	 * 获取最后一item的position
	 * @return 如果集合为空返回－1
	 */
	public int getLastIitemPosition(){

		return CollectionUtil.size(mData) - 1;
	}

	/**
	 * 获取集合中指定type的第一个position
	 * @param type
	 * @return
	 */
	public int findItemViewTypePosition(int type){

		if(CollectionUtil.isEmpty(mData))
			return -1;

		for(int i=0; i<mData.size(); i++){

			if(getItemViewType(i) == type)
				return i;
		}

		return -1;
	}

	/**
	 * 获取最后一个元素的类型
	 * @return
	 */
	public int findLastItemViewType(){

		if(CollectionUtil.isEmpty(mData))
			return -1;
		else
			return getItemViewType(mData.size() - 1);
	}
}
