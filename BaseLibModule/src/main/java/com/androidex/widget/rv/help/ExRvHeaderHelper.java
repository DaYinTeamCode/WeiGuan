package com.androidex.widget.rv.help;

import android.support.v7.widget.RecyclerView;

import com.androidex.widget.rv.attacher.ExRvOnChildAttacher;
import com.androidex.widget.rv.hf.ExRvItemViewHolderHeader;
import com.androidex.widget.rv.view.ExRecyclerView;

/**
 * ExRecyclerView header辅助类
 * Created by yihaibin on 2017/11/22.
 */

public class ExRvHeaderHelper {

    private boolean mHeaderAttached;
    private ExRvItemViewHolderHeader mHeaderViewHolder;
    private ExRvHeaderListener mLisn;
    private int mHeaderBtm;

    public ExRvHeaderHelper(ExRecyclerView erv) {

        erv.addOnChildAttachStateChangeListener(getExRvOnChildAttacher(erv));
        erv.addOnScrollListener(getScrollListener());
    }

    public void setListener(ExRvHeaderListener lisn) {

        mLisn = lisn;
    }

    public boolean isAttached() {

        return mHeaderAttached;
    }

    private ExRvOnChildAttacher getExRvOnChildAttacher(ExRecyclerView rv) {

        return new ExRvOnChildAttacher(rv) {
            @Override
            public void onExRvHeaderChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

                mHeaderAttached = true;
                mHeaderViewHolder = header;

                if (mLisn != null)
                    mLisn.onExRvHeaderAttachChanged(true);
            }

            @Override
            public void onExRvHeaderChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

                mHeaderAttached = false;
                mHeaderBtm = Integer.MIN_VALUE;
                if (mLisn != null)
                    mLisn.onExRvHeaderAttachChanged(false);
            }
        };
    }

    /**
     * item高度发生变化，也会回调onScroll
     *
     * @return
     */
    private RecyclerView.OnScrollListener getScrollListener() {

        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (mHeaderAttached) {

                    mHeaderBtm = mHeaderViewHolder.getConvertView().getBottom();
                    if (mLisn != null)
                        mLisn.onExRvHeaderShowHeight(mHeaderBtm);
                } else {

                    if (mHeaderBtm == Integer.MIN_VALUE) {

                        if (mLisn != null)
                            mLisn.onExRvHeaderShowHeight(0);
                        mHeaderBtm = 0;
                    }
                }
            }
        };
    }

    public static interface ExRvHeaderListener {

        void onExRvHeaderAttachChanged(boolean attached);

        void onExRvHeaderShowHeight(int showHeight);
    }
}
