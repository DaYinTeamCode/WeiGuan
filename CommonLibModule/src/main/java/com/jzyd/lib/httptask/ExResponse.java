package com.jzyd.lib.httptask;


import com.androidex.util.TextUtil;


/**
 * 网络请求返回数据封装
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 4:01 PM
 */
public class ExResponse<T> {

    public static final String STATUS_JSON_BROKEN = "-1";
    public static final String STATUS_SUCCESS = "1";

    private String code;
    private String message = TextUtil.TEXT_EMPTY;
    private T data;

    private String version;

    public ExResponse() {

    }

    public String getCode() {

        return code;
    }

    public int getIntCode() {

        return Integer.valueOf(code);
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = TextUtil.filterNull(message);
    }

    public void setData(T data) {

        this.data = data;
    }

    public T getData() {

        return data;
    }


    //******************** 帮助函数 *********************


    public void setJsonBrokenStatus() {


        this.code = STATUS_JSON_BROKEN;
    }

    public boolean isSuccess() {

        return STATUS_SUCCESS.equalsIgnoreCase(this.code);
    }

    public boolean isJsonBroken() {

        return STATUS_JSON_BROKEN.equalsIgnoreCase(this.code);
    }
}
