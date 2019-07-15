package com.sjteam.weiguan.stat;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.androidex.util.LogMgr;
import com.androidex.widget.rv.attacher.ExRvOnChildAttacher;
import com.androidex.widget.rv.hf.ExRvItemViewHolderFooter;
import com.androidex.widget.rv.hf.ExRvItemViewHolderHeader;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.androidex.widget.rv.view.ExRecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 7:05 PM
 */
public class StatRecyclerViewNewAttacher extends ExRvOnChildAttacher {

    private static final int HEADER_FIX_POS = -1000;
    private static final int FOOTER_FIX_POS = -1001;

    private HeaderListener mHeaderLisn;
    private DataItemListener mDataItemLisn;
    private FooterListener mFooterLisn;

    private Map<Integer, Boolean> mShowPool;
    private boolean mUiShowToUser = true;
    private boolean mEnable = true;
    private boolean mNotifyDatasetChangedCallbackShow = true;
    private boolean mHeaderRepeatShowMode;

    public StatRecyclerViewNewAttacher(ExRecyclerView erv) {

        super(erv);
        mShowPool = new HashMap();
        erv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    onExRvScrollStateChangeStateIdle();
            }

        });
    }

    /**
     * 设置header监听器
     *
     * @param lisn
     */
    public void setHeaderListener(HeaderListener lisn) {

        mHeaderLisn = lisn;
    }

    /**
     * 设置头部重复曝光模式
     *
     * @param repeatShowMode
     */
    public void setHeaderRepeatShowMode(boolean repeatShowMode) {

        mHeaderRepeatShowMode = repeatShowMode;
    }

    /**
     * 设置数据项监听器
     *
     * @param lisn
     */
    public void setDataItemListener(DataItemListener lisn) {

        mDataItemLisn = lisn;
    }

    /**
     * 设置footer监听器
     *
     * @param lisn
     */
    public void setFooterListener(FooterListener lisn) {

        mFooterLisn = lisn;
    }

    /**
     * 设置ui视图可见状态
     *
     * @param uiShowToUser
     */
    public void setUIShowToUser(boolean uiShowToUser) {

        mUiShowToUser = uiShowToUser;
    }

    /**
     * 检测UI标记，初始化默认值为true
     *
     * @return
     */
    public boolean checkUIShowToUser() {

        return mUiShowToUser;
    }

    /**
     * 是否可用状态
     *
     * @return
     */
    public boolean isEnable() {

        return mEnable;
    }

    /**
     * 设置可用状态
     *
     * @param enable
     */
    public void setEnable(boolean enable) {

        this.mEnable = enable;
    }

    /**
     * 设置列表刷新时，是否回调曝光事件
     *
     * @param callbackShow
     */
    public void setNotifyDatasetChangedCallbackShow(boolean callbackShow) {

        mNotifyDatasetChangedCallbackShow = callbackShow;
    }


    //************************ scroll state idle **************************


    /**
     * 列表停止滚动
     */
    private void onExRvScrollStateChangeStateIdle() {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "onScrollStateChangeStateIdle");

        performRecyclerViewStatShow(getRecyclerView(), false);
    }


    //************************ header attach/detach **************************


    /**
     * header attach时 保存曝光状态信息
     * 如果ui可见并且列表是停止滚动状态，则直接曝光
     * 其他情况将曝光标记为未曝光
     *
     * @param erv
     * @param header
     */
    @Override
    public void onExRvHeaderChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "header item 附加 = " + HEADER_FIX_POS + "，scrollState = " + erv.getScrollState());

        callbackHeaderAttachStatChanged(true);

        boolean showState = getShowStateOnAttached(erv);
        putShowPoolItem(HEADER_FIX_POS, showState);
        if (showState)
            callbackHeaderShow(false);
    }

    /**
     * header从列表中剥离,将曝光状态从曝光池中移除
     *
     * @param erv
     * @param header
     */
    @Override
    public void onExRvHeaderChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderHeader header) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "header item 剥离 = " + HEADER_FIX_POS + "，scrollState = " + erv.getScrollState() + ", ui show to user = " + checkUIShowToUser());

        callbackHeaderAttachStatChanged(false);

        removeShowPoolItem(HEADER_FIX_POS);
    }


    //************************ data item attach/detach **************************


    /**
     * 数据item被添加到recyclerview中
     * 将item添加至曝光池中
     * 如果列表是idle状态，则立即曝光,因为：
     * 列表第一次布局刷新或调用了notifydatasetchanged函数触发了刷新, 不会触发scroll idle的回调
     * 所以此时要立即曝光数据，不用等待至列表滚动停止时才触发
     * 如果列表是滑动状态，则放入池中，曝光状态为未曝光，等列表停止再曝光
     *
     * @param erv
     * @param viewHolder
     */
    @Override
    public void onExRvDataChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderBase viewHolder) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "data item 附加 = " + viewHolder.getDataPosition() + "， scrollState = " + erv.getScrollState() + ", ui show to user = " + checkUIShowToUser());

        int dataPos = viewHolder.getDataPosition();
        boolean showState = getShowStateOnAttached(erv);
        putShowPoolItem(dataPos, showState);
        if (showState)
            callbackDataItemShow(dataPos);
    }


    /**
     * 数据item从列表中剥离
     *
     * @param erv
     * @param viewHolder
     */
    @Override
    public void onExRvDataChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderBase viewHolder) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "data item 剥离 = " + viewHolder.getDataPosition());

        removeShowPoolItem(viewHolder.getDataPosition());
    }


    //************************ footer attach/detach **************************


    /**
     * footer attach时 保存曝光状态信息
     * 如果ui可见并且列表是停止滚动状态，则直接曝光
     * 其他情况将曝光标记为未曝光
     *
     * @param erv
     * @param footer
     */
    @Override
    public void onExRvFooterChildViewAttachedToWindow(ExRecyclerView erv, ExRvItemViewHolderFooter footer) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "footer item 附加 = " + FOOTER_FIX_POS + "，scrollState = " + erv.getScrollState() + ", ui show to user = " + checkUIShowToUser());

        boolean showState = getShowStateOnAttached(erv);
        putShowPoolItem(FOOTER_FIX_POS, showState);
        if (showState)
            callbackFooterShow();
    }

    /**
     * footer从列表中剥离
     *
     * @param erv
     * @param footer
     */
    @Override
    public void onExRvFooterChildViewDetachedToWindow(ExRecyclerView erv, ExRvItemViewHolderFooter footer) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "footer item 剥离 = " + FOOTER_FIX_POS + "，scrollState = " + erv.getScrollState());

        removeShowPoolItem(FOOTER_FIX_POS);
    }

    /**
     * 列表刷新attach时获取 item 对应的状态
     *
     * @param erv
     * @return true：ui可见 且 非滑动状态 且 支持 支持notifyDatasetChanged false：other
     */
    private boolean getShowStateOnAttached(ExRecyclerView erv) {

        return mNotifyDatasetChangedCallbackShow && checkUIShowToUser() && isEnable() && erv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE;
    }


    //************************ 曝光触发api **************************


    /**
     * 供外部触发列表曝光, 会忽略已曝光属性, 重复曝光
     * 外部回调时，RecyclerView有可能在布局，因此post一下保证布局完成能够获取到数据项起始范围
     */
    public void performRecyclerViewStatShow() {

        boolean needPost = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 || getRecyclerView().isInLayout();
        if (needPost) {

            getRecyclerView().post(new Runnable() {
                @Override
                public void run() {

                    performRecyclerViewStatShow(getRecyclerView(), true);
                }
            });
        } else {

            performRecyclerViewStatShow(getRecyclerView(), true);
        }
    }

    /**
     * 触发一次曝光，如果当前元素已曝光不会再重复曝光
     */
    public void performRecyclerViewStatShowNoForce() {

        performRecyclerViewStatShow(getRecyclerView(), false);
    }

    /**
     * 触发列表曝光回调
     *
     * @param erv
     */
    private void performRecyclerViewStatShow(ExRecyclerView erv, boolean forceShow) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "performRecyclerViewStatShow forceShow = " + forceShow + ", is show to user = " + checkUIShowToUser());

        if (checkUIShowToUser()) {

            performHeaderStatShow(erv, forceShow);
            performDataItemStatShow(erv, forceShow);
            performFooterStatShow(erv, forceShow);
        }
    }

    /**
     * 触发列表header曝光回调
     *
     * @param erv
     * @param forceShow 是否强制曝光
     */
    private void performHeaderStatShow(ExRecyclerView erv, boolean forceShow) {

        ExRvItemViewHolderHeader header = erv.getHeader();
        if (header == null)
            return;

        //在曝光池里没有数据，则不曝光
        Boolean showState = getShowPoolState(HEADER_FIX_POS);
        if (showState == null)
            return;

        //曝光池中的数据如果是未曝光过，标记为曝光状态
        if (!showState)
            putShowPoolItem(HEADER_FIX_POS, true);

        //强制曝光或者曝光池中的状态未曝光过
        if (forceShow || mHeaderRepeatShowMode || !showState)
            callbackHeaderShow(forceShow);
    }

    /**
     * 触发数据item曝光
     *
     * @param erv
     * @param forceShow
     */
    private void performDataItemStatShow(ExRecyclerView erv, boolean forceShow) {

        if(!isEnable())
            return;

        //计算数据item显示范围
        int[] rangeArray = getRecyclerViewDataItemRange(erv);
        if (rangeArray == null || rangeArray.length < 2)
            return;

        int dataItemFirstPos = rangeArray[0];
        int dataItemLastPos = rangeArray[1];

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "performDataItemStatShow start = " + dataItemFirstPos + ", last = " + dataItemLastPos + ", forceShow = " + forceShow);

        for (int i = dataItemFirstPos; i <= dataItemLastPos; i++) {

            Boolean showState = getShowPoolState(i);
            if (showState != null) {

                if (!showState)
                    putShowPoolItem(i, true);

                if (forceShow || !showState)
                    callbackDataItemShow(i);
            }
        }
    }

    /**
     * 触发footer曝光回调
     *
     * @param erv
     * @param forceShow
     */
    private void performFooterStatShow(ExRecyclerView erv, boolean forceShow) {

        ExRvItemViewHolderFooter footer = erv.getFooter();
        if (footer == null)
            return;

        //在曝光池里没有数据，则不曝光
        Boolean showState = getShowPoolState(FOOTER_FIX_POS);
        if (showState == null)
            return;

        //曝光池中的数据如果是未曝光过，标记为曝光状态
        if (!showState)
            putShowPoolItem(FOOTER_FIX_POS, true);

        //强制曝光或者曝光池中的状态是未曝光过
        if (forceShow || !showState)
            callbackFooterShow();
    }


    //************************** 曝光池 api ******************************


    /**
     * 添加待显示数据
     *
     * @param key       待显示item pos
     * @param showState 显示状态 true:已曝光 false:未曝光
     */
    private void putShowPoolItem(int key, boolean showState) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "putShowPoolItem key = " + key + ", showState = " + showState);

        mShowPool.put(key, showState);
    }

    /**
     * 将指定key移除
     *
     * @param key
     */
    private void removeShowPoolItem(int key) {

        if (LogMgr.isDebug())
            LogMgr.d(simpleTag(), "removeShowPoolItem key = " + key);

        mShowPool.remove(key);
    }

    /**
     * 获取指定key的曝光状态
     *
     * @param key
     * @return
     */
    private Boolean getShowPoolState(int key) {

        return mShowPool.get(key);
    }

    /**
     * 清空曝光缓存池
     */
    public void clearShowPool() {

        if (mShowPool == null || mShowPool.isEmpty())
            return;

        for (Map.Entry<Integer, Boolean> entry : mShowPool.entrySet()){

            entry.setValue(false);
        }
    }

    //***************************** 帮助函数 *******************************


    /**
     * 计算当前列表可见区域范围位置
     *
     * @param rv
     */
    private int[] getRecyclerViewDataItemRange(ExRecyclerView rv) {

        if (rv == null)
            return null;

        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager)
            return getLinearLayoutDataItemRange(rv, (LinearLayoutManager) layoutManager);
        else if (layoutManager instanceof StaggeredGridLayoutManager)
            return getStaggeredGridLayoutDataItemRange(rv, (StaggeredGridLayoutManager) layoutManager);
        else
            return null;
    }

    /**
     * 计算单排列表可见区域范围位置
     *
     * @param recyclerView
     * @param linearLayoutManager
     * @return
     */
    private int[] getLinearLayoutDataItemRange(ExRecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {

        return getDataItemRange(recyclerView, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
    }

    /**
     * 计算StaggeredGridLayoutManager 类型列表数据item起始范围
     *
     * @param rv
     * @param staggeredGridLayoutManager
     * @return
     */
    private int[] getStaggeredGridLayoutDataItemRange(ExRecyclerView rv, StaggeredGridLayoutManager staggeredGridLayoutManager) {

        int[] spanCount = new int[staggeredGridLayoutManager.getSpanCount()];
        return getDataItemRange(rv, findMin(staggeredGridLayoutManager.findFirstVisibleItemPositions(spanCount)),
                findMax(staggeredGridLayoutManager.findLastVisibleItemPositions(spanCount)));
    }

    /**
     * 计算列表中数据item起始范围
     *
     * @param rv
     * @param firstVisiblePos
     * @param lastVisiblePos
     * @return 如果没有数据item，或数据item未显示，返回null
     */
    private int[] getDataItemRange(ExRecyclerView rv, int firstVisiblePos, int lastVisiblePos) {

        if (rv.getDataItemCount() <= 0)
            return null;

        int dataItemFirstPos = firstVisiblePos;
        int dataItemLastPos = lastVisiblePos;
        if (rv.hasHeader()) {

            dataItemFirstPos--;
            dataItemLastPos--;
        }

        if (rv.hasFooter()) {

            if (lastVisiblePos == rv.getItemCount() - 1)
                dataItemLastPos--;
        }

        if (dataItemFirstPos < 0)
            dataItemFirstPos = 0;

        if (dataItemLastPos < 0)
            dataItemLastPos = 0;

        int[] rangeArray = new int[2];
        rangeArray[0] = dataItemFirstPos;
        rangeArray[1] = dataItemLastPos;
        return rangeArray;
    }

    /**
     * 查找数组中最小值
     *
     * @param intArray
     * @return
     */
    private int findMin(int[] intArray) {

        if (intArray == null || intArray.length == 0)
            return -1;

        int min = intArray[0];
        for (int value : intArray) {

            if (value < min)
                min = value;
        }
        return min;
    }

    /**
     * 查找数组中最大值
     *
     * @param intArray
     * @return
     */
    private int findMax(int[] intArray) {

        if (intArray == null || intArray.length == 0)
            return -1;

        int max = intArray[0];
        for (int value : intArray) {

            if (value > max)
                max = value;
        }
        return max;
    }

    /**
     * 回调header曝光
     */
    private void callbackHeaderShow(boolean forceShow) {

        if (mHeaderLisn != null)
            mHeaderLisn.onRecyclerViewHeaderStatShow(forceShow);
    }

    /**
     * 回调header attach
     *
     * @param attached
     */
    private void callbackHeaderAttachStatChanged(boolean attached) {

        if (mHeaderLisn != null)
            mHeaderLisn.onRecyclerViewHeaderAttachStatChanged(attached);
    }

    /**
     * 回调数据项曝光
     *
     * @param dataPos
     */
    private void callbackDataItemShow(int dataPos) {

        if (mDataItemLisn != null)
            mDataItemLisn.onRecyclerViewDataItemStatShow(dataPos);
    }

    /**
     * 回调footer曝光
     */
    private void callbackFooterShow() {

        if (mFooterLisn != null)
            mFooterLisn.onRecyclerViewFooterStatShow();
    }

    public String simpleTag() {

        return getClass().getSimpleName();
    }

    /**
     * header监听器
     */
    public interface HeaderListener {

        void onRecyclerViewHeaderStatShow(boolean forceShow);

        void onRecyclerViewHeaderAttachStatChanged(boolean attached);
    }

    /**
     * 数据项监听器
     */
    public interface DataItemListener {

        void onRecyclerViewDataItemStatShow(int dataPos);
    }

    /**
     * footer监听器
     */
    public interface FooterListener {

        void onRecyclerViewFooterStatShow();
    }
}
