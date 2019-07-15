package com.sjteam.weiguan.page.video.discover.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.imageloader.fresco.FrescoImageView;
import com.androidex.util.TextUtil;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.video.discover.bean.FeedsVideoResult;

import java.text.DecimalFormat;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/9 9:29 PM
 */
public class VideoDetailViewHolder extends ExRvItemViewHolderBase {

    /*** 1位小数格式 */
    private static DecimalFormat mNumberFraction2Digitsformater = new DecimalFormat("#.#");
    //无小数格式
    private static DecimalFormat mNumberFractionNoDigitsformater = new DecimalFormat("#");

    private FrescoImageView thumb, mFiHeaderImageUrl;
    private TextView mTvTitle, mTvDesc, mTvLikes, mTvComment, mTvShare;

    public VideoDetailViewHolder(ViewGroup viewGroup) {

        super(viewGroup, R.layout.item_video_vh);
    }

    @Override
    protected void initConvertView(View convertView) {

        thumb = itemView.findViewById(R.id.thumb);
        mTvTitle = itemView.findViewById(R.id.tvTitle);
        mTvDesc = itemView.findViewById(R.id.tvDesc);

        mTvLikes = itemView.findViewById(R.id.tvLikeCount);
        mTvComment = itemView.findViewById(R.id.tvCommentCount);
        mTvShare = itemView.findViewById(R.id.tvShareCount);
        mFiHeaderImageUrl = itemView.findViewById(R.id.fiHeaderImageUrl);
    }

    /***
     *  刷新VideHolder
     *
     * @param feedsVideoResult
     */
    public void invalidateView(FeedsVideoResult feedsVideoResult) {

        if (feedsVideoResult == null) {

            return;
        }

        thumb.setImageUriByLp(feedsVideoResult.getShowUrls());
        mTvTitle.setText(String.format("@%s", replaceText(feedsVideoResult.getAuthorNickname())));
        mTvDesc.setText(replaceText(feedsVideoResult.getContent()));

        mTvLikes.setText(formatSalesNumber(feedsVideoResult.getLikesCount()));
        mTvComment.setText(formatSalesNumber(feedsVideoResult.getCommentsCount()));
        mTvShare.setText(formatSalesNumber(feedsVideoResult.getSharesCount()));
        mFiHeaderImageUrl.setImageUriByLp(feedsVideoResult.getAuthorIcon());
    }

    /***
     *  替换文本内容
     *
     * @param content
     */
    private String replaceText(String content) {

        if (TextUtil.isEmpty(content)) {

            return content;
        }
        return content.replace("抖音", "微观");
    }

    public String formatSalesNumber(int sales) {

        if (sales >= 10000 && sales < 100000) {

            return mNumberFraction2Digitsformater.format(sales / 10000d) + "w";
        } else if (sales >= 100000) {

            return mNumberFractionNoDigitsformater.format(sales / 10000d) + "w";
        } else {
            return String.valueOf(sales);
        }
    }
}
