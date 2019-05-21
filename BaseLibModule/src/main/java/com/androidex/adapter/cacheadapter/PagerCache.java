package com.androidex.adapter.cacheadapter;

/**
 * PagerAdapter 内容页缓存数据
 *
 * @author : pzwwei
 * @crate : 2016 - 10 - 12 下午6:38
 */

public class PagerCache {

    private int pageIndex;//列表翻页索引
    private Object object;//缓存数据
    private int scrollY;

    public int getPageIndex() {

        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {

        this.pageIndex = pageIndex;
    }

    public Object getObject() {

        return object;
    }

    public void setObject(Object object) {

        this.object = object;
    }

    public int getScrollY() {

        return scrollY;
    }

    public void setScrollY(int scrollY) {

        this.scrollY = scrollY;
    }
}
