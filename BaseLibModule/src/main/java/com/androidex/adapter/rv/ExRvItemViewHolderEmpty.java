package com.androidex.adapter.rv;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecycleView itemview 的空布局实现。
 * 可以用来填充itemType的默认布局
 * Created by yihaibin on 2017/2/20.
 */
public class ExRvItemViewHolderEmpty extends ExRvItemViewHolderBase {

    private ExRvItemViewHolderEmpty(ViewGroup parent, int orientation) {

        super(getEmptyView(parent, orientation));
    }

    public static ExRvItemViewHolderEmpty newHoriInstance(ViewGroup parent){

        return new ExRvItemViewHolderEmpty(parent, 0);
    }

    public static ExRvItemViewHolderEmpty newVertInstance(ViewGroup parent){

        return new ExRvItemViewHolderEmpty(parent, 1);
    }

    private static View getEmptyView(ViewGroup parent, int orientation){

        View view = new View(parent.getContext());

        //0表示水平朝向，1表示垂直朝向
        if(orientation == 0)
            view.setLayoutParams(new RecyclerView.LayoutParams(50, RecyclerView.LayoutParams.WRAP_CONTENT));
        else
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 100));

        return view;
    }

    @Override
    protected void initConvertView(View convertView) {

        //nothing
    }
}
