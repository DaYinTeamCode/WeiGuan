package com.sjteam.weiguan.page.web.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidex.util.TextUtil;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:32 PM
 */
public class BrowserFra extends CpOperWebBaseFra {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setContentWebView(false);
        loadInitUrl();
    }

    @Override
    protected String getLoadUrl() {

        return TextUtil.filterNull(getArgumentString("url"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (getWebWidget() != null && getActivity() != null)
            getWebWidget().onActivityResult(requestCode, requestCode, data);
    }

    public static BrowserFra newInstance(Context context, String url) {

        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        return (BrowserFra) BrowserFra.instantiate(context, BrowserFra.class.getName(), bundle);
    }
}
