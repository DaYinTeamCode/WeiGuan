package com.androidex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

import com.androidex.util.LogMgr;

/**
 * 扩展的WebView
 * Created by yihaibin on 15/9/12.
 */
public class ExWebView extends WebView {

    private OnWebViewScrollChange mOnWebViewScrollChange;

    public ExWebView(Context context) {

        super(context);
    }

    public ExWebView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public void setOnWebViewScrollChange(OnWebViewScrollChange onWebViewScrollChange) {

        mOnWebViewScrollChange = onWebViewScrollChange;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnWebViewScrollChange != null)
            mOnWebViewScrollChange.onWebViewScrollChange(l, t,oldl, oldt);
    }

    /**
     * 界面销毁时调用
     */
    @Override
    public void destroy(){

        try{

            stopLoading();

            ViewGroup parent = (ViewGroup) getParent();
            if(parent instanceof ListView)
                ((ListView)parent).removeHeaderView(this);
            else
                parent.removeView(this);

            super.destroy();

        }catch(Exception e){

            if(LogMgr.isDebug())
                LogMgr.e(ExWebView.class.getSimpleName(), "exwebview desroty error msg = "+e.getMessage());
        }
    }

    public interface OnWebViewScrollChange{

        void onWebViewScrollChange(int l, int t, int oldl, int oldt);
    }
}
