package com.androidex.adapter.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidex.adapter.lisn.OnListItemViewClickListener;
import com.androidex.adapter.lisn.OnListItemViewLongClickListener;
import com.androidex.adapter.lisn.OnListItemViewSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表适配器：适用于ListView、GridView
 * Created by yihaibin on 2017/2/8.
 */
public abstract class ExListAdapterBase<T, K extends ExListItemViewHolder> extends BaseAdapter {

    private List<T> mData;
    private OnListItemViewClickListener mClickLisn;
    private OnListItemViewLongClickListener mLongClickLisn;
    private OnListItemViewSelectListener mSelectLisn;


    protected ExListAdapterBase() {}

    protected ExListAdapterBase(List<T> data) {

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
    public int getViewTypeCount() {

        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public T getItem(int position) {

        return checkPosition(position) ? mData.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        K viewHolder = null;
        if (convertView == null) {

            viewHolder = onCreateViewHolder(parent, getItemViewType(position));

            int resId = viewHolder.getConvertViewRid();
            if (resId == 0)
                convertView = viewHolder.getConvertView(parent);
            else
                convertView = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);

            viewHolder.setPosition(position);
            viewHolder.setEventListener(mClickLisn, mLongClickLisn, mSelectLisn);
            viewHolder.initConvertView(convertView);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (K) convertView.getTag();
            viewHolder.setPosition(position);
        }

        onBindViewHolder(viewHolder, position);

        return convertView;
    }

    public abstract K onCreateViewHolder(ViewGroup parent, int viewType);
    public abstract void onBindViewHolder(K holder, int position);



    /*
        自定义函数*********************************************
     */



    public List<T> getData() {

        return mData;
    }

    public void setData(List<T> data) {

        mData = data;
    }

    public void add(T item) {

        if(item != null){

            initDataIfNull();
            mData.add(item);
        }
    }

    public void add(int position, T item) {

        if(item != null){

            initDataIfNull();
            if(checkInsertPosition(position))
                mData.add(position, item);
        }
    }

    public void addAll(List<? extends T> data) {

        if (data != null && data.size() > 0){

            initDataIfNull();
            mData.addAll(data);
        }
    }

    public void addAll(int position, List<? extends T> data) {

        if(data != null && data.size() > 0 &&
           checkInsertPosition(position)){

            initDataIfNull();
            mData.addAll(position, data);
        }
    }

    public boolean remove(T item) {

        if (!isEmpty() && item != null)
            return mData.remove(item);
        else
            return false;
    }

    public T remove(int position) {

        if(!isEmpty() && checkPosition(position))
            return mData.remove(position);
        else
            return null;
    }

    /**
     * 清空数据
     */
    public void clear(){

        if(mData != null)
            mData.clear();
    }

    /**
     * 判断数据是否为空
     * @return
     */
    @Override
    public boolean isEmpty() {

        return getCount() <= 0;
    }

    /**
     * 设置点击监听器
     * @param lisn
     */
    public void setOnListItemViewClickListener(OnListItemViewClickListener lisn){

        mClickLisn = lisn;
    }

    /**
     * 设置长按监听器
     * @param lisn
     */
    public void setOnListItemViewLongClickListener(OnListItemViewLongClickListener lisn){

        mLongClickLisn = lisn;
    }

    /**
     * 设置选择监听器
     * @param lisn
     */
    public void setOnListItemViewSelectListener(OnListItemViewSelectListener lisn){

        mSelectLisn = lisn;
    }

    /**
     * 获取最后一item的position
     * @return 如果集合为空返回－1
     */
    public int getLastIitemPosition(){

        return getCount() - 1;
    }

    /**
     * 获取集合中指定type的第一个position
     * @param type
     * @return
     */
    public int findItemViewTypePosition(int type){

        if(isEmpty())
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

        if(isEmpty())
            return -1;
        else
            return getItemViewType(getCount() - 1);
    }

    /**
     * 检查pos是否越界
     * @param position
     * @return
     */
    public boolean checkPosition(int position){

        return position >= 0 && position < getCount();
    }

    /**
     * 检查插入操作pos是否越界
     * @param position
     * @return
     */
    public boolean checkInsertPosition(int position){

        return position >= 0 && position <= getCount();
    }

    private void initDataIfNull(){

        if(mData == null)
            mData = new ArrayList<T>();
    }
}
