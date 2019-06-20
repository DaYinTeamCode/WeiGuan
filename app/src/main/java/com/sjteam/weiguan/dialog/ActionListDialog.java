package com.sjteam.weiguan.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androidex.util.DensityUtil;
import com.sjteam.weiguan.R;
import com.sjteam.weiguan.view.CpTextView;

import java.util.List;

/**
 * Create By DaYin(gaoyin_vip@126.com) on 2019/6/20 7:05 PM
 */
public class ActionListDialog extends CpBaseDialog {

    private String[] datas;
    private DialogItemClick dialogItemClick;
    private int mHighLightPosition = -1;

    public ActionListDialog(Context context, String[] datas) {

        super(context);
//		setOwnerActivity(activity);
        this.datas = datas;
    }

    public ActionListDialog(Context context, List<String> data) {

        super(context);
        this.datas = data.toArray(new String[data.size()]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_action_list);
        getWindow().setGravity(Gravity.CENTER);
        initContentView();
    }

    private void initContentView() {

//		Resources.Theme theme = getOwnerActivity().getTheme();
        int textColor = 0XFF444444;//ThemeBuilder.getAttr(theme, R.attr.text_title_color);
        int splitColor = 0XFFF4F4F4;//ThemeBuilder.getAttr(theme, R.attr.nm_cm_split_color);
        int selectorResId = 0;//ThemeBuilder.getAttrResId(theme, R.attr.selector_item_color);
        LinearLayout linContent = (LinearLayout) findViewById(R.id.linContent);
        for (int i = 0; i < datas.length; i++) {

            final int position = i;
            CpTextView fontText = new CpTextView(getContext());
            if (i == mHighLightPosition) {

                fontText.setTextColor(0XFFEC5252);
            } else {

                fontText.setTextColor(textColor);
            }

            fontText.setText(datas[i]);
            fontText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fontText.setPadding(DensityUtil.dip2px(16f), DensityUtil.dip2px(16f), DensityUtil.dip2px(16f), DensityUtil.dip2px(16f));

            View line = new View(getContext());
            line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(splitColor);

            linContent.addView(fontText, params);
            linContent.addView(line);

            fontText.setBackgroundResource(selectorResId);

            fontText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    callBackItemClick(position, datas[position], v);
                }
            });
        }

    }

    public void setDialogItemClick(DialogItemClick dialogItemClick) {

        this.dialogItemClick = dialogItemClick;
    }

    public void callBackItemClick(int position, String text, View view) {

        if (dialogItemClick != null)
            dialogItemClick.onDialogItemClick(this, position, text, view);
    }

    public String[] getTextArray() {

        return this.datas;
    }

    public void setHighLightPosition(int position) {

        mHighLightPosition = position;
    }

    public interface DialogItemClick {

        void onDialogItemClick(CpBaseDialog dialog, int position, String text, View view);
    }
}
