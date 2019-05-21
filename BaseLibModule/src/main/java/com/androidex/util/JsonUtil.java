package com.androidex.util;

import org.json.JSONException;

/**
 * json工具类
 * Created by yihaibin on 2018/6/27.
 */

public class JsonUtil {

    /**
     * 返回json漂亮格式
     *
     * @param json
     * @return
     */
    public static String prettyFormat(String json) {

        try {
            return new org.json.JSONObject(json).toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "json format error";
    }
}
