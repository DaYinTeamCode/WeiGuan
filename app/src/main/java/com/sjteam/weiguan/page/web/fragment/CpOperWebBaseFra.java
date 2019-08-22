package com.sjteam.weiguan.page.web.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.imageloader.fresco.FrescoImageView;
import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.DensityUtil;
import com.androidex.util.NumberUtil;
import com.androidex.util.ResUtil;
import com.androidex.util.TextUtil;
import com.androidex.util.UrlUtil;
import com.androidex.util.VglpUtil;
import com.androidex.view.ExWebView;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.constants.DimenConstant;
import com.sjteam.weiguan.page.web.widget.WebTitleBackWidget;
import com.sjteam.weiguan.scheme.CpActSchemeLaunchUtil;
import com.sjteam.weiguan.utils.CpFontUtil;
import com.sjteam.weiguan.widget.TitleTransWidget;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:08 PM
 */
public abstract class CpOperWebBaseFra extends CpWebBaseFra implements DimenConstant,
        ExWebView.OnWebViewScrollChange, WebTitleBackWidget.WebTitleLitener {

    private static final String TYPE_WEBVIEW_FLOAT_STYLE = "1";
    private static final String TYPE_OPEN_NEW_PAGE = "1";

    private boolean mIntentHasTitle;
    private WebTitleBackWidget mWebTitleWidget;
    protected TextView mTvTitle;
    protected int mBackResId, mCloseResId;
    private ImageView mIvTitleBack;
    private TitleTransWidget mTitleWidget;
    private int mHeaderTop = 0;
    private String mWebviewType;
    private String mHwRatio;
    private String mTitleBgColor;
    private boolean isTitleBgColorException;
    private String mTitleExceptionBgColor;
    private boolean mIsMainTab;
    private String mNoTitleBarTag;
    private String mInterceptPageBackFlag = TextUtil.TEXT_EMPTY;
    private String mLoadUrl;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (getWebWidget() != null)
            getWebWidget().onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initData() {

        String url = getLoadUrl();

        if (TextUtil.isEmpty(url))
            return;

        Uri uri = Uri.parse(url);

        mWebviewType = TextUtil.filterEmpty(uri.getQueryParameter("webview"), "0");
        mHwRatio = TextUtil.filterEmpty(uri.getQueryParameter("hw_ratio"), "0.128");
        mTitleBgColor = TextUtil.filterEmpty(uri.getQueryParameter("nav_bg"), "");
        mNoTitleBarTag = TextUtil.filterEmpty(uri.getQueryParameter("no_title_bar"), "0");
        if (isFloatTitleStyle())
            mLoadUrl = UrlUtil.appendParam(url, "app_nav_ratio", String.valueOf((float) getTitleViewHeight() / SCREEN_WIDTH));
        else
            mLoadUrl = url;
    }

    @Override
    protected void initTitleView() {

        if ("1".equalsIgnoreCase(mNoTitleBarTag)) {

            StatusBarManager.getInstance()
                    .initStatusbar(getActivity());
            return;
        }

        if (isFloatTitleStyle())
            setFloatTitle();
        else
            setDefaultTitle();

        if (getExDecorView().getTitleView() != null) {

            setIsStatusBarDarkFot(true);
            setStatusbarView(getExDecorView().getTitleView());
        }
    }

    @Override
    protected void initContentView() {

    }

    protected void loadInitUrl() {

        getWebWidget().loadUrl(mLoadUrl);
    }

    /*--------------------------------Default Title Style-----------------------------*/

    private void setDefaultTitle() {

        String title = TextUtil.filterNull(getArgumentString("title"));

        mIntentHasTitle = !TextUtil.isEmpty(title);
        mTvTitle = addTitleMiddleTextView(title);
        CpFontUtil.setFont(mTvTitle);

        if (mIsMainTab) {

            mTvTitle.setTextColor(Color.WHITE);

        } else {

            mTvTitle.setTextColor(0XFF555555);
            mWebTitleWidget = new WebTitleBackWidget(getActivity(), mBackResId, mCloseResId);
            mWebTitleWidget.hideClose();
            mWebTitleWidget.setWebTitleListener(this);
            addTitleLeftView(mWebTitleWidget.getContentView(), VglpUtil.getLllpWM());
            getTitleView().setBackground(ResUtil.getDrawable(getContext(), R.drawable.bg_title_bar));
        }
        try {

            getTitleView().setBackgroundColor(Color.parseColor(String.format("#%s" + mTitleBgColor)));

        } catch (Exception e) {

            if (mIsMainTab) {

                getTitleView().setBackgroundColor(Color.parseColor(TextUtil.isEmpty(mTitleExceptionBgColor) ? "#FFFF2220" : mTitleExceptionBgColor));

            } else {

                getTitleView().setBackground(ResUtil.getDrawable(getContext(), R.drawable.bg_title_bar));
            }
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = DensityUtil.dip2px(48f) +
                StatusBarManager.getInstance().getStatusbarHeight(getActivity());
        getWebWidget().getWebView().setLayoutParams(layoutParams);

    }

    /*--------------------------------Float Title Style-----------------------------*/

    private void setFloatTitle() {

        getWebWidget().setOnWebViewScrollChange(this);
        mTitleWidget = new TitleTransWidget(getActivity(), getExDecorView(), false);

        try {

            isTitleBgColorException = false;
            mTitleWidget.setTitleBgRGB(mTitleBgColor);
        } catch (Exception e) {

            mTitleWidget.setTitleDefaultBgRGB(TextUtil.isEmptyTrim(mTitleExceptionBgColor) ? "#FFFFFF" : mTitleExceptionBgColor);
            isTitleBgColorException = true;
        }
        mTitleWidget.setAlpha(0);
        mTitleWidget.setOnBgAlphaChangedListener(alpha -> setTitleAlphaStyle(alpha));

        if (!mIsMainTab)
            mIvTitleBack = addTitleLeftBackView(); // 返回
        //标题
        mTvTitle = addTitleMiddleTextView(TextUtil.TEXT_EMPTY);
        CpFontUtil.setFont(mTvTitle);
        setTitleBarStyleRescoure(false);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = DensityUtil.dip2px(48f) + StatusBarManager.getInstance().getStatusbarHeight(getActivity());
        getWebWidget().getWebView().setLayoutParams(layoutParams);

    }

    /*--------------------------对子类提供函数----------------------------------*/

    protected void setIsMainTabWebView(boolean isMainTab) {

        this.mIsMainTab = isMainTab;
    }

    protected void setTitleExceptionBgColor(String bgColor) {

        this.mTitleExceptionBgColor = bgColor;
    }

    protected void hideTitlebackWidget() {

        if (mWebTitleWidget == null)
            return;

        mWebTitleWidget.hide();
    }


    /*-------------------------辅助函数-------------------------*/

    private void setTitleAlphaStyle(int alpha) {

        if (alpha > 64) {

            if (isDefaultBarStyle())
                setTitleBarStyleRescoure(true);
            else
                setTitleBarStyleRescoure(false);
        } else {

            setTitleBarStyleRescoure(false);
        }
    }

    private void setTitleBarStyleRescoure(boolean isDefault) {

        if (isDefault) {

            if (mIvTitleBack != null)
                mIvTitleBack.setImageResource(R.mipmap.ic_title_bar_back_gray);

            if (mTvTitle != null) {

                mTvTitle.setTextColor(0XFF555555);
            }
        } else {

            if (mIvTitleBack != null)
                mIvTitleBack.setImageResource(R.mipmap.ic_title_bar_back_white);

            if (mTvTitle != null) {

                mTvTitle.setTextColor(Color.WHITE);
            }
        }
    }

    private boolean isDefaultBarStyle() {

        return mTitleBgColor.equalsIgnoreCase("FFFFFF") || isTitleBgColorException && TextUtil.isEmpty(mTitleExceptionBgColor);
    }

    /**
     * 浮动样式
     *
     * @return
     */
    private boolean isFloatTitleStyle() {

        return TYPE_WEBVIEW_FLOAT_STYLE.equalsIgnoreCase(mWebviewType);
    }

    /**
     * 是否 打开新页面
     *
     * @return
     */
    private boolean isNewPage(String url) {

        if (TextUtil.isEmpty(url))
            return false;
        try {

            Uri uri = Uri.parse(url);
            String newPage = TextUtil.filterEmpty(uri.getQueryParameter("new_page"), "0");
            return TYPE_OPEN_NEW_PAGE.equals(newPage);

        } catch (Throwable e) {

        }

        return true;
    }

    private void setTitleAlphaByHeaderWidgetTop(int headerY) {

        int headerTop = headerY;
        if (mHeaderTop != headerTop) {

            float ratio = NumberUtil.parseFloat(mHwRatio, 0) * SCREEN_WIDTH;
            int alpha = (int) ((headerTop / (SCREEN_WIDTH * (ratio / SCREEN_HEIGHT) * 1.0f)) * 255);
            if (mTitleWidget != null)
                mTitleWidget.setAlpha(alpha);
            mHeaderTop = headerTop;
        }
    }

    /*-----------------------提供子类函数-------------------------*/

    /**
     * WebView
     * 滚动改变监听
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    public void onWebViewScrollChange(int l, int t, int oldl, int oldt) {

        super.onWebViewScrollChange(l, t, oldl, oldt);
        setTitleAlphaByHeaderWidgetTop(t);
    }

    @Override
    public void onWebViewReceivedTitle(WebView webView, String title) {

        if (title == null
                || title.startsWith("http")
                || title.startsWith("https")) {

            return;
        }

        if (!mIntentHasTitle && mTvTitle != null)
            mTvTitle.setText(TextUtil.filterNull(title));
    }

    @Override
    public void onWebViewPageFinished(WebView view, String url) {

        super.onWebViewPageFinished(view, url);
        invalidateTitleClose();
    }

    @Override
    public boolean onWebViewShouldOverrideUrlLoading(WebView view, String url) {

        if (TextUtil.isEmpty(url))
            return true;

        if (url.equals(view.getUrl())) {
            view.reload();
            return true;
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {

            return overrideHttpScheme(url, isFloatTitleStyle() || isNewPage(url));

        } else {//其他scheme 默认发送intent

            sendUriIntent(url);
            return true;
        }
    }

    protected boolean overrideHttpScheme(String url, boolean isOpenNewPage) {

        if (getActivity() == null)
            return false;

        boolean result = CpActSchemeLaunchUtil.startActivityBySchemeUrl(getActivity(), url, isOpenNewPage,

                new CpActSchemeLaunchUtil.CpActSchemeUrlListener() {

                    @Override
                    public boolean onCheckSchemeUrlStartActivity(Activity activity, Uri uri, String url, String path, String lastPathSegment,
                                                                 String title) {

                        return false;
                    }
                });

        return result;
    }

    protected boolean sendUriIntent(String url) {

        if (getActivity() == null)
            return false;

        try {

            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.setComponent(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                intent.setSelector(null);
            }

            //该Intent无法被设备上的应用程序处理
            if (getActivity().getPackageManager().resolveActivity(intent, 0) == null) {
                return false;
            }
            startActivity(intent);

        } catch (Exception e) {

            return false;
        }

        return true;
    }

    @Override
    public void onTitleBackClick() {

        if (!getWebWidget().canGoBack())
            finishActivity();
        else
            getWebWidget().goBack();
    }

    @Override
    public void onTitleCloseClick() {

        finishActivity();
    }

    @Override
    public void onWebViewPageStarted(WebView view, String url) {

        super.onWebViewPageStarted(view, url);

        if (!TextUtil.isEmpty(mInterceptPageBackFlag))
            mInterceptPageBackFlag = TextUtil.TEXT_EMPTY;
    }

    private void invalidateTitleClose() {

        if (mWebTitleWidget == null)
            return;

        if (getWebWidget().canGoBack())
            mWebTitleWidget.showClose();
        else
            mWebTitleWidget.hideClose();
    }

    /**
     * web 加载页面url
     *
     * @return 要加载的url
     */
    protected abstract String getLoadUrl();

}
