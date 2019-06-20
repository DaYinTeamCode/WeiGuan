package com.sjteam.weiguan.page.web.fragment;

import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.androidex.util.DensityUtil;
import com.androidex.util.TextUtil;
import com.androidex.util.VglpUtil;
import com.androidex.view.ExWebView;
import com.jzyd.lib.refresh.sqkbswipe.SqkbSwipeRefreshLayout;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragment;
import com.sjteam.weiguan.page.web.widget.CpWebWidget;

/**
 * app webview fragment 基类
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:06 PM
 */
public abstract class CpWebBaseFra extends CpFragment implements CpWebWidget.WebViewListener, ExWebView.OnWebViewScrollChange, SqkbSwipeRefreshLayout.OnRefreshListener {

    private FrameLayout mFlSrlContainer;
    private SqkbSwipeRefreshLayout mSrlRefresh;
    private CpWebWidget mWebWidget;

    @Override
    public void onResume() {

        super.onResume();
        if (mWebWidget != null)
            mWebWidget.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (mWebWidget != null)
            mWebWidget.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mWebWidget != null) {

            mWebWidget.onDestroy();
        }
    }

    //*********************** 供子类调用 ************************

    protected CpWebWidget getWebWidget() {

        return mWebWidget;
    }

    protected SqkbSwipeRefreshLayout getSrlRefresh() {

        return mSrlRefresh;
    }

    protected void setContentWebView(boolean needSwipeRefresh) {

        setContentWebView(VglpUtil.getFllpMM(), needSwipeRefresh);
    }

    protected void setContentWebView(FrameLayout.LayoutParams params, boolean needSwipeRefresh) {

        mWebWidget = new CpWebWidget(getActivity());
        mWebWidget.setWebViewListener(this);

        if (needSwipeRefresh) {

            //构建webview内容
            mFlSrlContainer = new FrameLayout(getActivity());
            mFlSrlContainer.addView(mWebWidget.getContentView(), params);

            //构建下拉刷新框架,将webview内容添加进去
            mSrlRefresh = new SqkbSwipeRefreshLayout(getActivity());
            mSrlRefresh.setColorSchemeResources(R.color.cp_progress_color);
            mSrlRefresh.setEnabled(true);
            mSrlRefresh.setOnRefreshListener(this);
            mSrlRefresh.addView(mFlSrlContainer, VglpUtil.getVglpMM());
            mSrlRefresh.setProgressViewEndTarget(true, DensityUtil.dip2px(100));
            //解决webview与swiperefreshlayout的冲突
            mSrlRefresh.setOnChildScrollUpCallback(new SqkbSwipeRefreshLayout.OnChildScrollUpCallback() {
                @Override
                public boolean canChildScrollUp(SqkbSwipeRefreshLayout parent, @Nullable View child) {

                    return mWebWidget.getWebView().getScrollY() > 0;
                }
            });

            //设置fragment
            setContentView(mSrlRefresh);

            //设置webview 监听
            mWebWidget.setOnWebViewScrollChange(this);

        } else {

            //设置fragment
            setContentView(mWebWidget.getContentView());
        }
    }

    protected FrameLayout getSrlContainer() {

        return mFlSrlContainer;
    }


    //*********************** 默认实现 ************************


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {

        if (mWebWidget != null)
            mWebWidget.reload();

    }

    /**
     * webview滚动监听
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    public void onWebViewScrollChange(int l, int t, int oldl, int oldt) {

        //nothing
    }


    //*********************** 供子类覆写 ************************


    @Override
    public void onWebViewPageStarted(WebView view, String url) {

        //nothing
    }

    @Override
    public void onWebViewReceivedTitle(WebView webView, String title) {

        //nothing
    }

    @Override
    public void onWebViewProgressChanged(WebView view, int newProgress) {

        //nothing
    }

    @Override
    public void onWebViewPageFinished(WebView view, String url) {

        if (mSrlRefresh != null)
            mSrlRefresh.setRefreshing(false);
    }

    @Override
    public void onWebViewReceivedError(WebView view, int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onReceivedHttpError(WebView view, Object request, WebResourceResponse errorResponse) {

        int code = -1;
        String msg = "";
        String url = "";
        if (view != null) {
            url = TextUtil.isEmpty(view.getUrl()) ? view.getOriginalUrl() : view.getUrl();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && errorResponse != null) {
            msg = errorResponse.getReasonPhrase();
            code = errorResponse.getStatusCode();
        }
    }

    @Override
    public boolean onWebViewShouldOverrideUrlLoading(WebView view, String url) {

        return false;
    }

    @Override
    public boolean onKeyBackToWebFirstPage() {

        return false;
    }

    @Override
    public WebResourceResponse onInterceptRequest(WebView view, String url) {

        return null;
    }

    @Override
    public void onLoadResource(WebView view, String url) {

    }

}
