package com.sjteam.weiguan.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.util.DensityUtil;
import com.androidex.util.ToastUtil;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.view.toast.ExToast;


/**
 * 用户发布Dialog
 * <p>
 * Create By DaYin(gaoyin_vip@126.com) on 2019/7/9 6:44 PM
 */
public class PusblishDialog extends CpBaseDialog implements View.OnClickListener {

    private Activity mActivity;

    public PusblishDialog(Activity activity) {

        super(activity, R.style.cp_theme_dialog_push_btm);
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pushlish);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        initContentView();
    }

    @Override
    public void show() {

        super.show();
    }

    @Override
    public void dismiss() {

        super.dismiss();
    }

    private void initContentView() {

        ImageView imClose = findViewById(R.id.ivClose);
        imClose.setOnClickListener(this);

        TextView textView = findViewById(R.id.tvVideo);
        textView.setOnClickListener(this);

        textView = findViewById(R.id.tvImage);
        textView.setOnClickListener(this);

        textView = findViewById(R.id.tvText);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ivClose) {

            dismiss();
        } else {

            dismiss();
            ExToast.makeText("目前仅开放部分用户试用，敬请期待"
                    , Gravity.BOTTOM, DensityUtil.dip2px(60f)).show();
        }

    }
}
