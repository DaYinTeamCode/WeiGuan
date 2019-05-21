package com.androidex.adapter.cacheadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.androidex.adapter.ExFragmentPagerStateAdapter;
import com.androidex.util.LogMgr;

import java.util.HashMap;

/**
 * 缓存viewpager列表数据内容
 *
 * @author : pzwwei
 * @crate : 2016 - 10 - 12 下午6:38
 */
public abstract class BaseCachePagerAdapter<T> extends ExFragmentPagerStateAdapter<T> {

    private HashMap<Integer, PagerCache> mCache = new HashMap<>();
    private int mCacheId;

    public boolean putCache(int key, PagerCache value) {

        if (value == null) {

            return false;
        } else {

            mCache.put(key, value);
            return true;
        }
    }

    public PagerCache getCache(int key) {

        return mCache.get(key);
    }

    public void clearCache() {

        mCache.clear();
    }

    public BaseCachePagerAdapter(Context context, FragmentManager fmtMgr) {

        super(context, fmtMgr);
        setFragmentItemDataSetChangedEnable(true);//设置notify时销毁之前的fragment，进行重新创建刷新页面数据
    }

    @Override
    public Fragment getItem(int position) {

        IBaseCacheFra baseCacheFra = newInstanceCacheFra(mCacheId, position);

        if (LogMgr.isDebug())
            LogMgr.d("getItem cacheId : " + mCacheId + " PageCacheId : " + baseCacheFra.getPageCacheId() + " position : " + position);

        if (mCacheId == baseCacheFra.getPageCacheId())
            baseCacheFra.setPageCacheData(getCache(position));

        return (Fragment) baseCacheFra.getFragment();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        IBaseCacheFra fra = (IBaseCacheFra) object;
        if (fra == null)
            return;

        if (LogMgr.isDebug())
            LogMgr.d("destroyItem cacheId : " + mCacheId + " PageCacheId : " + fra.getPageCacheId() + " position : " + position);

        if (mCacheId == fra.getPageCacheId())
            putCache(position, fra.getPageCacheData());
    }

    @Override
    public void notifyDataSetChanged() {

        clearCache();
        mCacheId++;
        super.notifyDataSetChanged();
    }

    public HashMap<Integer, PagerCache> getCache() {

        return this.mCache;
    }

    public abstract IBaseCacheFra newInstanceCacheFra(int mCacheId, int position);
}
