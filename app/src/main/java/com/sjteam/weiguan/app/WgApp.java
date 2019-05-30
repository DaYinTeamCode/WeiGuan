package com.sjteam.weiguan.app;

import com.sjteam.weiguan.utils.FrescoInitUtil;

/**
 * Created by 大印 on 2019/5/21.
 */
public class WgApp extends BaseWgApp {

    @Override
    public void onCreate() {

        super.onCreate();

        initAsyncImageLoader();
    }

    /***
     *  初始化图片库
     */
    private void initAsyncImageLoader() {

        FrescoInitUtil.initFrescoConfig(this);
    }
}
