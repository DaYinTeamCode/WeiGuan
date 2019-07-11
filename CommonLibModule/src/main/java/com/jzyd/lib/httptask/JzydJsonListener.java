package com.jzyd.lib.httptask;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.ex.android.http.task.HttpTask;
import com.ex.android.http.task.HttpTaskStatus;
import com.ex.android.http.task.listener.HttpTaskStringListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一处理网络请求返回数据
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 4:11 PM
 */
public abstract class JzydJsonListener<T> implements HttpTaskStringListener<ExResponse<T>>, HttpTaskStatus {

    protected Class<?> mClazz;
    private static List<RespHandler> mRespHandlerList;

    public JzydJsonListener(Class<?> clazz) {

        mClazz = clazz;
    }

    public static void addRespHandler(RespHandler handler) {

        if (handler == null)
            return;

        if (mRespHandlerList == null)
            mRespHandlerList = new ArrayList<>();

        mRespHandlerList.add(handler);
    }

    @Override
    public void onTaskPre() {

    }

    /**
     * 接收到网络请求返回数据
     * <p/>
     * 在这里可以模拟网络状态不理想的情况
     *
     * @param jsonText
     * @return
     */
    @Override
    public ExResponse<T> onTaskResponse(HttpTask ht, String jsonText) {

        jsonText = callbackJzydHttpTaskResponse(ht, jsonText);

        ExResponse<T> resp = new ExResponse<T>();

        if (LogMgr.isDebug())
            LogMgr.d("response : " + jsonText);

        if (TextUtils.isEmpty(jsonText)) { //返回为空

            resp.setJsonBrokenStatus();
            return resp;
        }

        try {

            JSONObject jsonObj = new JSONObject(jsonText);
            if (LogMgr.isDebug())
                LogMgr.d(JzydJsonListener.class.getSimpleName(), "onTaskResponse = " + jsonObj.toString());

            if (jsonObj.has("code"))
                resp.setCode(jsonObj.getString("code"));

            if (jsonObj.has("message"))
                resp.setMessage(jsonObj.getString("message"));

            if (resp.isSuccess()) {

                if (jsonObj.has("data"))
                    jsonText = jsonObj.getString("data");
                else
                    jsonText = TextUtil.TEXT_EMPTY;

                if (TextUtils.isEmpty(jsonText)) {

                    resp.setData((T) mClazz.newInstance());

                } else {

                    if (jsonText.startsWith("["))
                        resp.setData(((T) JSON.parseArray(jsonText, mClazz)));// JsonArray
                    else if (jsonText.startsWith("{"))
                        resp.setData((T) JSON.parseObject(jsonText, mClazz));// JsonObj
                    else
                        resp.setData((T) mClazz.newInstance());// new default obj
                }
            }

        } catch (Throwable t) {

            resp.setJsonBrokenStatus();
            if (LogMgr.isDebug())
                t.printStackTrace();

            callbackJzydHttpTaskResponseError(ht, t, resp == null ? 0 : resp.getIntCode());
        }

        //子线程回调
        try {

            if (resp.isSuccess() && resp.getData() != null) {

                callbabckJzydHttpTaskResponseSuccess(resp);

                onTaskResultDoInBackground(resp);
                onTaskResultDoInBackground(resp.getData());
            }

        } catch (ClassCastException e) {

            if (LogMgr.isDebug())
                LogMgr.e(JzydJsonListener.class.getSimpleName(), "onTaskResponse json obj cost exception " + e.getMessage());
        }

        return resp;
    }

    public void onTaskResultDoInBackground(ExResponse<T> resp) {

    }

    public void onTaskResultDoInBackground(T t) {

    }

    @Override
    public boolean onTaskSaveCache(ExResponse<T> resp) {

        return false;
    }

    @Override
    public void onTaskSuccess(ExResponse<T> resp) {

        if (resp.isSuccess() && resp.getData() != null) {

            try {

                onTaskResult(resp.getData());

            } catch (ClassCastException e) {

                if (LogMgr.isDebug())
                    LogMgr.e(JzydJsonListener.class.getSimpleName(), "onTaskSuccess json obj cost exception " + e.getMessage());

                onTaskFailed(TASK_FAILED_RESPONSE_PARSE_ERROR, TextUtil.TEXT_EMPTY);
            }

        } else {

            if (resp.isJsonBroken()) {

                onTaskFailed(TASK_FAILED_RESPONSE_PARSE_ERROR, TextUtil.TEXT_EMPTY);
            } else {

                onTaskFailed(resp.getIntCode(), resp.getMessage());
            }
        }
    }

    public abstract void onTaskResult(T result);

    @Override
    public void onTaskFailed(int failedCode) {

        onTaskFailed(failedCode, TextUtil.TEXT_EMPTY);
    }

    public abstract void onTaskFailed(int failedCode, String msg);

    @Override
    public void onTaskAbort() {

        //nothing
    }

    private String callbackJzydHttpTaskResponse(HttpTask ht, String jsonText) {

        for (RespHandler handler : mRespHandlerList)
            jsonText = handler.onJzydHttpTaskResponse(ht, jsonText);

        return jsonText;
    }

    private void callbabckJzydHttpTaskResponseSuccess(ExResponse<?> resp) {

        for (RespHandler handler : mRespHandlerList) {

            handler.onJzydHttpTaskResponseSuccess(resp);
        }
    }

    private void callbackJzydHttpTaskResponseError(HttpTask ht, Throwable t, int statusCode) {

        for (RespHandler handler : mRespHandlerList) {

            handler.onJzydHttpTaskResponseError(ht, t, statusCode);
        }
    }

    public interface RespHandler {

        String onJzydHttpTaskResponse(HttpTask ht, String jsonText);

        void onJzydHttpTaskResponseSuccess(ExResponse<?> resp);

        void onJzydHttpTaskResponseError(HttpTask ht, Throwable t, int statusCode);
    }
}
