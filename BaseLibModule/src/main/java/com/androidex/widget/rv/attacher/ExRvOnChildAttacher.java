package com.androidex.widget.rv.attacher;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidex.widget.rv.hf.ExRvItemViewHolderFooter;
import com.androidex.widget.rv.hf.ExRvItemViewHolderHeader;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.vh.ExRvItemViewHolderUtil;
import com.androidex.widget.rv.view.ExRecyclerView;

/**
 * Created by yihaibin on 2017/6/10.
 */

public class ExRvOnChildAttacher implements RecyclerView.OnChildAttachStateChangeListener {

    private ExRecyclerView mErv;

    public ExRvOnChildAttacher(ExRecyclerView erv) {

        mErv = erv;
    }

    protected ExRecyclerView getRecyclerView() {

        return mErv;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

        ExRvItemViewHolderBase holder = mErv.getChildViewHolder(view);
        if (ExRvItemViewHolderUtil.isHeader(holder))
            onExRvHeaderChildViewAttachedToWindow(mErv, (ExRvItemViewHolderHeader) holder);
        else if (ExRvItemViewHolderUtil.isFooter(holder))
            onExRvFooterChildViewAttachedToWindow(mErv, (ExRvItemViewHolderFooter) holder);
        else
            onExRvDataChildViewAttachedToWindow(mErv, holder);
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

        ExRvItemViewHolderBase holder = mErv.getChildViewHolder(view);
        if (ExRvItemViewHolderUtil.isHeader(holder))
            onExRvHeaderChildViewDetachedToWindow(mErv, (ExRvItemViewHolderHeader) holder);
        else if (ExRvItemViewHolderUtil.isFooter(holder))
            onExRvFooterChildViewDetachedToWindow(mErv, (ExRvItemViewHolderFooter) holder);
        else
            onExRvDataChildViewDetachedToWindow(mErv, holder);
    }


    public void onExRvHeaderChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

    }

    public void onExRvHeaderChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

    }


    public void onExRvDataChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderBase viewHolder) {

    }

    public void onExRvDataChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderBase viewHolder) {

    }


    public void onExRvFooterChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderFooter footer) {

    }

    public void onExRvFooterChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderFooter footer) {

    }

}
