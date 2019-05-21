package com.androidex.adapter.rv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidex.adapter.lisn.OnListItemViewClickListener;
import com.androidex.adapter.lisn.OnListItemViewLongClickListener;
import com.androidex.adapter.lisn.OnListItemViewSelectListener;

/**
 * RecycleView item view 抽象类
 * 定义了相关抽象函数
 * Created by yihaibin on 2017/2/20.
 */
public abstract class ExRvItemViewHolderBase extends RecyclerView.ViewHolder implements View.OnClickListener{

    private OnListItemViewClickListener mClickLisn;
    private OnListItemViewLongClickListener mLongClickLisn;
    private OnListItemViewSelectListener mSelectLisn;
    private View mConvertView;

    public ExRvItemViewHolderBase(View itemView) {

        super(itemView);
        mConvertView = itemView;
    }

    public ExRvItemViewHolderBase(ViewGroup viewGroup, int itemViewResId){

        this(LayoutInflater.from(viewGroup.getContext()).inflate(itemViewResId, null));
    }

    public void setEventListener(OnListItemViewClickListener clickLisn,
                                 OnListItemViewLongClickListener longClickLisn,
                                 OnListItemViewSelectListener selectLisn) {

        mClickLisn = clickLisn;
        mLongClickLisn = longClickLisn;
        mSelectLisn = selectLisn;
    }

    public void callbackInitConvertView(){

        initConvertView(mConvertView);
    }

    protected abstract void initConvertView(View convertView);

    protected View getConvertView(){

        return mConvertView;
    }

    /**
     * 默认onclick实现
     * @param v
     */
    @Override
    public void onClick(View v) {

        callbackClickListener(v, getAdapterPosition());
    }

    protected void callbackClickListener(View view, int position) {

        if (mClickLisn != null)
            mClickLisn.onListItemViewClick(view, position);
    }

    protected boolean callbackLongClickListener(View view, int position) {

        if (mClickLisn == null)
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
