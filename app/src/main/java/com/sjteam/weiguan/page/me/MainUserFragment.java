package com.sjteam.weiguan.page.me;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidex.util.DensityUtil;
import com.androidex.util.ExResUtil;
import com.androidex.widget.rv.lisn.item.OnExRvItemViewClickListener;
import com.jzyd.lib.httptask.HttpFrameParams;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.viewer.CpHttpFrameXrvFragmentViewer;
import com.sjteam.weiguan.page.me.adapter.MainUserAdapter;
import com.sjteam.weiguan.page.me.bean.UserItemSet;
import com.sjteam.weiguan.page.me.decoration.MainUserItemDecoration;
import com.sjteam.weiguan.page.me.utils.MainUserDataUtil;
import com.sjteam.weiguan.page.me.widget.MainUserHeaderWidget;
import com.sjteam.weiguan.page.setting.AboutActivity;
import com.sjteam.weiguan.utils.CpFontUtil;
import com.sjteam.weiguan.widget.TitleTransWidget;

import java.util.List;

/**
 * 用户中心Fragment
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/11 4:34 PM
 */
public class MainUserFragment extends CpHttpFrameXrvFragmentViewer implements OnExRvItemViewClickListener {

    private MainUserHeaderWidget mMainUserHeaderWidget;
    private TitleTransWidget mTitleWidget;
    private MainUserAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        setContentRecyclerView();
        showContent();
    }

    @Override
    protected HttpFrameParams getHttpParamsOnFrameExecute(Object... params) {

        return null;
    }

    @Override
    protected boolean invalidateContent(Object result) {

        return true;
    }

    @Override
    protected void initData() {

        mAdapter = new MainUserAdapter();
        mAdapter.setOnExRvItemViewClickListener(this);
    }

    @Override
    protected void initTitleView() {

        mTitleWidget = new TitleTransWidget(getActivity(), getExDecorView(), false);
        mTitleWidget.setTitleViewBg(new ColorDrawable(Color.TRANSPARENT), ExResUtil.getDrawable(R.drawable.cp_white));
        mTitleWidget.setAlpha(0);

        TextView tvTitle = addTitleMiddleTextView("我的");
        getTitleView().setBackgroundResource(R.color.app_white);
        CpFontUtil.setFont(tvTitle);
        getTitleView().setClickable(false);
        setStatusbarView(getTitleView());
    }

    @Override
    protected void initContentView() {

        mMainUserHeaderWidget = new MainUserHeaderWidget(getActivity());
        mAdapter.addHeaderView(mMainUserHeaderWidget.getContentView());

        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        getRecyclerView().setLayoutManager(mLayoutManager);
        getRecyclerView().setGridSpanSizeLookUp(this::getGridSpanCount);
        getRecyclerView().addItemDecoration(new MainUserItemDecoration());
        getRecyclerView().setBackgroundColor(0xFFFFFFFF);
        getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                onRecyclerViewScroll();
            }
        });

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getRecyclerView().getLayoutParams();
        params.bottomMargin = DensityUtil.dip2px(48f);
        getRecyclerView().setLayoutParams(params);

        getRecyclerView().setAdapter(mAdapter);
        List<Object> objects = MainUserDataUtil.getUserItemDatas();
        mAdapter.setData(objects);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void invalidateFrame(Object data) {

    }

    @Override
    public void onLoadFrameFailed(int failedCode, String msg) {

    }

    @Override
    public void invalidateLoadMore(Object data) {

    }

    @Override
    public void invalidatePullRefresh(Object data) {

    }

    @Override
    public void onRefreshFailed(int failedCode, String msg) {

    }

    /**
     * 列表点击
     *
     * @param view
     * @param dataPos
     */
    @Override
    public void onExRvItemViewClick(View view, int dataPos) {

        Object object = mAdapter.getDataItem(dataPos);

        if (object instanceof UserItemSet) {

            switch (((UserItemSet) object).getItemType()) {

                case UserItemSet.ABOUT_APP_TYPE:
                    AboutActivity.startActivity(getActivity());
                    break;
                case UserItemSet.SHREAD_APP_TYPE:
                    shreadAppClick();
                    break;
                case UserItemSet.SMALL_GAME_TYPE:
                    smallGameClick();
                    break;
                default:
                    break;
            }
        }
    }

    /***
     *  分享微信好友
     */
    private void shreadAppClick() {

        showToast("分享app给好友");
    }

    /***
     * 小游戏
     */
    private void smallGameClick() {


        showToast("小游戏");
    }

    private void onRecyclerViewScroll() {

        if (mAdapter == null) {

            return;
        }
        float headerTop = mAdapter.getHeader().getConvertView().getTop();
        //从往上滚动48dp 后再做渐变
        headerTop = headerTop + DensityUtil.dip2px(48);
        int alpha = (int) (-headerTop / DensityUtil.dip2px(40f) * 255);
        mTitleWidget.setAlpha(alpha);
    }

    private int getGridSpanCount(int position) {

        int type = mAdapter.getDataItemViewType(position);
        switch (type) {
            default:
                return mLayoutManager.getSpanCount();
        }
    }

    public static MainUserFragment newInstance(Context context) {

        return (MainUserFragment) Fragment.instantiate(context, MainUserFragment.class.getName());
    }
}
