package com.sjteam.weiguan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * 带字体的TextView
 */
public class CpTextView extends TextView {

    public CpTextView(Context context) {

        super(context);
        init();
    }

    public CpTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();

    }

    public CpTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        try {//java.lang.NullPointerException Attempt to invoke virtual method 'java.lang.CharSequence android.content.res.StringBlock.get(int)' on a null object reference

            setIncludeFontPadding(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        try {
            //vivo还是oppo 会报奇怪的错
            return super.onTextContextMenuItem(id);
        } catch (Exception e) {

            return false;
        }
    }
}
