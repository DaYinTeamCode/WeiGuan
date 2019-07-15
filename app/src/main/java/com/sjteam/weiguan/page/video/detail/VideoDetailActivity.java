package com.sjteam.weiguan.page.video.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sjteam.weiguan.R;
import com.sjteam.weiguan.page.aframe.CpFragmentActivity;
import com.sjteam.weiguan.page.login.UserLoginFragment;

/**
 * 视频播放详情页
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/26 12:04 PM
 */
public class VideoDetailActivity extends CpFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentFragment(UserLoginFragment.newInstance(this));
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

    public static void startActivity(Activity activity) {

        Intent intent = new Intent();
        intent.setClass(activity, VideoDetailActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.push_exit_stop);
    }
}
