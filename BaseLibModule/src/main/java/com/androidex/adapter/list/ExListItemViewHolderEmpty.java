package com.androidex.adapter.list;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 列表适配器 ViewHolder 默认空item类
 * Created by yihaibin on 2017/2/8.
 */
public class ExListItemViewHolderEmpty extends ExListItemViewHolderBase {

    @Override
    public int getConvertViewRid() {

        return 0;
    }

    @Override
    public View getConvertView(ViewGroup parent) {

        //默认的 item view 如果 没有高度，会导致不断的加载更多，所以这里随便设定了个高度值100
        View v = new View(parent.getContext());
        v.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 100));
        return v;
    }

    @Override
    public void initConvertView(View convertView) {

    }
}
