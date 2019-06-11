package com.jzyd.lib.refresh.sqkbswipe;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 适配RecyclerView
 * 暂不支持瀑布流布局
 * Created by yihaibin on 2017/11/15.
 */

public class SqkbSwipeRecyclerViewScrollUpCallback implements SqkbSwipeRefreshLayout.OnChildScrollUpCallback {

    @Override
    public boolean canChildScrollUp(SqkbSwipeRefreshLayout parent, @Nullable View child) {

        if (child instanceof RecyclerView) {

            RecyclerView erv = (RecyclerView) child;
            RecyclerView.LayoutManager lm = erv.getLayoutManager();
            if (lm instanceof LinearLayoutManager) {

                if (erv.getChildCount() > 0) {

                    child = erv.getChildAt(0);
                    if (child != null && child.getHeight() == 0)
                        return !(((LinearLayoutManager) lm).findFirstVisibleItemPosition() == 1);
                }
            }
        }

        return parent.canChildScrollUp(false);
    }
}
