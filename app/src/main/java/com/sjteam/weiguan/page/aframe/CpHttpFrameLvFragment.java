package com.sjteam.weiguan.page.aframe;

import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.androidex.adapter.list.ExListAdapterBase;
import com.androidex.util.CollectionUtil;
import com.androidex.util.ViewUtil;
import com.sjteam.weiguan.R;

import java.util.List;

/**
 * Created by yihaibin on 15/8/24.
 */
public abstract class CpHttpFrameLvFragment<T> extends HttpFrameFragment<T> {

    protected void setContentListView() {

        setContentView(onCreateListView());
    }

    protected ListView onCreateListView() {

        return ViewUtil.getCleanListView(getActivity(), R.id.lv);
    }

    public ListView getListView() {

        return (ListView) getFrameContentView();
    }

    protected void setListViewBackground(int resId) {

        getListView().setBackgroundResource(resId);
    }

    protected void addListViewHeader(View v) {

        getListView().addHeaderView(v);
    }

    protected void addListViewFooter(View v) {

        getListView().addFooterView(v);
    }

    protected void setListViewAdapter(ExListAdapterBase<?, ?> adapter) {

        getListView().setAdapter(adapter);
    }

    /**
     * @return true:表示数据可用，false:表示数据不可用
     * 列表页用数据是否为空来表示数据是否可用
     */
    @Override
    protected boolean invalidateContent(T result) {

        ExListAdapterBase adapter = getExAdapter();
        List<?> list = invalidateContentGetList(result);
        adapter.setData(list);
        adapter.notifyDataSetChanged();
        return !CollectionUtil.isEmpty(list);
    }

    protected List<?> invalidateContentGetList(T result) {

        return (List<?>) result;
    }

    public ExListAdapterBase<?, ?> getExAdapter() {

        if (getListView().getAdapter() instanceof HeaderViewListAdapter) {

            return (ExListAdapterBase<?, ?>) ((HeaderViewListAdapter) getListView().getAdapter()).getWrappedAdapter();
        } else {

            return (ExListAdapterBase<?, ?>) getListView().getAdapter();
        }
    }

    @Override
    public void scrollTop() {

        if (getListView() != null)
            getListView().setSelectionFromTop(0, 0);
    }
}
