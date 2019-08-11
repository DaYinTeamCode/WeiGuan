package com.sjteam.weiguan.page.video.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.imageloader.fresco.FrescoImageView;
import com.androidex.util.TextUtil;
import com.androidex.widget.rv.vh.ExRvItemViewHolderBase;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoResult;

import java.text.DecimalFormat;

/**
 * 短视频双排卡片
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/9 9:29 PM
 */
public class VideoDcViewHolder extends ExRvItemViewHolderBase {

    /*** 1位小数格式 */
    private static DecimalFormat mNumberFraction2Digitsformater = new DecimalFormat("#.#");
    //无小数格式
    private static DecimalFormat mNumberFractionNoDigitsformater = new DecimalFormat("#");

    private FrescoImageView mAivCover, mFiAuthorIcon;
    private TextView mTvDesc, mTvLikes, mTvComment, mTvShare;
    private int mImageLength;

    public VideoDcViewHolder(ViewGroup viewGroup, int imageLength) {

        super(viewGroup, R.layout.page_item_video_dc_vh);
        mImageLength = imageLength;
    }

    @Override
    protected void initConvertView(View convertView) {

        convertView.setOnClickListener(this);
        View cDiv = convertView.findViewById(R.id.cDiv);
        cDiv.getLayoutParams().width = mImageLength;

        mAivCover = convertView.findViewById(R.id.aivCover);
        mAivCover.getLayoutParams().width = mImageLength;
        mAivCover.getLayoutParams().height = (int) (mImageLength * 1.7);

        mTvLikes = convertView.findViewById(R.id.tvLikes);
        mTvDesc = convertView.findViewById(R.id.tvDesc);
        mFiAuthorIcon = convertView.findViewById(R.id.fiAuthorIcon);
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
        mAivCover.setImageUriByLp(feedsVideoResult.getShowUrls());
        mTvDesc.setText(replaceText(feedsVideoResult.getContent()));
        mTvLikes.setText(formatSalesNumber(feedsVideoResult.getLikesCount()));
        mFiAuthorIcon.setImageUriByLp(feedsVideoResult.getAuthorIcon());
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
