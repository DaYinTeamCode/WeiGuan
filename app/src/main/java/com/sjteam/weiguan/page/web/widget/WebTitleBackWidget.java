package com.sjteam.weiguan.page.web.widget;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidex.plugin.ExLayoutWidget;
import com.androidex.util.DensityUtil;
import com.androidex.util.VglpUtil;
import com.androidex.util.ViewUtil;
import com.sjteam.weiguan.R;

/**
 * 浏览器标题栏组件
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:10 PM
 */
public class WebTitleBackWidget extends ExLayoutWidget {

    private WebTitleLitener mLisn;
    private ImageView mIvClose;

    public WebTitleBackWidget(Activity activity) {

        super(activity);
    }

    public WebTitleBackWidget(Activity activity, int backResId, int closeResId) {

        super(activity, backResId, closeResId);
    }

    public void setWebTitleListener(WebTitleLitener onWebTitleClick) {

        mLisn = onWebTitleClick;
    }

    @Override
    protected View onCreateView(Activity activity, ViewGroup parent, Object... args) {

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(VglpUtil.getLllpWM());

        int backResId = R.mipmap.ic_title_bar_back_gray;
        int closeResId = R.mipmap.ic_title_webview_close;
        if (args != null && args.length > 1) {

            backResId = initResId(backResId, (int) args[0]);
            closeResId = initResId(closeResId, (int) args[1]);
        }

        ImageView ivBack = initSubImageView(backResId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLisn != null)
                    mLisn.onTitleBackClick();
            }
        });

        ivBack.setPadding(DensityUtil.dip2px(7f), 0, 0, 0);
        linearLayout.addView(ivBack, VglpUtil.getLllpSS(DensityUtil.dip2px(36), VglpUtil.M));

        mIvClose = initSubImageView(closeResId, new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mLisn != null)
                    mLisn.onTitleCloseClick();
            }
        });

        mIvClose.setPadding(0, 0, DensityUtil.dip2px(4f), 0);
        linearLayout.addView(mIvClose, VglpUtil.getLllpSS(DensityUtil.dip2px(36), VglpUtil.M));

        return linearLayout;
    }

    private int initResId(int srcId, int resId) {

        if (resId != 0)
            srcId = resId;

        return srcId;
    }

    private ImageView initSubImageView(int resuroce, View.OnClickListener onclickListener) {

        ImageView ivBack = new ImageView(getActivity());
        ivBack.setImageResource(resuroce);
        ivBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivBack.setOnClickListener(onclickListener);
        return ivBack;
    }

    public void showClose() {

        ViewUtil.showView(mIvClose);
    }

    public void hideClose() {

        ViewUtil.hideView(mIvClose);
    }

    public interface WebTitleLitener {

        void onTitleBackClick();

        void onTitleCloseClick();

    }
}
