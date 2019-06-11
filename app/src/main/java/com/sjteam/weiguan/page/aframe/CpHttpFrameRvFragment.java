package com.sjteam.weiguan.page.aframe;

import android.view.View;

import com.androidex.util.CollectionUtil;
import com.androidex.widget.rv.adapter.ExRvAdapterBase;
import com.androidex.widget.rv.view.ExRecyclerView;
import com.sjteam.weiguan.R;

import java.util.List;

/**
 * Created by yihaibin on 15/8/24.
 */
public abstract class CpHttpFrameRvFragment<T> extends HttpFrameFragment<T> {

    private ExRecyclerView mRv;

    protected void setContentRecyclerView(ExRecyclerView erv){

        mRv = erv;
    }

    protected void setContentRecyclerView(){

        setContentView(onCreateRecyclerView());
    }

    protected ExRecyclerView onCreateRecyclerView(){

        mRv = new ExRecyclerView(getActivity());
        mRv.setId(R.id.erv);
        return mRv;
    }

    public ExRecyclerView getRecyclerView(){

        return mRv;
    }

    protected void setRecyclerViewBackground(int resId){

        mRv.setBackgroundResource(resId);
    }

    protected void addRecyclerViewHeaderView(View v){

        mRv.addHeaderView(v);
    }

    protected void addRecyclerViewFooter(View v){

        mRv.addFooterViewFirst(v);
    }

    protected void setRecyclerViewAdapter(ExRvAdapterBase<?, ?> adapter){

        mRv.setAdapter(adapter);
    }

    /**Ω
     * @return true:表示数据可用，false:表示数据不可用
     * 列表页用数据是否为空来表示数据是否可用
     */
    @Override
    protected boolean invalidateContent(T result) {

        ExRvAdapterBase adapter = getRecyclerViewAdapter();
        List<?> list = invalidateContentGetList(result);
        adapter.clearData();
        adapter.addDataAll(list);
        adapter.notifyDataSetChanged();
        return !CollectionUtil.isEmpty(list);
    }

    protected List<?> invalidateContentGetList(T result){

        return (List<?>) result;
    }

    public ExRvAdapterBase<?,?> getRecyclerViewAdapter(){

        return mRv.getAdapter();
    }

    @Override
    public void scrollTop(){

        if(mRv != null)
            mRv.scrollToPosition(0);
    }
}
