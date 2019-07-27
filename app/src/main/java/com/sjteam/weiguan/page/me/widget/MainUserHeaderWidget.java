package com.sjteam.weiguan.page.me.widget;

import android.app.Activity;

import android.graphics.drawable.Animatable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.imageloader.fresco.FrescoImageView;
import com.androidex.plugin.ExLayoutWidget;
import com.androidex.statusbar.StatusBarManager;
import com.androidex.util.DensityUtil;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.image.ImageInfo;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.constants.DimenConstant;
import com.sjteam.weiguan.page.login.bean.WxBind;
import com.sjteam.weiguan.page.login.prefs.AccountPrefs;

/**
 * 个人中心头部组件
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 4:05 PM
 */
public class MainUserHeaderWidget extends ExLayoutWidget implements View.OnClickListener {

    private FrescoImageView mAivBgPic;
    private FrescoImageView mAivAvater;
    private TextView mTvName;

    public MainUserHeaderWidget(Activity activity) {

        super(activity);
    }

    @Override
    protected View onCreateView(Activity activity, ViewGroup parent, Object... args) {

        View view = activity.getLayoutInflater().inflate(R.layout.activity_main_user_header_widget, parent);

        int paddingTop = StatusBarManager.getInstance().getStatusbarHeight(getActivity());
        mAivBgPic = view.findViewById(R.id.aivBgPic);
        mAivBgPic.setBaseControllerListener(new BaseControllerListener<ImageInfo>() {

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo != null && mAivBgPic != null) {

                    int height = (int) ((float) imageInfo.getHeight() * DimenConstant.SCREEN_WIDTH / imageInfo.getWidth());
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mAivBgPic.getLayoutParams();
                    layoutParams.height = height + paddingTop;
                }
            }
        });
        mAivBgPic.setBackgroundColor(0xFFFFFFFF);
        mAivAvater = view.findViewById(R.id.aivAvater);
        mAivAvater.setRoundingParams(RoundingParams.asCircle());
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mAivAvater.getLayoutParams();
        layoutParams.topMargin = DensityUtil.dip2px(26f) + paddingTop;

        mTvName = view.findViewById(R.id.tvName);
        mTvName.setText("点击登录");
        mTvName.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tvName) {

            callbackWidgetViewClickListener(v);
        }
    }

    /**
     * 刷新内容数据
     *
     * @param wxBind
     */
    public void invalidateContentView(WxBind wxBind) {

        if (wxBind != null) {

            mAivAvater.setImageUriByLp(wxBind.getHeadImageUrl());
            boolean isLogin = AccountPrefs.getInstance().isLogin();
            mTvName.setText(isLogin ? wxBind.getNickName() : "登录");
        }
    }
}
