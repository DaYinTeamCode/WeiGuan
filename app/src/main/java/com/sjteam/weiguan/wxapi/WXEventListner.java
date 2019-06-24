package com.sjteam.weiguan.wxapi;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/21 11:47 AM
 */
public interface WXEventListner {

    public void onResp(BaseResp baseResp);

    public void onReq(BaseReq baseReq);
}
