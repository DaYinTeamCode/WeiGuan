package com.sjteam.weiguan.page.video.adapter;

import android.view.ViewGroup;

import com.androidex.widget.rv.adapter.ExRvAdapterMulti;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.vh.ExRvItemViewHolderEmpty;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoResult;
import com.sjteam.weiguan.page.video.viewholder.VideoDcViewHolder;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019-08-10 11:50
 */
public class MainVideoAdapter extends ExRvAdapterMulti<Object> {

    public static final int ITEM_TYPE_VIDEO = 1;
    public static final int ITEM_TYPE_NONE = 2;

    private int mCardWidth;

    public void setCardWidth(int cardWidth) {

        mCardWidth = cardWidth;
    }

    @Override
    public int getDataItemViewType(int dataPos) {

        Object obj = getDataItem(dataPos);

        if (obj instanceof FeedsVideoResult) {

            return ITEM_TYPE_VIDEO;
        } else {

            return ITEM_TYPE_NONE;
        }
    }

    @Override
    public ExRvItemViewHolderBase onCreateDataViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case ITEM_TYPE_VIDEO:

                VideoDcViewHolder videoDcViewHolder = new VideoDcViewHolder(parent, mCardWidth);
                return videoDcViewHolder;
            case ITEM_TYPE_NONE:
            default:
                return ExRvItemViewHolderEmpty.newVertInstance(parent);
        }
    }

    @Override
    public void onBindDataViewHolder(ExRvItemViewHolderBase holder, int position) {

        if (holder instanceof VideoDcViewHolder) {

            ((VideoDcViewHolder) holder).invalidateView((FeedsVideoResult) getDataItem(position));
        }
    }
}
