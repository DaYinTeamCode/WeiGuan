package com.androidex.widget.rv.hf;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.androidex.widget.rv.lisn.IStaggerFullSpan;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.vh.ExRvItemViewHolderUtil;
import com.androidex.widget.rv.view.ExRecyclerView;

/**
 * StaggedGridLayoutManager header footer full span 处理
 * Created by yihaibin on 2017/6/9.
 */
public class ExRvHfStaggeredAttacher implements RecyclerView.OnChildAttachStateChangeListener {

    private ExRecyclerView mRv;

    public ExRvHfStaggeredAttacher(ExRecyclerView rv) {

        mRv = rv;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

        ExRvItemViewHolderBase holder = mRv.getChildViewHolder(view);
        if (ExRvItemViewHolderUtil.isHeaderOrFooter(holder) || holder instanceof IStaggerFullSpan) {

            StaggeredGridLayoutManager.LayoutParams sglm = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            sglm.setFullSpan(true);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
