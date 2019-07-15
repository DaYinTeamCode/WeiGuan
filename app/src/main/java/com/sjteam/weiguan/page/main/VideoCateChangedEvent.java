package com.sjteam.weiguan.page.main;

import com.androidex.zbuild.IKeepSource;

/**
 * 视频分类Tab改变事件
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/10 11:41 AM
 */
public class VideoCateChangedEvent implements IKeepSource {

    private int postion;

    public VideoCateChangedEvent(int postion) {

        this.postion = postion;
    }

    public int getPostion() {

        return postion;
    }

    public void setPostion(int postion) {

        this.postion = postion;
    }
}
