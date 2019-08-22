package com.sjteam.weiguan.page.video.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidex.util.DensityUtil;
import com.androidex.widget.rv.decoration.ExRvDecoration;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.view.ExRecyclerView;
import com.sjteam.weiguan.constants.DimenConstant;
import com.sjteam.weiguan.page.video.viewholder.VideoDcViewHolder;

/**
 * 视频间隔线
 *
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2018/8/3 下午3:34
 */
public class VideoDcCardGridDecoration extends ExRvDecoration {

    public static final int ITEM_VERTICAL_SPLIT = DensityUtil.dip2px(7f);
    public static final int ITEM_DECORATION_OUTSIDE_SPLIT = DensityUtil.dip2px(20f);
    public static final int ITEM_DECORATION_BETWEEN_SPLIT = DensityUtil.dip2px(7f);
    public static final int ITEM_WIDTH = (DimenConstant.SCREEN_WIDTH - ITEM_DECORATION_OUTSIDE_SPLIT - ITEM_DECORATION_BETWEEN_SPLIT) / 2;

    @Override
    protected void getExRvItemOffsets(Rect outRect, View view, ExRecyclerView parent, RecyclerView.State state) {

        ExRvItemViewHolderBase viewHolder = parent.getChildViewHolder(view);
        if (viewHolder instanceof VideoDcViewHolder) {

            outRect.top = ITEM_VERTICAL_SPLIT;
            GridLayoutManager.LayoutParams sglm = (GridLayoutManager.LayoutParams) viewHolder.getConvertView().getLayoutParams();
            int spanIndex = sglm.getSpanIndex();

            if (spanIndex == 0) {

                outRect.left = ITEM_DECORATION_OUTSIDE_SPLIT / 2;
                outRect.right = ITEM_DECORATION_BETWEEN_SPLIT / 2;
            } else {

                outRect.left = ITEM_DECORATION_BETWEEN_SPLIT / 2;
                outRect.right = ITEM_DECORATION_OUTSIDE_SPLIT / 2;
            }
        }
    }
}
