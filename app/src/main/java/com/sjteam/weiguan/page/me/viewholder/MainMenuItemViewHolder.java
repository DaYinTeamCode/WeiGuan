package com.sjteam.weiguan.page.me.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.me.bean.UserItemSet;

/**
 * 个人中心ViewHolder
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 6:11 PM
 */
public class MainMenuItemViewHolder extends ExRvItemViewHolderBase {

    private TextView mTvTitle;

    public MainMenuItemViewHolder(ViewGroup viewGroup) {

        super(viewGroup, R.layout.page_main_user_item_vh);
    }

    @Override
    protected void initConvertView(View convertView) {

        convertView.setOnClickListener(this);

        mTvTitle = convertView.findViewById(R.id.tvTitle);
    }

    public void invalidataContentView(UserItemSet itemSet) {

        if (itemSet == null) {

            return;
        }

        mTvTitle.setText(itemSet.getTitle());
    }
}