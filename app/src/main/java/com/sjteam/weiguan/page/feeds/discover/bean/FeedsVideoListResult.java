package com.sjteam.weiguan.page.feeds.discover.bean;

import com.androidex.zbuild.IKeepSource;

import java.util.List;

/**
 * 视频流列表
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 4:14 PM
 */
public class FeedsVideoListResult implements IKeepSource {

    /*** 视频总数 */
    private int total;

    private List<FeedsVideoResult> list;

    public int getTotal() {

        return total;
    }

    public void setTotal(int total) {

        this.total = total;
    }

    public List<FeedsVideoResult> getList() {

        return list;
    }

    public void setList(List<FeedsVideoResult> list) {

        this.list = list;
    }

    @Override
    public String toString() {

        return "FeedsVideoListResult{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}
