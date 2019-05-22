package com.jzyd.lib.httptask;


import com.androidex.util.TextUtil;

/**
 * 网络请求返回数据封装
 *
 * @param <T>
 * @author pzwwei
 * @version 1.0
 * @since 2014-12-16
 */
public class JzydResponse<T> {

    public static final int STATUS_JSON_BROKEN = -1;
    public static final int STATUS_SUCCESS = 1;

    private int status_code;
    private String message = TextUtil.TEXT_EMPTY;
    private long timestamp;
    private String trace_id = TextUtil.TEXT_EMPTY;
    private T data;

    public JzydResponse() {

    }

    public int getStatus_code() {

        return status_code;
    }

    public void setStatus_code(int status_code) {

        this.status_code = status_code;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = TextUtil.filterNull(message);
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;
    }

    public String getTrace_id() {

        return trace_id;
    }

    public void setTrace_id(String trace_id) {

        this.trace_id = TextUtil.filterNull(trace_id);
    }

    public void setData(T data) {

        this.data = data;
    }

    public T getData() {

        return data;
    }


    //******************** 帮助函数 *********************


    public void setJsonBrokenStatus() {

        this.status_code = STATUS_JSON_BROKEN;
    }

    public boolean isSuccess() {

        return status_code == STATUS_SUCCESS;
    }

    public boolean isJsonBroken() {

        return this.status_code == STATUS_JSON_BROKEN;
    }
}
