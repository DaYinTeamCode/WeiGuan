package com.androidex.widget.rv.vh;

import static com.androidex.widget.rv.vh.ExRvItemViewHolderConstant.ITEM_VIEW_TYPE_FOOTER;
import static com.androidex.widget.rv.vh.ExRvItemViewHolderConstant.ITEM_VIEW_TYPE_HEADER;

/**
 * Created by yihaibin on 2017/6/11.
 */

public class ExRvItemViewHolderUtil {

    public static boolean isHeaderOrFooter(ExRvItemViewHolderBase viewHolder){

        return isHeader(viewHolder) || isFooter(viewHolder);
    }

    public static boolean isHeader(ExRvItemViewHolderBase viewHolder){

        if(viewHolder == null)
            return false;
        else
            return viewHolder.getItemViewType() == ITEM_VIEW_TYPE_HEADER;
    }

    public static boolean isFooter(ExRvItemViewHolderBase viewHolder){

        if(viewHolder == null)
            return false;
        else
            return viewHolder.getItemViewType() == ITEM_VIEW_TYPE_FOOTER;
    }
}
