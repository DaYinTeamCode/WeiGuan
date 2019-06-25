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
        userItemSet.setItemType(UserItemSet.SHREAD_APP_TYPE);
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("小游戏");
        userItemSet.setItemType(UserItemSet.SMALL_GAME_TYPE);
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("五星好评");
        userItemSet.setItemType(UserItemSet.PRAISE_TYPE);
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("帮助与反馈");
        userItemSet.setItemType(UserItemSet.HELP_FEED_BACK_TYPE);
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("检测更新");
        userItemSet.setItemType(UserItemSet.CHECK_UPDATE_APP);
        objects.add(userItemSet);

        userItemSet = new UserItemSet();
        userItemSet.setTitle("关于微观短视频");
        userItemSet.setItemType(UserItemSet.ABOUT_APP_TYPE);
        objects.add(userItemSet);

        return objects;
    }

}
