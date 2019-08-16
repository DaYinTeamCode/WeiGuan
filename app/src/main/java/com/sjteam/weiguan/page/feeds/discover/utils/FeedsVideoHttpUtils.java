package com.sjteam.weiguan.page.feeds.discover.utils;

import com.alibaba.fastjson.JSON;
import com.ex.android.http.params.HttpTaskParams;
import com.sjteam.weiguan.httptask.BaseHttpParamsUtil;
import com.sjteam.weiguan.httptask.HttpApi;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoRequest;
import static com.ex.android.http.params.HttpTaskParams.CONTENT_TYPE_JSON;

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

        HttpTaskParams htp = getBasePostHttpTaskParams(HttpApi.URL_VIDEO_FEEDS, page, pageSize);

        try {

            htp.setContentType(CONTENT_TYPE_JSON);
            FeedsVideoRequest feedsVideoRequest = new FeedsVideoRequest();
            feedsVideoRequest.setShowType("2");
            feedsVideoRequest.setPageNum(page);
            feedsVideoRequest.setPageSize(pageSize);
            String json = JSON.toJSONString(feedsVideoRequest);
            htp.setContentData(json);
        } catch (Exception ex) {

        }
        return htp;
    }
}
