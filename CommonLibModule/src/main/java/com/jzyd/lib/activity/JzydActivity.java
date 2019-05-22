package com.jzyd.lib.activity;

import com.androidex.activity.ExActivity;
import com.ex.umeng.UmengAgent;

/**

 * 提供与基础业务相关的功能:友盟统计等。
 * Created by yihaibin on 15/8/22.
 */
public abstract class JzydActivity extends ExActivity {

    @Override
    protected void onDestroy() {

        onDestoryClearImageCahe();
        super.onDestroy();
    }

    protected void onDestoryClearImageCahe(){

    }

	/*
	 * umeng event part
	 */

    protected void onUmengEvent(String key) {

        UmengAgent.onEvent(this, key);
    }

    protected void onUmengEvent(String key, String info) {

        UmengAgent.onEvent(this, key, info);
    }
}
