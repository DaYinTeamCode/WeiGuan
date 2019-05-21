package com.androidex.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.androidex.context.ExApplication;

/**
 * 剪贴板 操作
 * User: pzwwei
 * Date: 16/3/1
 * Time: 下午2:36
 * To change this template use File | Settings | File Templates.
 */
public class ClipBoardUtil {


    public static void copyText(String text) {

        try{

            ClipboardManager cm = (ClipboardManager) ExApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if(cm != null)
                cm.setPrimaryClip(ClipData.newPlainText(null, TextUtil.filterNull(text)));

        }catch (Exception e){

            //umeng bug: java.lang.IllegalArgumentException: Unknown package com.jzyd.coupon
        }
    }

    public static CharSequence getText() {

        try{

            ClipboardManager cm = (ClipboardManager) ExApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm == null)
                return TextUtil.TEXT_EMPTY;

            ClipData cd = cm.getPrimaryClip();
            if (cd == null)
                return TextUtil.TEXT_EMPTY;

            if (cd.getItemCount() > 0)
                return cd.getItemAt(0) == null ? TextUtil.TEXT_EMPTY : cd.getItemAt(0).getText();
            else
                return TextUtil.TEXT_EMPTY;

        }catch (Exception e){

            //umeng bug: java.lang.IllegalArgumentException: Unknown package com.jzyd.coupon
        }

        return TextUtil.TEXT_EMPTY;
    }
}
