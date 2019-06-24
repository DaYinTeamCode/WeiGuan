package com.sjteam.weiguan.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sjteam.weiguan.app.AppConfig;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信回调基类
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/21 11:48 AM
 */
public class WXEntryBaseActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI wxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wxApi = WXAPIFactory.createWXAPI(this, AppConfig.wxKey, false);
        wxApi.registerApp(AppConfig.wxKey);

        try {
            Intent intent = getIntent();
            wxApi.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        try {
            setIntent(intent);
            wxApi.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq req) {

        WXManagerHandler.getInstance().onReq(req);
        finish();
    }

    @Override
    public void onResp(BaseResp reps) {

        WXManagerHandler.getInstance().onResp(reps);
        finish();
    }
}
