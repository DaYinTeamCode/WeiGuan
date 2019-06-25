package com.sjteam.weiguan.page.me.bean;

/**
 * 用户中心Item 数据
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 6:15 PM
 */
public class UserItemSet {

    /*** 分享App给好友 */
    public static final int SHREAD_APP_TYPE = 1;
    /*** 小游戏 */
    public static final int SMALL_GAME_TYPE = 2;
    /*** 五星好评 */
    public static final int PRAISE_TYPE = 3;
    /*** 帮助与反馈 */
    public static final int HELP_FEED_BACK_TYPE = 4;
    /*** 关于微观短视频 */
    public static final int ABOUT_APP_TYPE = 5;
    /*** 设置 */
    public static final int SETTING_APP_TYPE = 6;

    /*** 检测更新 */
    public static final int CHECK_UPDATE_APP = 7;

//    /*** 清理缓存 */
//    public static final int

    private int itemType;

    private String title;

    private int iconRes;

    public int getItemType() {

        return itemType;
    }

    public void setItemType(int itemType) {

        this.itemType = itemType;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public int getIconRes() {

        return iconRes;
    }

    public void setIconRes(int iconRes) {

        this.iconRes = iconRes;
    }
}
