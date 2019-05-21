package com.androidex.adapter.rv;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.androidex.adapter.lisn.OnListItemViewClickListener;
import com.androidex.adapter.lisn.OnListItemViewLongClickListener;
import com.androidex.adapter.lisn.OnListItemViewSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView 适配器 基类
 * Created by yihaibin on 2017/2/20.
 */
public abstract class ExRvAdapterBase<T, K extends ExRvItemViewHolderBase> extends RecyclerView.Adapter<ExRvItemViewHolderBase> {

    private List<T> mData;
    private OnListItemViewClickListener mClickLisn;
    private OnListItemViewLongClickListener mLongClickLisn;
    private OnListItemViewSelectListener mSelectLisn;

    protected ExRvAdapterBase() {}

    protected ExRvAdapterBase(List<T> data) {

        setData(data);
    }

    @Override
    public int getItemCount() {

        return mData == null ? 0 : mData.size();
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {

        K vh = onCreateRvViewHolder(parent, viewType);
        vh.setEventListener(mClickLisn, mLongClickLisn, mSelectLisn);
        vh.callbackInitConvertView();
        return vh;
    }

    public abstract K onCreateRvViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ExRvItemViewHolderBase holder, int position) {

        onBindRvViewHolder((K) holder, position);
    }

    public abstract void onBindRvViewHolder(K holder, int position);



    /*
        以下是自定义函数*********************************************
     */



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
    public boolean isEmpty() {

        return getItemCount() <= 0;
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

        return getItemCount() - 1;
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
            return getItemViewType(getItemCount() - 1);
    }

    /**
     * 检查pos是否越界
     * @param position
     * @return
     */
    public boolean checkPosition(int position){

        return position >= 0 && position < getItemCount();
    }

    /**
     * 检查插入操作pos是否越界
     * @param position
     * @return
     */
    public boolean checkInsertPosition(int position){

        return position >= 0 && position <= getItemCount();
    }

    private void initDataIfNull(){

        if(mData == null)
            mData = new ArrayList<T>();
    }

}
