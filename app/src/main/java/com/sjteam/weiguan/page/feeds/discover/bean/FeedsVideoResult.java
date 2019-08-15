package com.sjteam.weiguan.page.feeds.discover.bean;

import com.androidex.util.TextUtil;
import com.androidex.zbuild.IKeepSource;

import java.io.Serializable;

/**
 * 视频流结果
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/11 4:15 PM
 */
public class FeedsVideoResult implements IKeepSource, Serializable {

    private String id;

    private String authorId;

    private String authorNickname;

    private String authorIcon;

    private String authorType;

    private String mediaType;

    private String showType;

    private String content;

    private String showUrls;

    private String openUrls;

    private int likesCount;

    private int commentsCount;

    private int sharesCount;

    private int browsesCount;

    private String channelId;

    private String channelType;

    private String coverPicSize;

    private int videoTotalTime;

    private int createTime;

    private long lastUpdateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = TextUtil.filterNull(authorId);
    }

    public String getAuthorNickname() {

        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {

        this.authorNickname = TextUtil.filterNull(authorNickname);
    }

    public String getAuthorIcon() {

        return authorIcon;
    }

    public void setAuthorIcon(String authorIcon) {

        this.authorIcon = TextUtil.filterNull(authorIcon);
    }

    public String getAuthorType() {

        return authorType;
    }

    public void setAuthorType(String authorType) {

        this.authorType = TextUtil.filterNull(authorType);
    }

    public String getMediaType() {

        return mediaType;
    }

    public void setMediaType(String mediaType) {

        this.mediaType = TextUtil.filterNull(mediaType);
    }

    public String getShowType() {

        return showType;
    }

    public void setShowType(String showType) {

        this.showType = TextUtil.filterNull(showType);
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = TextUtil.filterNull(content);
    }

    public String getShowUrls() {

        return showUrls;
    }

    public void setShowUrls(String showUrls) {

        this.showUrls = TextUtil.filterNull(showUrls);
    }

    public String getOpenUrls() {
        return openUrls;
    }

    public void setOpenUrls(String openUrls) {

        this.openUrls = TextUtil.filterNull(openUrls);
    }

    public int getLikesCount() {

        return likesCount;
    }

    public void setLikesCount(int likesCount) {

        this.likesCount = likesCount;
    }

    public int getCommentsCount() {

        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {

        this.commentsCount = commentsCount;
    }

    public int getSharesCount() {

        return sharesCount;
    }

    public void setSharesCount(int sharesCount) {

        this.sharesCount = sharesCount;
    }

    public int getBrowsesCount() {

        return browsesCount;
    }

    public void setBrowsesCount(int browsesCount) {

        this.browsesCount = browsesCount;
    }

    public String getChannelId() {

        return channelId;
    }

    public void setChannelId(String channelId) {

        this.channelId = TextUtil.filterNull(channelId);
    }

    public String getChannelType() {

        return channelType;
    }

    public void setChannelType(String channelType) {

        this.channelType = TextUtil.filterNull(channelType);
    }

    public String getCoverPicSize() {

        return coverPicSize;
    }

    public void setCoverPicSize(String coverPicSize) {

        this.coverPicSize = TextUtil.filterNull(coverPicSize);
    }

    public int getVideoTotalTime() {

        return videoTotalTime;
    }

    public void setVideoTotalTime(int videoTotalTime) {

        this.videoTotalTime = videoTotalTime;
    }

    public int getCreateTime() {

        return createTime;
    }

    public void setCreateTime(int createTime) {

        this.createTime = createTime;
    }

    public long getLastUpdateTime() {

        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {

        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {

        return "FeedsVideoResult{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorNickname='" + authorNickname + '\'' +
                ", authorIcon='" + authorIcon + '\'' +
                ", authorType='" + authorType + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", showType='" + showType + '\'' +
                ", content='" + content + '\'' +
                ", showUrls='" + showUrls + '\'' +
                ", openUrls='" + openUrls + '\'' +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                ", sharesCount=" + sharesCount +
                ", browsesCount=" + browsesCount +
                ", channelId='" + channelId + '\'' +
                ", channelType='" + channelType + '\'' +
                ", coverPicSize='" + coverPicSize + '\'' +
                ", videoTotalTime=" + videoTotalTime +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
