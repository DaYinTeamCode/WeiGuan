package com.sjteam.weiguan.page.video.discover.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.androidex.widget.rv.adapter.ExRvAdapterMulti;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.sjteam.weiguan.page.video.discover.bean.FeedsVideoResult;
import com.sjteam.weiguan.page.video.discover.viewholder.VideoDetailViewHolder;

/**
 * 视频适配器
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 4:46 PM
 */
public class VideoDetailAdapter extends ExRvAdapterMulti<Object> {

    private Context context;

    public VideoDetailAdapter(Context context) {

        this.context = context;
    }

    @Override
    public ExRvItemViewHolderBase onCreateDataViewHolder(ViewGroup parent, int viewType) {

        return new VideoDetailViewHolder(parent);
    }

    @Override
    public void onBindDataViewHolder(ExRvItemViewHolderBase holder, int dataPos) {

        Object object = getDataItem(dataPos);
        if (holder instanceof VideoDetailViewHolder) {

            ((VideoDetailViewHolder) holder).invalidateView((FeedsVideoResult) object);
        }
    }
}