package com.sjteam.weiguan.page.video.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;
import com.sjteam.weiguan.page.feeds.discover.bean.FeedsVideoResult;

import java.io.Serializable;
import java.util.List;

/**
 * 视频播放详情页
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/26 12:04 PM
 */
public class VideoDetailActivity extends CpFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<FeedsVideoResult> feedsVideoResults = (List<FeedsVideoResult>) getIntent().getSerializableExtra("feedsVideo");
        int postion = getIntent().getIntExtra("postion", 0);
        setContentFragment(VideoDetailFragment.newInstance(this, feedsVideoResults, postion));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleView() {

    }

    @Override
    protected void initContentView() {

    }

    @Override
    public void finish() {

        super.finish();
        overridePendingTransition(R.anim.push_exit_stop, R.anim.alpha_out);
    }

    public static void startActivity(Activity activity, List<FeedsVideoResult> feedsVideoResults, int postion) {
        try {

            Intent intent = new Intent();
            intent.putExtra("feedsVideo", (Serializable) feedsVideoResults);
            intent.putExtra("postion", postion);
            intent.setClass(activity, VideoDetailActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.alpha_in, R.anim.push_exit_stop);
        } catch (Exception ex) {

        }
    }
}
