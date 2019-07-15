package com.sjteam.weiguan.httptask.jsonresult;

import com.androidex.zbuild.IKeepSource;

import java.util.List;

/**
 * 字符串数组结果集
 * Created by yihaibin on 2017/3/23.
 */
public class StringListResult implements IKeepSource {
    private List<String> list;

    public List<String> getList() {

        return list;
    }

    public void setList(List<String> list) {

        this.list = list;
    }
}
