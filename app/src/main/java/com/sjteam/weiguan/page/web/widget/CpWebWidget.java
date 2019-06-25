package com.sjteam.weiguan.page.web.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.androidex.plugin.ExLayoutWidget;
import com.androidex.util.AppInfoUtil;
import com.androidex.util.CompatUtil;
import com.androidex.util.DensityUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.WebViewUtil;
import com.androidex.view.ExWebView;
import com.jzyd.lib.loading.PageLoadingView;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.dialog.ActionListDialog;
import com.sjteam.weiguan.dialog.CpBaseDialog;
import com.sjteam.weiguan.page.web.activity.BrowserActivity;
import com.sjteam.weiguan.utils.GetPathFromUrikitkatUtil;
import com.sjteam.weiguan.utils.PermissionUtil;

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:02 PM
 */
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class CpWebWidget extends ExLayoutWidget implements View.OnKeyListener {

    private static final String TAG = CpWebWidget.class.getSimpleName();
    private static final int REQUEST_CODE_TAKE_PICETURE = 11;
    private static final int REQUEST_CODE_PICK_PHOTO = 12;
    private ExWebView mWebView;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private WebViewDownListener mDonloadlistener;
    private PageLoadingView mLogingView;
    private WebViewListener mLisn;
    private OnWebGoBackListener mGoBackListener;

    private boolean mOnlyReceiveFirstPageTitle;
    private boolean mIsReceivedFirstPageTitle;

    public ValueCallback mFilePathCallback;
    private File picturefile;
    private boolean mEnableReplaceHtml;

    public CpWebWidget(Activity activity) {

        super(activity);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mWebView != null)
            mWebView.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (mWebView != null)
            mWebView.onPause();
    }


    @Override
    public void onDestroy() {

        if (mWebView != null)
            mWebView.destroy();
    }

    public void removeMySelfFromParentView() {
        if (getContentView() == null) {
            return;
        }
        try {
            ViewGroup parent = (ViewGroup) getContentView().getParent();
            parent.removeView(getContentView());
        } catch (Exception e) {
        }
    }

    @Override
    protected View onCreateView(Activity activity, ViewGroup parent, Object... args) {

        FrameLayout content = new FrameLayout(activity);
        initWebView(content);
        initLoadingView(content);
        return content;
    }

    private void initWebView(FrameLayout content) {

        mWebView = new ExWebView(content.getContext());
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);   // 开启硬件加速  播放视频必须开启 否则黑屏
        content.addView(mWebView, VglpUtil.getFllpMM());

        mWebChromeClient = newWebChromeClient();
        mWebViewClient = newWebViewClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setDownloadListener(newDownloadListener());
        mWebView.setOnKeyListener(this);

        WebSettings settings = mWebView.getSettings();
        String userAgent = settings.getUserAgentString() + " " + AppInfoUtil.getAppPackageName() + "/" + AppInfoUtil.getVersionName();
        settings.setUserAgentString(userAgent);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setTextZoom(100);//设置webview 不随系统字体大小变化
        settings.setDomStorageEnabled(true);
        WebViewUtil.setMixedContentMode(settings, WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        settings.setBuiltInZoomControls(true);//构建缩放控制，包含 setSupportZoom(true)和setDisplayZoomControls(true)
        settings.setSupportZoom(true);//支持缩放
        settings.setDisplayZoomControls(false);//是否显示缩放按钮
    }

    private void initLoadingView(FrameLayout content) {

        mLogingView = new PageLoadingView(getActivity());
        content.addView(mLogingView, VglpUtil.getFllpSS(DensityUtil.dip2px(24), DensityUtil.dip2px(24), Gravity.CENTER));
    }

    public void setWebViewListener(WebViewListener lisn) {

        mLisn = lisn;
    }

    public void setGoBackListener(OnWebGoBackListener goBackListener) {

        mGoBackListener = goBackListener;
    }

    public void setOnlyReceiveFirstPageTitle(boolean bool) {

        mOnlyReceiveFirstPageTitle = bool;
    }

    public WebView getWebView() {

        return mWebView;
    }

    public WebChromeClient getWebChromeClient() {

        return mWebChromeClient;
    }

    public WebViewClient getWebViewClient() {

        return mWebViewClient;
    }

    public WebViewDownListener getDownloadListener() {

        return mDonloadlistener;
    }

    public void setDownloadListener(WebViewDownListener doanloadListener) {

        mDonloadlistener = doanloadListener;
    }

    public void setOnWebViewScrollChange(ExWebView.OnWebViewScrollChange listner) {

        mWebView.setOnWebViewScrollChange(listner);
    }

    public void addJavascriptInterface(Object object, String name) {

        mWebView.addJavascriptInterface(object, name);
    }

    public void loadUrl(String url) {

        url = TextUtil.filterNull(url);
        mWebView.loadUrl(url);
    }

    public void reload() {

        mWebView.reload();
    }

    public void loadJavascript(String script) {

        script = TextUtil.filterNull(script);
        mWebView.loadUrl("javascript: " + script);
    }

    public boolean canGoBack() {

        return mWebView.canGoBack();
    }

    public void goBack() {

        mWebView.goBack();
        if (mGoBackListener != null) {

            mGoBackListener.onWebGoBack();
        }
    }

    public void scrollTop() {

        mWebView.scrollTo(0, 0);
    }

    private void showLoadingView() {

        mLogingView.show();
    }

    private void hideLoadingView() {

        mLogingView.hide();
    }

    /**
     * webview方法回调区
     *****************************************************************************/

    /**
     * 拦截back按键，返回上一个页面
     *
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {

            if (canGoBack()) {

                goBack();
                return true;

            } else {

                if (!isActivityFinishing() && mLisn != null)
                    return mLisn.onKeyBackToWebFirstPage();
            }
        }

        return false;
    }

    private WebChromeClient newWebChromeClient() {

        //Permission 相关方法重写，修复百川自定义webview 到商品详情闪退bug 【2.7.6 修复】
        return new WebChromeClient() {

            @Override
            public void onPermissionRequest(PermissionRequest request) {

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {


            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {

            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {

            }

            private boolean checkTaobaoUrl() {

                String url = mWebView.getOriginalUrl();
                if (TextUtil.isEmpty(url))
                    return false;

                String host = null;
                try {
                    Uri uri = Uri.parse(mWebView.getOriginalUrl());
                    host = uri.getHost().toLowerCase();
                } catch (Exception e) {

                }

                return !TextUtil.isEmpty(host) && (host.contains("taobao") || host.contains("tmall"));
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onReceivedTitle title = " + title);

                if (mIsReceivedFirstPageTitle && mOnlyReceiveFirstPageTitle)
                    return;

                mIsReceivedFirstPageTitle = true;

                if (!isActivityFinishing() && mLisn != null)
                    mLisn.onWebViewReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (!isActivityFinishing() && mLisn != null)
                    mLisn.onWebViewProgressChanged(view, newProgress);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {//5.0+

                mFilePathCallback = uploadMsg;
                showDialog();
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {// android 系统版本>4.1.1

                mFilePathCallback = uploadMsg;
                showDialog();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {//android 系统版本<3.0

                mFilePathCallback = uploadMsg;
                showDialog();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {//android 系统版本3.0+

                mFilePathCallback = uploadMsg;
                showDialog();
            }
        };
    }

    /**
     * 网页下载监听
     *
     * @return
     */
    private DownloadListener newDownloadListener() {

        return new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                if (mDonloadlistener != null) {

                    mDonloadlistener.onDownload(url, userAgent, contentDisposition, mimetype, contentLength);
                } else {

                    if (TextUtils.isEmpty(url) || getActivity() == null)
                        return;

                    try {

                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        getActivity().startActivity(intent);
                    } catch (Exception ex) {

                        if (LogMgr.isDebug())
                            ex.printStackTrace();
                    }
                }
            }
        };
    }

    private void showDialog() {

        String[] items = new String[]{"相机", "图库", "取消"};
        ActionListDialog dialog = new ActionListDialog(getActivity(), items);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setDialogItemClick(new ActionListDialog.DialogItemClick() {
            @Override
            public void onDialogItemClick(CpBaseDialog dialog, int position, String text, View view) {
                switch (position) {
                    case 0:
                        checkCameraPermissionOrTakeCamera();  // 相机
                        break;
                    case 1:
                        takeForPhoto();  // 图库
                        break;
                    case 2:
                    default:
                        cancelFilePathCallback();  // 取消
                        break;
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void checkCameraPermissionOrTakeCamera() {

        if (checkCameraPermission())
            showPermissionsDialog();
        else
            takeForCamera();
    }

    /**
     * 检测相机权限
     *
     * @return
     */
    private boolean checkCameraPermission() {

        if (getActivity() == null)
            return true;

        if (PermissionUtil.checkPermissionForLessVersion_M(getActivity(), Manifest.permission.CAMERA))
            return true;
        else
            return false;
    }

    /**
     * 提示权限Dialog
     */
    private void showPermissionsDialog() {

        if (getActivity() == null)
            return;
        cancelFilePathCallback();

        if (getActivity() instanceof BrowserActivity)
            ((BrowserActivity) getActivity()).showCameraMissingPermissionDialog();
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {

        if (getActivity() == null)
            return;

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    /**
     * 调用相机
     */
    private void takeForCamera() {

        if (getActivity() == null)
            return;

        try {

            File pFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "public");// 手机图片的公共目录 图片位置
            if (!pFile.exists())
                pFile.mkdirs();

            //拍照所存路径
            picturefile = new File(pFile + File.separator + "IvMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT > 23) {//7.0及以上

                Uri contentUri = getUriForFile(getActivity(), getActivity().getResources().getString(R.string.filepath), picturefile);
                getActivity().grantUriPermission(AppInfoUtil.getAppPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {//7.0以下

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
            }

            getActivity().startActivityForResult(intent, REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {

            cancelFilePathCallback();
        }
    }

    private void cancelFilePathCallback() {

        if (mFilePathCallback != null) {

            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICETURE:

                takeCameraResult();
                break;
            case REQUEST_CODE_PICK_PHOTO:

                takePhotoResult(intent);
                break;
        }
    }

    /**
     * 图库回调
     *
     * @param data
     */
    private void takePhotoResult(Intent data) {

        if (mFilePathCallback != null) {

            Uri result = data == null ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUrikitkatUtil.getPath(getActivity(), result);
                Uri uri = Uri.fromFile(new File(path));

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                else
                    mFilePathCallback.onReceiveValue(uri);

                mFilePathCallback = null;
            } else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    /**
     * 相机回调
     */
    private void takeCameraResult() {

        if (mFilePathCallback != null) {

            Uri uri = Uri.fromFile(picturefile);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
                mFilePathCallback.onReceiveValue(new Uri[]{uri});
            else
                mFilePathCallback.onReceiveValue(uri);
            mFilePathCallback = null;
        }
    }

    private WebViewClient newWebViewClient() {

        return new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onPageStarted url = " + url);

                if (!isActivityFinishing()) {

                    showLoadingView();
                    if (mLisn != null)
                        mLisn.onWebViewPageStarted(view, url);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onLoadResource url = " + url);

                if (mLisn != null)
                    mLisn.onLoadResource(view, url);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onFormResubmission dontResend = " + dontResend + " resend = " + resend);

                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onPageCommitVisible url = " + url);

                super.onPageCommitVisible(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "shouldInterceptRequest url = " + request.toString());

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "shouldInterceptRequest url = " + url);

                return mLisn != null ? mLisn.onInterceptRequest(view, url) : null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "shouldOverrideUrlLoading url = " + url);

                if (isActivityFinishing())
                    return false;
                else
                    return mLisn == null ? false : mLisn.onWebViewShouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onPageFinished url = " + url);

                if (!isActivityFinishing()) {

                    hideLoadingView();
                    if (mLisn != null)
                        mLisn.onWebViewPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);
                if (LogMgr.isDebug())
                    LogMgr.d(simpleTag(), "onReceivedError errorCode = " + errorCode);

                if (!isActivityFinishing()) {

                    hideLoadingView();
                    replaceErrorHtml();

                    if (mLisn != null)
                        mLisn.onWebViewReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

                super.onReceivedHttpError(view, request, errorResponse);

                if (!isActivityFinishing()) {

                    hideLoadingView();
                    if (request.isForMainFrame())
                        replaceErrorHtml();

                    if (mLisn != null)
                        mLisn.onReceivedHttpError(view, request, errorResponse);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                if (handler != null)
                    handler.proceed();
            }
        };
    }

    private void replaceErrorHtml() {

        if (!mEnableReplaceHtml)
            return;

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=no'>");
        builder.append("<head>");
        builder.append("<title></title>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div style='position:relative;width:100%;height:100%;display:table;text-align:center;'>");
        builder.append("<div style='position: absolute;top:50%;left: 50%;transform: translate(-50%,-50%);-ms-transform: translate(-50%,-50%);-moz-transform: translate(-50%,-50%);-webkit-transform: translate(-50%,-50%);-o-transform: translate(-50%,-50%);'>");
        builder.append("<img  style='width: 2.6rem;height:  2.6rem;margin: 0 auto;' src='data:image/png;base64,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'/>");
        builder.append("<p style='font-size:0.3rem;color:#999999'>数据获取失败</p>");
        builder.append("</div>");
        builder.append("</div>");

        builder.append("<script>");
        builder.append("var updateBaseFontSize = function() {");
        builder.append("var dWidth = document.documentElement.clientWidth;");
        builder.append("var baseFontSize = dWidth * 100 / 750;");
        builder.append("document.getElementsByTagName('html')[0].style.fontSize=baseFontSize+'px';");
        builder.append("};");
        builder.append("window.addEventListener('resize', updateBaseFontSize);");
        builder.append("updateBaseFontSize();");
        builder.append("</script>");

        builder.append("</body>");
        builder.append("</html>");

        if (CompatUtil.isGreatThanOrEqualToKitkatVersion())
            mWebView.evaluateJavascript("javascript:document.write(\"" + builder.toString() + "\");", null);
        else
            mWebView.loadUrl("javascript:document.write(\"" + builder.toString() + "\");");
    }

    public void setEnableReplaceHtml(boolean enableReplaceHtml) {

        this.mEnableReplaceHtml = enableReplaceHtml;
    }

    /*** WebView 下载监听回调 */
    public interface WebViewDownListener {

        void onDownload(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
    }

    public interface WebViewListener {

        void onWebViewReceivedTitle(WebView webView, String title);

        void onWebViewProgressChanged(WebView view, int newProgress);

        void onWebViewPageStarted(WebView view, String url);

        void onLoadResource(WebView view, String url);

        boolean onWebViewShouldOverrideUrlLoading(WebView view, String url);

        void onWebViewPageFinished(WebView view, String url);

        void onWebViewReceivedError(WebView view, int errorCode, String description, String failingUrl);

        boolean onKeyBackToWebFirstPage();

        WebResourceResponse onInterceptRequest(WebView view, String url);

        /**
         * 修复4.4 及低版本当页面注册eventbus时，调用class.getMethods() 时报 WebResourceRequest NoClassDefFoundError。
         * 故给子类实现接口定义为Object 类型, 如果子类要做特殊判断要优选判断下request 类型再做处理。
         */
        void onReceivedHttpError(WebView view, Object request, WebResourceResponse errorResponse);
    }

    public static abstract class SampleWebViewListener implements WebViewListener {

        @Override
        public void onWebViewReceivedTitle(WebView webView, String title) {
        }

        @Override
        public void onWebViewProgressChanged(WebView view, int newProgress) {
        }

        @Override
        public void onWebViewPageStarted(WebView view, String url) {
        }

        @Override
        public void onLoadResource(WebView view, String url) {
        }

        @Override
        public boolean onWebViewShouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onWebViewPageFinished(WebView view, String url) {
        }

        @Override
        public void onWebViewReceivedError(WebView view, int errorCode, String description, String failingUrl) {

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
        public void onReceivedHttpError(WebView view, Object request, WebResourceResponse errorResponse) {

        }
    }

    /**
     * web页面 goback 回调监听
     */
    public interface OnWebGoBackListener {

        void onWebGoBack();
    }
}
