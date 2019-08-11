package com.sjteam.weiguan.utils;


import com.sjteam.weiguan.R;
import com.sjteam.weiguan.app.WgApp;

public class CpResUtil {

    private static int mTitleHeight;
    private static int mMainTabHeight;

    public static int getTitleBarHeight() {

        if (mTitleHeight == 0) {

            mTitleHeight = WgApp.getContext().getResources().getDimensionPixelSize(R.dimen.cp_title_bar_height);
        }
        return mTitleHeight;
    }

    public static int getMainTabHeight() {

        if (mMainTabHeight == 0) {

            mMainTabHeight = WgApp.getContext().getResources().getDimensionPixelSize(R.dimen.cp_page_main_act_menu_bar_height);
        }
        return mMainTabHeight;
    }
}
