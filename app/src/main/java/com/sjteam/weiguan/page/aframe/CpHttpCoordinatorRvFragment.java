package com.sjteam.weiguan.page.aframe;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidex.util.CollectionUtil;
import com.androidex.view.ExDecorView;
import com.androidex.widget.rv.adapter.ExRvAdapterBase;
import com.androidex.widget.rv.view.ExRecyclerView;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;

import java.util.List;

/**
 * 该Fragment的content区域不再是一个RecyclerView,
 * 而是一个CoordinatorLayout. CoordinatorLayout包含一个RecyclerView以及一个
 * AppBarLayout
 *
 * @param <T>
 */
public abstract class CpHttpCoordinatorRvFragment<T> extends HttpFrameFragment<T> {
    private static final String TAG = "CoordinatorFragment";
    private static final boolean DEBUG = false;

    protected CoordinatorLayout mCoordinatorLayout;
    protected AppBarLayout mHeaderAppBarLayout;
    protected FrameLayout mHeaderContentView;
    protected ExRecyclerView mRecyclerView;

    @Override
    protected HttpFrameParams getHttpParamsOnFrameExecute(Object... params) {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ExDecorView decorView = (ExDecorView) super.onCreateView(inflater, container, savedInstanceState);
        View contentView = getLayoutInflater().inflate(R.layout.cp_http_coordinator_fragment, null);
        mCoordinatorLayout = contentView.findViewById(R.id.coordinator_root);
        mHeaderAppBarLayout = contentView.findViewById(R.id.cp_coordinator_header_view_app_bar);
        mHeaderContentView =contentView.findViewById(R.id.cp_coordinator_header_view);
        mRecyclerView = contentView.findViewById(R.id.cp_coordinator_fragment_recycler_view);
        ensureContentViewHasNoParent(contentView);
        super.setContentView(contentView);
        return decorView;
    }

    protected ExRecyclerView onCreateRecyclerView(){
        return mRecyclerView;
    }

    private void ensureContentViewHasNoParent(View contentView) {
        if(contentView != null && contentView.getParent() != null) {
            try {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
            }catch (Exception e) {
                //DO NOTHING
            }
        }
    }

    @Override
    protected boolean invalidateContent(T result) {
        ExRvAdapterBase adapter = getRecyclerViewAdapter();
        List<?> list = invalidateContentGetList(result);
        adapter.clearData();
        adapter.addDataAll(list);
        adapter.notifyDataSetChanged();
        return !CollectionUtil.isEmpty(list);
    }

    @Override
    protected void initContentView() {

    }

    protected List<?> invalidateContentGetList(T result) {

        return (List<?>) result;
    }

    public ExRvAdapterBase<?, ?> getRecyclerViewAdapter() {
        return mRecyclerView.getAdapter();
    }

    public ExRecyclerView getRecyclerView(){
        return mRecyclerView;
    }

    @Override
    public void scrollTop() {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    public AppBarLayout getHeaderAppBarLayout() {
        return mHeaderAppBarLayout;
    }

    public FrameLayout getHeaderContentView() {
        return mHeaderContentView;
    }
}
