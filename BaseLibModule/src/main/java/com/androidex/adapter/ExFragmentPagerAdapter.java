package com.androidex.adapter;

import java.util.ArrayList;
import java.util.List;

import com.androidex.util.LogMgr;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 该适配器适用于页面较少的情况:
 * 当第一次加载position对应的Fragment时，首先getItem()创建Fragment对象，并缓存起来，再走生命周期:onCreateView -> onActivityCreate() -> onStart()
 * 当要销毁时adapter回调destoryItem：
 * 		如果destoryItem正常完成，此时Fragment的生命周期是:onStop() -> onDestoryView()，但并没有销毁Fragment对象；
 * 		如果destoryItem未正常完成，则什么都不做;
 *
 * 当需要再次使用的时候，通过instantiateItem()，获取缓存Fragment：
 * 		如果之前正常destoryItem，不会再getItem()创建Fragment对象，直接走缓存的Fragment对象的onCreateView -> onActivityCreate() -> onStart
 * 		如果之前destoryItem未正常完成，则继续使用缓存的Fragment，且什么都不做
 *
 * 当调用NotifyDataSetChanged时，如果getItemPosition()返回POSITION_NONE时,
 * adapter会调用回调instantiateItem()，获取对应的缓存Fragment，回调 destoryItem：
 * 		如果destoryItem正常完成，此时Fragment的生命周期是:onStop() -> onDestoryView()，
 * 								 然后再 onCreateView -> onActivityCreate -> onStart()；
 *
 * 		如果destoryItem未正常完成：则什么都不做
 *
 * # destoryItem 正常与未正常指的是真正调用了父类的destoryItem方法
 * @author yhb
 */
public abstract class ExFragmentPagerAdapter<T> extends FragmentPagerAdapter{

    private Context mContext;
    private List<T> mData;
    private boolean mFragmentItemDataSetChangedEnable;
    private boolean mFragmentItemDestoryEnable = true;

    public ExFragmentPagerAdapter(Context context, FragmentManager fmtMgr) {

        super(fmtMgr);
        mContext = context;
    }

    @Override
    public int getItemPosition(Object object) {

        if(LogMgr.isDebug())
            LogMgr.d(getClass().getSimpleName(), "~~getItemPosition, obj = "+object.hashCode());

        return mFragmentItemDataSetChangedEnable ? POSITION_NONE : POSITION_UNCHANGED;
        //return super.getItemPosition(object);
        //POSITION_UNCHANGED super 默认实现返回的是该值，
        //调用notifyDataSetChanged是不会回调getItem等方法进行刷新的，这里返回NONE表示item需要更新
    }

    @Override
    public int getCount() {

        return mData == null ? 0 : mData.size();
    }

    public void setData(List<T> data){

        mData = data;
    }

    public List<T> getData(){

        return mData;
    }

    public void addAll(List<? extends T> data) {

        if (data == null)
            return;

        if (mData == null)
            mData = new ArrayList<T>();

        mData.addAll(data);
    }

    public void addAll(int position, List<? extends T> item) {

        if(item == null)
            return;

        if(mData == null)
            mData = new ArrayList<T>();

        if(position >= 0 && position <= mData.size())
            mData.addAll(position, item);
    }

    public boolean isEmpty(){

        return mData == null || mData.size() == 0 ? true : false;
    }

    public T getDataItem(int position) {

        return checkPosition(position) ? mData.get(position) : null;
    }

    public Context getContext(){

        return mContext;
    }

    public void setFragmentItemDataSetChangedEnable(boolean enable){

        mFragmentItemDataSetChangedEnable = enable;
    }

    public void setFragmentItemDestoryEnable(boolean enable){

        mFragmentItemDestoryEnable = enable;
    }

    /**
     * 新方法
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        return super.instantiateItem(container, position);
    }

    /**
     * 新方法
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if(LogMgr.isDebug())
            LogMgr.d(getClass().getSimpleName(), "~~destroyItem pos="+position+", fragment="+object.hashCode()+", destory enable="+mFragmentItemDestoryEnable);

        if(mFragmentItemDestoryEnable)
            super.destroyItem(container, position, object);
    }

    /**
     * 该方法已被弃用
     */
    @Override
    public void destroyItem(View container, int position, Object object) {

        if(mFragmentItemDestoryEnable)
            super.destroyItem(container, position, object);
    }

    /**
     * 该方法已被弃用
     */
    @Override
    public Object instantiateItem(View container, int position) {

        return super.instantiateItem(container, position);
    }

    public boolean checkPosition(int position){

        return position >= 0 && position < (mData == null ? 0 : mData.size());
    }

    public String simpleTag(){

        return getClass().getSimpleName();
    }
}
