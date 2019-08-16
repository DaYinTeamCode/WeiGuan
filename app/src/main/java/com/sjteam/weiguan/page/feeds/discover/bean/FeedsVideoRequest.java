package com.sjteam.weiguan.page.feeds.discover.bean;

import com.androidex.zbuild.IKeepSource;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019-08-16 12:47
 */
public class FeedsVideoRequest implements IKeepSource {

    private int pageNum;

    private int pageSize;

    private String showType;

    public int getPageNum() {

        return pageNum;
    }

    public void setPageNum(int pageNum) {

        this.pageNum = pageNum;
    }

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize(int pageSize) {

        this.pageSize = pageSize;
    }

    public String getShowType() {

        return showType;
    }

    public void setShowType(String showType) {

        this.showType = showType;
    }
}
