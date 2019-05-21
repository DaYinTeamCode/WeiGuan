package com.androidex.widget.rv.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidex.widget.rv.hf.ExRvItemViewHolderFooter;
import com.androidex.widget.rv.hf.ExRvItemViewHolderHeader;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.vh.ExRvItemViewHolderUtil;
import com.androidex.widget.rv.view.ExRecyclerView;

/**
 * ExRecyclerView 分割器基础类
 * Created by yihaibin on 2017/6/9.
 */

public class ExRvDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        onExRvDraw(c, (ExRecyclerView) parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        onExRvDrawOver(c, (ExRecyclerView) parent, state);
    }

    protected void onExRvDraw(Canvas c, ExRecyclerView parent, RecyclerView.State state) {

        //nothing
    }

    protected void onExRvDrawOver(Canvas c, ExRecyclerView parent, RecyclerView.State state) {

        //nothing
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        getExRvItemOffsets(outRect, view, (ExRecyclerView) parent, state);
    }

    protected void getExRvItemOffsets(Rect outRect, View view, ExRecyclerView parent, RecyclerView.State state) {

        ExRvItemViewHolderBase viewHolder = parent.getChildViewHolder(view);
        if(ExRvItemViewHolderUtil.isHeader(viewHolder))
            getExRvHeaderItemOffsets(outRect, (ExRvItemViewHolderHeader)viewHolder, parent, state);
        else if(ExRvItemViewHolderUtil.isFooter(viewHolder))
            getExRvFooterItemOffsets(outRect, (ExRvItemViewHolderFooter)viewHolder, parent, state);
        else
            getExRvDataItemOffsets(outRect, viewHolder, parent, state);
    }

    protected void getExRvHeaderItemOffsets(Rect outRect, ExRvItemViewHolderHeader header, ExRecyclerView parent, RecyclerView.State state){

        //nothing
    }

    protected void getExRvDataItemOffsets(Rect outRect, ExRvItemViewHolderBase viewHolder, ExRecyclerView parent, RecyclerView.State state) {

        //nothing
    }

    protected void getExRvFooterItemOffsets(Rect outRect, ExRvItemViewHolderFooter footer, ExRecyclerView parent, RecyclerView.State state){

        //nothing
    }
}
