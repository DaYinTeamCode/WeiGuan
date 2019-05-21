package com.androidex.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidex.adapter.lvadapter.ExLvAdapterBase;

import java.util.List;

/**
 * 封装了ExViewHolder的ListView适配器
 *
 * @param <T>
 */
public abstract class ExAdapter<T> extends ExLvAdapterBase<T> {

    private OnItemViewClickListener mOnItemViewClickLisn;
    private OnItemViewLongClickListener mOnItemViewLongClickLisn;
    private OnItemViewSelectListener mOnItemViewSelectLisn;
    private OnItemViewInvalidateListener mOnItemInvalidateLisn;

    protected ExAdapter() {
    }

    protected ExAdapter(List<T> data) {

        setData(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ExViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = getViewHolder(position);

            int resid = viewHolder.getConvertViewRid();
            if (resid == 0)
                convertView = viewHolder.getConvertView(parent);
            else
                convertView = LayoutInflater.from(parent.getContext()).inflate(resid, parent, false);

            viewHolder.initConvertView(convertView, position);
            viewHolder.setOnItemViewClickListener(mOnItemViewClickLisn);
            viewHolder.setOnItemViewLongClickListener(mOnItemViewLongClickLisn);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ExViewHolder) convertView.getTag();
        }

        viewHolder.invalidateConvertView(getItem(position), position);

        return convertView;
    }

    protected abstract ExViewHolder getViewHolder(int position);

	/*
     * click listener part
	 */

    public void setOnItemViewClickListener(OnItemViewClickListener lisn) {

        mOnItemViewClickLisn = lisn;
    }

    public void setOnItemViewLongClickListener(OnItemViewLongClickListener lisn) {

        mOnItemViewLongClickLisn = lisn;
    }

    public void setOnItemViewSelectListener(OnItemViewSelectListener lisn) {

        mOnItemViewSelectLisn = lisn;
    }

    public void setOnItemViewInvalidateListener(OnItemViewInvalidateListener lisn) {

        mOnItemInvalidateLisn = lisn;
    }

    public OnItemViewClickListener getOnItemViewClickListener(){

        return mOnItemViewClickLisn;
    }

    public OnItemViewLongClickListener getOnItemViewLongClickListener(){

        return mOnItemViewLongClickLisn;
    }

    public OnItemViewInvalidateListener getOnItemViewInvalidateListener(){

        return mOnItemInvalidateLisn;
    }

    public void callbackOnItemViewClickListener(int position, View view) {

        if (mOnItemViewClickLisn != null)
            mOnItemViewClickLisn.onItemViewClick(position, view);
    }

    public void callbackOnItemViewLongClickListener(int position, View view) {

        if (mOnItemViewLongClickLisn != null)
            mOnItemViewLongClickLisn.onItemViewLongClick(position, view);
    }

    public boolean callbackOnItemViewSelectListener(int position, View view) {

        if (mOnItemViewSelectLisn == null)
            return false;
        else
            return mOnItemViewSelectLisn.onItemViewSelect(position, view);
    }

    public void callbackOnItemViewInvalidateListener(int position, View view) {

        if (mOnItemInvalidateLisn != null)
            mOnItemInvalidateLisn.onItemViewInvalidate(position, view);
    }
}
