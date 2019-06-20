package com.sjteam.weiguan.page.me.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidex.util.DensityUtil;
import com.androidex.widget.rv.decoration.ExRvDecoration;

import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.view.ExRecyclerView;
import com.sjteam.weiguan.page.me.viewholder.MainMenuItemViewHolder;

/**
 * 个人中心间隔线
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 5:03 PM
 */
public class MainUserItemDecoration extends ExRvDecoration {

    public MainUserItemDecoration() {

    }

    /**
     * recycleview每刷新一次就会回调,展现在屏幕上、滑动导致ui变动都会回调
     *
     * @param canvas 是整个 RecycleView的 Canvas,他的范围就是RecycleView
     * @param parent
     * @param state
     */
    @Override
    protected void onExRvDraw(Canvas canvas, ExRecyclerView parent, RecyclerView.State state) {

    }

    @Override
    protected void onExRvDrawOver(Canvas canvas, ExRecyclerView parent, RecyclerView.State state) {

    }

    @Override
    protected void getExRvItemOffsets(Rect outRect, View view, ExRecyclerView parent, RecyclerView.State state) {

        ExRvItemViewHolderBase viewHolder = parent.getChildViewHolder(view);
        if (viewHolder instanceof MainMenuItemViewHolder) {

            int pos = parent.getChildAdapterPosition(view);
//            if (pos == 1) {
//
//                outRect.top = DensityUtil.dip2px(20f);
//            }
        }
    }
}
