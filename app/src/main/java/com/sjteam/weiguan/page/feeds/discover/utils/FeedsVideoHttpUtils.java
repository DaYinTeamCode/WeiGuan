package com.sjteam.weiguan.page.feeds.discover.utils;

import com.ex.android.http.params.HttpTaskParams;
import com.sjteam.weiguan.httptask.BaseHttpParamsUtil;
import com.sjteam.weiguan.httptask.HttpApi;

/**
 * Created by 大印 on 2019/7/11.
 */
public class FeedsVideoHttpUtils extends BaseHttpParamsUtil {

    /***
     *  获取Feeds视频请求参数
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static HttpTaskParams getFeedsVideoParams(int page, int pageSize) {

        HttpTaskParams htp = getBaseGetHttpTaskParams(HttpApi.URL_VIDEO_FEEDS, page, pageSize);
        return htp;
    }
}
