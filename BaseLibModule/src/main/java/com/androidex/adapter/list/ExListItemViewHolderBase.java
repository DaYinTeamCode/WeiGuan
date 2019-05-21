package com.androidex.adapter.list;

import android.view.View;
import android.view.ViewGroup;

import com.androidex.adapter.lisn.OnListItemViewClickListener;
import com.androidex.adapter.lisn.OnListItemViewLongClickListener;
import com.androidex.adapter.lisn.OnListItemViewSelectListener;

/**
 * 列表适配器 ViewHolder 接口 抽象基类
 * Created by yihaibin on 2017/2/8.
 */
public abstract class ExListItemViewHolderBase implements ExListItemViewHolder {

    private int mOldPosition = -1;//上一次pos
    private int mCurPosition = -1;//当前pos
    private OnListItemViewClickListener mClickLisn;
    private OnListItemViewLongClickListener mLongClickLisn;
    private OnListItemViewSelectListener mSelectLisn;

    @Override
    public void setEventListener(OnListItemViewClickListener clickLisn,
                                 OnListItemViewLongClickListener longClickLisn,
                                 OnListItemViewSelectListener selectLisn) {

        mClickLisn = clickLisn;
        mLongClickLisn = longClickLisn;
        mSelectLisn = selectLisn;
    }

    @Override
    public void setPosition(int position) {

        mOldPosition = mCurPosition;
        mCurPosition = position;
    }

    @Override
    public View getConvertView(ViewGroup parent) {

        return null;
    }

    /**
     * 获取当前viewholder pos
     * @return
     */
    protected int getCurPosition() {

        return mCurPosition;
    }

    /**
     * 获取上一次viewholder pos
     * @return
     */
    protected int getOldPosition() {

        return mOldPosition;
    }

    protected void callbackClickListener(View view, int position) {

        if (mClickLisn != null)
            mClickLisn.onListItemViewClick(view, position);
    }

    protected boolean callbackLongClickListener(View view, int position) {

        if (mLongClickLisn == null)
            return false;
        else
            return mLongClickLisn.onListItemViewLongClick(view, position);
    }

    protected boolean callbackSelectListener(View view, int position) {

        if (mSelectLisn == null)
            return false;
        else
            return mSelectLisn.onListItemViewSelect(view, position);
    }
}
