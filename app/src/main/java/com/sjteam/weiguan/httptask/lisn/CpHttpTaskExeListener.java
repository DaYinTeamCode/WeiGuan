package com.sjteam.weiguan.httptask.lisn;

import com.androidex.util.CollectionUtil;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.androidex.util.ToastUtil;
import com.ex.android.http.params.HttpTaskParams;
import com.ex.android.http.task.HttpTask;
import com.jzyd.lib.httptask.JzydJsonListener;
import com.jzyd.lib.httptask.ExResponse;
import com.jzyd.lib.util.MD5Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpTask 全局监听器
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 3:45 PM
 */
public class CpHttpTaskExeListener implements HttpTask.HttpTaskExeListener, JzydJsonListener.RespHandler {

    public CpHttpTaskExeListener() {

    }

    @Override
    public void onHttpTaskSetParams(HttpTaskParams httpTaskParams) {

//        addSignParamIfNeed(httpTaskParams);
    }

    @Override
    public void onHttpTaskRequestFailed(HttpTask httpTask, int failedCode) {

        if (failedCode == 90003) {

            ToastUtil.showToast("登录状态已失效，请重新登录");
        }
    }

    @Override
    public void onHttpTaskStringResponse(HttpTask httpTask, String resp) {

    }

    /**
     * JzydJsonListener resp返回回调
     * 该函数运行在子线程
     *
     * @param ht
     * @param jsonText
     * @return
     */
    @Override
    public String onJzydHttpTaskResponse(HttpTask ht, String jsonText) {

        return jsonText;
    }

    /**
     * JzydJsonListener 响应解析成功回调
     * 该函数运行在子线程
     *
     * @param resp
     */
    @Override
    public void onJzydHttpTaskResponseSuccess(ExResponse<?> resp) {

    }

    /**
     * JzydJsonListener 响应解析错误回调
     * 该函数运行在子线程
     *
     * @param ht
     * @param t
     */
    @Override
    public void onJzydHttpTaskResponseError(HttpTask ht, Throwable t, int statusCode) {

    }


//    /**
//     * 追加接口签名参数
//     *
//     * @param httpTaskParams
//     */
//    public static void addSignParamIfNeed(HttpTaskParams httpTaskParams) {
//
//        if (httpTaskParams == null || TextUtil.isEmpty(BaseHttpParamsUtil.getApiDomain()))
//            return;
//
//        if (BaseHttpParamsUtil.getApiDomain().equals(httpTaskParams.getHost())
//                && !HttpApi.URL_UPLOAD_CONTACT.equals(httpTaskParams.getPath())) {//2.9.90通信录上报接口不用签名
//
//            removeStringKeyIfExists(httpTaskParams, "sign");
//            String signIn = getSign(httpTaskParams);
//            httpTaskParams.addParam("sign", signIn);
//
//            if (LogMgr.isDebug())
//                LogMgr.d("exeLisn", "final sign=" + signIn);
//        }
//    }

    /**
     * 移除已存在的签名key
     *
     * @param httpTaskParams
     * @param key
     */
    private static void removeStringKeyIfExists(HttpTaskParams httpTaskParams, String key) {

        if (httpTaskParams == null)
            return;

        key = TextUtil.filterNull(key);
        List<NameValuePair> params = httpTaskParams.getStringParams();
        NameValuePair pair;
        for (int i = 0; i < CollectionUtil.size(params); i++) {

            pair = params.get(i);
            if (pair != null && key.equals(pair.getName())) {

                params.remove(i);
                i--;
            }
        }
    }

    /**
     * 获取签名
     *
     * @param httpTaskParams
     * @return
     */
    private static String getSign(HttpTaskParams httpTaskParams) {

        try {

            List<NameValuePair> pairs = httpTaskParams.getStringParams();
            Collections.sort(pairs, new Comparator<NameValuePair>() {
                @Override
                public int compare(NameValuePair lhs, NameValuePair rhs) {

                    return lhs.getName().compareTo(rhs.getName());
                }
            });

            StringBuilder sb = new StringBuilder();
            NameValuePair pair;
            for (int i = 0; i < pairs.size(); i++) {

                pair = pairs.get(i);
                if (i > 0)
                    sb.append('&');

                sb.append(pair.getName());
                sb.append('=');
                sb.append(pair.getValue());
            }

            sb.append("81f454ac98956541b195f2c7f9e53a06");
            String md5 = MD5Util.md5(sb.toString());
            if (LogMgr.isDebug())
                LogMgr.d("http sign -> " + TextUtil.filterNull(md5));

            return TextUtil.filterNull(md5);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return TextUtil.TEXT_EMPTY;
    }

    /**
     * 替换channel_id 值
     *
     * @param pairs
     * @param replaceChannelId
     */
    private void replaceChannelIdValue(List<NameValuePair> pairs, int replaceChannelId) {

        NameValuePair pair;
        for (int i = 0; i < CollectionUtil.size(pairs); i++) {

            pair = pairs.get(i);
            if (pair != null && "channel_id".equals(pair.getName()))
                pairs.set(i, new BasicNameValuePair(pair.getName(), String.valueOf(replaceChannelId)));
        }
    }

    /**
     * 键值对转map对象
     *
     * @param pairs
     * @return
     */
    private Map<String, String> nameValuePairs2Map(List<NameValuePair> pairs) {

        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isEmpty(pairs))
            return map;

        NameValuePair pair;
        for (int i = 0; i < pairs.size(); i++) {

            pair = pairs.get(i);
            if (pair != null)
                map.put(TextUtil.filterNull(pair.getName()), TextUtil.filterNull(pair.getValue()));
        }
        return map;
    }
}
