package com.sjteam.weiguan.page.aframe.viewer;


import com.sjteam.weiguan.page.aframe.HttpFrameFragment;
import com.sjteam.weiguan.page.aframe.presenter.CpBasePresenter;

public abstract class CpHttpFrameVFragmentViewer<T, PRESENTER extends CpBasePresenter> extends HttpFrameFragment<T> {

    private PRESENTER mPresenter;

    @Override
    protected void initDataPre() {

        super.initDataPre();
        onInitPresenter();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        callbackPresenterDetached();
    }

    /**
     * 默认返空，不要求子类强制实现
     *
     * @return
     */
    private void onInitPresenter() {

        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.onAttachedToViewer();
        }
    }

    protected PRESENTER initPresenter() {

        return null;
    }

    /**
     * 回调控制器页面销毁
     */
    private void callbackPresenterDetached() {

        if (mPresenter != null) {

            mPresenter.onDetachedFromViewer();
        }
    }

    protected PRESENTER getPresenter() {

        return mPresenter;
    }
}
