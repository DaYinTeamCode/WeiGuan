package com.androidex.adapter.list;

import android.view.View;
import android.view.ViewGroup;

import com.androidex.adapter.lisn.OnListItemViewClickListener;
import com.androidex.adapter.lisn.OnListItemViewLongClickListener;
import com.androidex.adapter.lisn.OnListItemViewSelectListener;

/**
 * 列表适配器 ViewHolder 接口
 * Created by yihaibin on 2017/2/8.
 */
public interface ExListItemViewHolder {

    void setEventListener(OnListItemViewClickListener clickLisn,
                          OnListItemViewLongClickListener longClickLisn,
                          OnListItemViewSelectListener selectLisn);

    void setPosition(int position);

    int getConvertViewRid();

    View getConvertView(ViewGroup parent);

    void initConvertView(View convertView);
}
