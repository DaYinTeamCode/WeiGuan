package com.jzyd.lib.activity;

import com.androidex.activity.ExFragment;
import com.ex.umeng.UmengAgent;

/**
 * 提供与基础业务相关的功能:友盟统计等。
 * Created by yihaibin on 15/8/22.
 */
public abstract class JzydFragment extends ExFragment {

    /**
	 * umeng event part
	 */

    public void onUmengEvent(String key) {

        UmengAgent.onEvent(getActivity(), key);
    }

    protected void onUmengEvent(String key, String info) {

        UmengAgent.onEvent(getActivity(), key, info);
    }
}
