package com.sjteam.weiguan.page.login.bean;

import com.androidex.zbuild.IKeepSource;

/**
 * ===========================================================
 * ***********************************************************
 * 作    者：大印
 * Git 地址:https://github.com/DaYinTeamCode
 * 邮    箱：gaoyin_vip@126.com
 * 版    本：1.0
 * 创建日期：2018/6/15 下午1:59
 * 描    述： 用户微信绑定回调实体类
 * <p>
 * 修订历史：
 * <p>
 * ***********************************************************
 * ===========================================================
 */
public class WxBind implements IKeepSource {

    /*** 微信OpenId */
    private String token;
    /*** 微信UnionId */
    private String unionId;
    /*** 微信昵称 */
    private String nickName;
    /*** 微信头像 */
    private String headImageUrl;

    public String getUnionId() {

        return unionId;
    }

    public void setUnionId(String unionId) {

        this.unionId = unionId;
    }

    public String getNickName() {

        return nickName;
    }

    public void setNickName(String nickName) {

        this.nickName = nickName;
    }

    public String getHeadImageUrl() {

        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {

        this.headImageUrl = headImageUrl;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }
}
