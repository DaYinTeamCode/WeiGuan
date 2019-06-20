package com.sjteam.weiguan.page.me.utils;

import com.sjteam.weiguan.page.me.bean.UserItemSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心数据帮助类
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 6:21 PM
 */
public class MainUserDataUtil {


    public static List<Object> getUserItemDatas() {

        List<Object> objects = new ArrayList<>();

        UserItemSet userItemSet = new UserItemSet();
        userItemSet.setTitle("分享App给好友");
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("小游戏");
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("五星好评");
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("帮助与反馈");
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("关于微观短视频");
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("设置");
        objects.add(userItemSet);
        return objects;
    }

}
