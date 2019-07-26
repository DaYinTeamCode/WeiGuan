package com.sjteam.weiguan.page.login.utils;

import com.alibaba.fastjson.JSON;
import com.ex.android.http.params.HttpTaskParams;
import com.sjteam.weiguan.httptask.BaseHttpParamsUtil;
import com.sjteam.weiguan.httptask.HttpApi;
import com.sjteam.weiguan.page.login.bean.WxChatLogin;

import static com.ex.android.http.params.HttpTaskParams.CONTENT_TYPE_JSON;

/**
 * 登录网络请求
 *
 * <p>
 * Created by 大印 on 2019/7/11.
 */
public class LoginHttpUtils extends BaseHttpParamsUtil {

    /***
     *  获取微信绑定参数
     *
     * @param code
     * @return
     */
    public static HttpTaskParams getWxchatBindParams(String code) {

        HttpTaskParams<String> htp = getBasePostHttpTaskParams(HttpApi.URL_WX_AUTH);
        try {

            htp.setContentType(CONTENT_TYPE_JSON);
            WxChatLogin wxChatLogin = new WxChatLogin();
            wxChatLogin.setCode(code);
            String json = JSON.toJSONString(wxChatLogin);
            htp.setContentData(json);
        } catch (Exception ex) {

        }
        return htp;
    }
}
