package com.sjteam.weiguan.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.androidex.util.LogMgr;
import com.androidex.util.TextUtil;
import com.sjteam.weiguan.R;

public class CpBaseDialog extends Dialog {
    private static final String TAG = "CpBaseDialog";

    private String mTitleText = TextUtil.TEXT_EMPTY;
    private String mSubtitleText = TextUtil.TEXT_EMPTY;
    private CharSequence mContentText = TextUtil.TEXT_EMPTY;
    private Object mTagObj;

    public CpBaseDialog(Context context) {

        super(context, R.style.ex_theme_dialog);
    }

    public CpBaseDialog(Context context, int style) {

        super(context, style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onDialogCreate(savedInstanceState);
    }

    protected void onDialogCreate(Bundle savedInstanceState) {
    }

    public void setTitleText(int rid) {

        setTitleText(getContext().getString(rid));
    }

    public void setTitleText(String text) {

        mTitleText = TextUtil.filterNull(text);
    }

    public void setSubtitleText(String text) {

        mSubtitleText = TextUtil.filterNull(text);
    }

    public void setContentText(int rid) {

        setContentText(getContext().getString(rid));
    }

    public void setContentText(CharSequence text) {

        mContentText = text;
    }

    public void setTag(Object tagObj) {

        mTagObj = tagObj;
    }

    public Object getTag() {

        return mTagObj;
    }

    public void setTitleTextOnShowing(int rid) {

        setTitleTextOnShowing(getContext().getString(rid));
    }

    public void setTitleTextOnShowing(String text) {

    }

    public void setContentTextOnShowing(int rid) {

        setContentTextOnShowing(getContext().getString(rid));
    }

    public void setContentTextOnShowing(String text) {

    }

    public String getTitleText() {

        return mTitleText;
    }


    public String getSubTitleText() {

        return mSubtitleText;
    }

    public CharSequence getContentText() {

        return mContentText;
    }

    @Override
    public void dismiss() {

        try {
            super.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    public void cancel() {

        try {
            super.cancel();
        } catch (Exception e) {

        }

    }

    @Override
    public void show() {

        // 修复 BadToken 异常，当Activity正在Finishing时，不再弹出Dialog
        Context context = getContext();
        if (context == null) {
            if (LogMgr.isDebug()) {
                Log.e(TAG, "CpBaseDialog 在 Context Finishing 时，执行show操作！！！", new Exception());
            }
        }

        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            if (LogMgr.isDebug()) {
                Log.e(TAG, "CpBaseDialog 在 Context Finishing 时，执行show操作！！！", new Exception());
            }

        }

        Activity ownerActivity = getOwnerActivity();
        if (ownerActivity != null && ownerActivity.isFinishing()) {
            if (LogMgr.isDebug()) {
                Log.e(TAG, "CpBaseDialog 在 Context Finishing 时，执行show操作！！！", new Exception());
            }
        }

        try {
            super.show();
        } catch (Exception e) {
            if (LogMgr.isDebug()) {
                Log.e(TAG, "CpBaseDialog Exception :", e);
            }
        }
    }

    public static interface OnDialogClickListener {

        public void onClick(CpBaseDialog dialog);
    }
}
