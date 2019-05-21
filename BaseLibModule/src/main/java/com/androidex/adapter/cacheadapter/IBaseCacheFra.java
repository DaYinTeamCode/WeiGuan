package com.androidex.adapter.cacheadapter;

/**
 * viewpager 内容页存取缓存接口
 *
 * @author : pzwwei
 * @crate : 2016 - 10 - 12 下午4:53
 */

public interface IBaseCacheFra<Fragment> {

    void setPageCacheData(PagerCache value);

    PagerCache getPageCacheData();

    int getPageCacheId();

    Fragment getFragment();
}