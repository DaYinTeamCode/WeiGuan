package com.sjteam.weiguan.wxapi;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/21 11:47 AM
 */
public class WXManagerHandler {

    private static WXManagerHandler wxManagerHandler = new WXManagerHandler();
    private ArrayList<WXEventListner> mLisns;

    private WXManagerHandler() {

        this.mLisns = new ArrayList<>();
    }

    public static void releaseInstance() {

        if (wxManagerHandler != null)
            wxManagerHandler.clear();

        wxManagerHandler = null;
    }

    public static WXManagerHandler getInstance() {

        if (wxManagerHandler == null)
            wxManagerHandler = new WXManagerHandler();

        return wxManagerHandler;
    }

    public boolean register(WXEventListner lisn) {

        return mLisns.add(lisn);
    }

    /**
     * 实际上，mLisns 列表维护的是一个"分享到朋友圈" 或者 "分享给朋友" 或者 "微信授权"的回调消息队列。
     * 不可能同时出现几种消息同时存在于某个队列的情况。
     * 也不可能同时出现同一个同一个消息多次出现，比如说：两个分享给朋友的动作。
     * 只能是：一个分享给朋友，有很多地方同时监听这一个分享给朋友的信息。
     *
     * @param baseResp
     */
    public void onResp(BaseResp baseResp) {
        WXEventListner lisn = null;
        Iterator<WXEventListner> it = mLisns.iterator();
        while (it.hasNext()) {
            lisn = it.next();
            if (lisn != null) {
                lisn.onResp(baseResp);
            }
            it.remove();
        }
    }

    public void onReq(BaseReq baseReq) {
        WXEventListner lisn = null;
        Iterator<WXEventListner> it = mLisns.iterator();
        while (it.hasNext()) {
            lisn = it.next();
            if (lisn != null) {
                lisn.onReq(baseReq);
            }
            it.remove();
        }
    }

    public void clear() {

        mLisns.clear();
    }

}
