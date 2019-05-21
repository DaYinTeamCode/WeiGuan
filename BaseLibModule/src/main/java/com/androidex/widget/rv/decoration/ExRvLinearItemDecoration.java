package com.androidex.widget.rv.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.view.ExRecyclerView;

import static com.androidex.widget.rv.vh.ExRvItemViewHolderConstant.ITEM_VIEW_TYPE_FOOTER;
import static com.androidex.widget.rv.vh.ExRvItemViewHolderConstant.ITEM_VIEW_TYPE_HEADER;

/**
 * //未完成，还不能用!!!
 * 扩展的RecycleView item 装饰器
 * Created by yihaibin on 2017/5/1.
 */
public class ExRvLinearItemDecoration extends RecyclerView.ItemDecoration {

    private int mOrientation = OrientationHelper.VERTICAL;
    private int mStartDividerLength, mEndDividerLength;
    private int mDataItemDividerLength, mDataItemDividerSplitLength, mDataItemSideDividerLength;
    private Drawable mDataItemDividerDrawable;
    private boolean mFirstDataItemTopDividerEnable, mLastDataItemBtmDividerEnable;

    public ExRvLinearItemDecoration(int orientation) {

        mOrientation = orientation;
    }

    public void setStartEndDividerLength(int length) {

        setStartDividerLength(length);
        setEndDividerLength(length);
    }

    public void setStartDividerLength(int length){

        mStartDividerLength = length;
    }

    public void setEndDividerLength(int length){

        mEndDividerLength = length;
    }

    public void setDataItemDividerLength(int length) {

        mDataItemDividerLength = length;
    }

    public void setDataItemSideDividerLength(int length){

        mDataItemSideDividerLength = length;
    }

    public void setDataItemDividerSplitDrawable(Drawable drawable) {

        mDataItemDividerDrawable = drawable;
        mDataItemDividerSplitLength = getDividerLengthByDrawable(drawable, mOrientation);
    }

    public void setFirstDataItemTopDividerEnable(boolean enable){

        mFirstDataItemTopDividerEnable = enable;
    }

    public void setLastDataItemBtmDividerEnable(boolean enable){

        mLastDataItemBtmDividerEnable = enable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        onDraw(c, (ExRecyclerView) parent, state);
    }

    /**
     * recycleview每刷新一次就会回调,展现在屏幕上、滑动导致ui变动都会回调
     *
     * @param c      是整个 RecycleView的 Canvas,他的范围就是RecycleView
     * @param parent
     * @param state
     */
    private void onDraw(Canvas c, ExRecyclerView parent, RecyclerView.State state) {

        if (mDataItemDividerDrawable == null)
            return;

        final int childCount = parent.getChildCount();
        int adapterPos;
        for (int i = 0; i < childCount; i++) {

            final View child = parent.getChildAt(i);
            ExRvItemViewHolderBase vh = parent.getChildViewHolder(child);
            if (vh == null)
                continue;

            adapterPos = vh.getAdapterPosition();
            if (adapterPos == 0) {

                setFirstItemDividerSplit(c, child, vh);

            } else if (adapterPos == parent.getItemCount() - 1) {

                setLastItemDividerSplit(c, child, vh);

            } else {

                setMidItemDividerSplit(c, child, vh);
            }
        }
    }



    /**
     * 回调时机：当item要展现在屏幕中，会回调该函数确定四周间距偏移量
     * 比 onDraw onDrawOver 要先回调
     *
     * @param outRect item 四个方向的间距偏移量对象：top：表示上间距, left :
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        getItemOffsets(outRect, view, (ExRecyclerView) parent);
    }

    private void getItemOffsets(Rect outRect, View view, ExRecyclerView parent) {

        ExRvItemViewHolderBase vh = parent.getChildViewHolder(view);
        if (vh == null)
            return;

        switch (vh.getItemViewType()){
            case ITEM_VIEW_TYPE_HEADER:
                setItemHeaderDivider(outRect);
                break;
            case ITEM_VIEW_TYPE_FOOTER:
                setItemFooterDivider(outRect);
                break;
            default:
                if(parent.getDataItemCount() == 1)
                    setItemDataOnlyOneDivider(outRect);
                else
                    setItemDataDivider(outRect, vh.getDataPosition() == 0, vh.getDataPosition() == parent.getDataItemCount() - 1);
                break;
        }

        int adapterPos = vh.getAdapterPosition();
        if (adapterPos == 0) {

            setFirstItemOffsets(outRect, vh);

        } else if (adapterPos == parent.getItemCount() - 1) {

            setLastItemOffsets(outRect, vh);

        } else {

            setMidItemOffsets(outRect, vh, vh.getDataPosition() == parent.getDataItemCount() - 1);
        }
    }

    private void setFirstItemDividerSplit(Canvas c, View child, ExRvItemViewHolderBase viewHolder) {

//        if (viewHolder.getItemViewType() == viewHolder.ITEM_VIEW_TYPE_HEADER && mHeaderDividerEnable)
//            drawableItemDividerSplitBottom(c, child, mDataItemDividerLength, mDataItemDividerSplitLength, mOrientation);
    }

    private void setMidItemDividerSplit(Canvas c, View child, ExRvItemViewHolderBase viewHolder) {

        if (viewHolder.getDataPosition() != 0)
            drawableItemDividerSplitTop(c, child, mDataItemDividerLength, mDataItemDividerSplitLength, mOrientation);
    }

    private void setLastItemDividerSplit(Canvas c, View child, ExRvItemViewHolderBase vh) {

//        if (vh.getItemViewType() == vh.ITEM_VIEW_TYPE_FOOTER && mFooterDividerEnable)
//            drawableItemDividerSplitTop(c, child, mDataItemDividerLength, mDataItemDividerSplitLength, mOrientation);
    }

    private void drawableItemDividerSplitTop(Canvas c, View child, int dividerLength, int dividerSplitLength, int orientation) {

        if (orientation == OrientationHelper.HORIZONTAL) {

            int splitLeft = child.getLeft() - (dividerLength / 2);
            mDataItemDividerDrawable.setBounds(splitLeft - dividerSplitLength, 0, splitLeft, c.getHeight());
        } else {

            int splitBtm = child.getTop() - (dividerLength / 2);
            mDataItemDividerDrawable.setBounds(0, splitBtm - dividerSplitLength, c.getWidth(), splitBtm);
            ;
        }
        mDataItemDividerDrawable.draw(c);
    }

    private void drawableItemDividerSplitBottom(Canvas c, View child, int dividerLength, int dividerSplitLength, int orientation) {

        if (orientation == OrientationHelper.HORIZONTAL) {

            int splitLeft = child.getRight() + (dividerLength / 2);
            mDataItemDividerDrawable.setBounds(splitLeft + dividerSplitLength, 0, splitLeft + dividerSplitLength, c.getHeight());
        } else {

            int splitBtm = child.getBottom() + (dividerLength / 2);
            mDataItemDividerDrawable.setBounds(0, splitBtm, c.getWidth(), splitBtm + dividerSplitLength);
        }
        mDataItemDividerDrawable.draw(c);
    }

    private void setItemHeaderDivider(Rect outRect){

        if (mStartDividerLength > 0)
            setItemRectTopOrLeft(outRect, mOrientation, mStartDividerLength);
    }

    private void setItemFooterDivider(Rect outRect){

        if (mEndDividerLength > 0)
            setItemRectBtmOrRight(outRect, mOrientation, mEndDividerLength);
    }

    private void setItemDataOnlyOneDivider(Rect outRect) {


    }

    private void setItemDataDivider(Rect outRect, int adapterPos, int dataPos){

    }

    private void setItemDataDivider(Rect outRect, boolean isFirst, boolean isLast){

        if(isFirst && mFirstDataItemTopDividerEnable){

            int topOffset = mStartDividerLength;
            if(mFirstDataItemTopDividerEnable)
                topOffset = topOffset + mDataItemDividerLength + mDataItemDividerSplitLength;

            if(topOffset > 0)
                setItemRectTopOrLeft(outRect, mOrientation, topOffset);

        }else if(isLast){

        }else{

        }
    }

    private void setFirstItemOffsets(Rect outRect, ExRvItemViewHolderBase viewHolder) {

        if (viewHolder.getItemViewType() == ITEM_VIEW_TYPE_HEADER ||
            viewHolder.getItemViewType() == ITEM_VIEW_TYPE_FOOTER) {

            if (mStartDividerLength > 0)
                setItemRectTopOrLeft(outRect, mOrientation, mStartDividerLength);

        } else {

            //data item

            int topOffset = mStartDividerLength;
            if(mFirstDataItemTopDividerEnable)
                topOffset = topOffset + mDataItemDividerLength + mDataItemDividerSplitLength;

            if(topOffset > 0)
                setItemRectTopOrLeft(outRect, mOrientation, topOffset);

            if(mDataItemSideDividerLength > 0)
                setItemRectSide(outRect, mOrientation, mDataItemSideDividerLength);
        }
    }

    private void setMidItemOffsets(Rect outRect, ExRvItemViewHolderBase vh, boolean isLastData) {

        if (mDataItemDividerLength + mDataItemDividerSplitLength > 0 && vh.getDataPosition() != 0)
            setItemRectTopOrLeft(outRect, mOrientation, mDataItemDividerLength + mDataItemDividerSplitLength);

        if(isLastData && mLastDataItemBtmDividerEnable)
            setItemRectBtmOrRight(outRect, mOrientation, mDataItemDividerLength + mDataItemDividerSplitLength);

        if(mDataItemSideDividerLength > 0)
            setItemRectSide(outRect, mOrientation, mDataItemSideDividerLength);


    }

    private void setLastItemOffsets(Rect outRect, ExRvItemViewHolderBase viewHolder) {

        if(viewHolder.getItemViewType() == ITEM_VIEW_TYPE_FOOTER){

            if (mEndDividerLength > 0)
                setItemRectBtmOrRight(outRect, mOrientation, mEndDividerLength);

        }else{

            //data item

            int offset = mDataItemDividerLength + mDataItemDividerSplitLength;
            if(offset > 0)
                setItemRectTopOrLeft(outRect, mOrientation, offset);

            offset = mEndDividerLength;
            if(mLastDataItemBtmDividerEnable)
                offset = offset + mDataItemDividerLength + mDataItemDividerSplitLength;

            if(offset > 0)
                setItemRectBtmOrRight(outRect, mOrientation, offset);

            if(mDataItemSideDividerLength > 0)
                setItemRectSide(outRect, mOrientation, mDataItemSideDividerLength);
        }
    }

    private void setItemRectTopOrLeft(Rect outRect, int orientation, int length) {

        if (orientation == OrientationHelper.HORIZONTAL)
            outRect.left = length;
        else
            outRect.top = length;
    }

    private void setItemRectBtmOrRight(Rect outRect, int orientation, int length) {

        if (orientation == OrientationHelper.HORIZONTAL)
            outRect.right = length;
        else
            outRect.bottom = length;
    }

    private void setItemRectSide(Rect outRect, int orientation, int length){

        if (orientation == OrientationHelper.HORIZONTAL){

            outRect.top = length;
            outRect.bottom = length;
        }else{

            outRect.left = length;
            outRect.right = length;
        }
    }

    private int getDividerLengthByDrawable(Drawable drawable, int orientation){

        if(drawable == null)
            return 0;

        if (orientation == OrientationHelper.HORIZONTAL)
            return drawable.getIntrinsicWidth();
        else
            return drawable.getIntrinsicHeight();
    }
}
