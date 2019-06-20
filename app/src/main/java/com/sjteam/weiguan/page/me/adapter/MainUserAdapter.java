package com.sjteam.weiguan.page.me.adapter;

import android.view.ViewGroup;

import com.androidex.widget.rv.adapter.ExRvAdapterMulti;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.sjteam.weiguan.page.me.bean.UserItemSet;
import com.sjteam.weiguan.page.me.viewholder.MainMenuItemViewHolder;

/**
 * 用户中心 适配器
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 3:14 PM
 */
public class MainUserAdapter extends ExRvAdapterMulti<Object> {

    public static final int TYPE_SET_ITEM = 0;

    public MainUserAdapter() {

    }

    @Override
    public int getDataItemViewType(int dataPos) {

        return TYPE_SET_ITEM;
    }

    @Override
    public ExRvItemViewHolderBase onCreateDataViewHolder(ViewGroup parent, int viewType) {

        return new MainMenuItemViewHolder(parent);
    }

    @Override
    public void onBindDataViewHolder(ExRvItemViewHolderBase holder, int dataPos) {

        if (holder instanceof MainMenuItemViewHolder) {

            ((MainMenuItemViewHolder) holder).invalidataContentView((UserItemSet) getDataItem(dataPos));
        }
    }
}
