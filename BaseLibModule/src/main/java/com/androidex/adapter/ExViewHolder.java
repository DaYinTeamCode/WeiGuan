package com.androidex.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface ExViewHolder {

    void setOnItemViewClickListener(OnItemViewClickListener lisn);

    void setOnItemViewLongClickListener(OnItemViewLongClickListener lisn);

    int getConvertViewRid();

    View getConvertView(ViewGroup parent);

    void initConvertView(View convertView, int position);

    void invalidateConvertView(Object obj, int position);
}