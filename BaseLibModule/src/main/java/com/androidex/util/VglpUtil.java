package com.androidex.util;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ViewGroup LayoutParams 工具类
 * Created by yihaibin on 15/10/25.
 */
public class VglpUtil {

    public static int M = ViewGroup.LayoutParams.MATCH_PARENT;
    public static int W = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * ViewGroup LayoutParams
     */

    public static ViewGroup.LayoutParams getVglpMM(){

        return new ViewGroup.LayoutParams(M, M);
    }

    public static ViewGroup.LayoutParams getVglpMW(){

        return new ViewGroup.LayoutParams(M, W);
    }

    public static ViewGroup.LayoutParams getVglpWW(){

        return new ViewGroup.LayoutParams(W, W);
    }

    public static ViewGroup.LayoutParams getVglpWM(){

        return new ViewGroup.LayoutParams(W, M);
    }

    public static ViewGroup.LayoutParams getVglpSS(int width, int height){

        return new ViewGroup.LayoutParams(width, height);
    }

    /**
     * FrameLayout LayoutParams
     */

    public static FrameLayout.LayoutParams getFllpMM(){

        return new FrameLayout.LayoutParams(M, M);
    }

    public static FrameLayout.LayoutParams getFllpMW(){

        return new FrameLayout.LayoutParams(M, W);
    }

    public static FrameLayout.LayoutParams getFllpWW(){

        return new FrameLayout.LayoutParams(W, W);
    }

    public static FrameLayout.LayoutParams getFllpWM(){

        return new FrameLayout.LayoutParams(W, M);
    }

    public static FrameLayout.LayoutParams getFllpSS(int width, int height){

        return new FrameLayout.LayoutParams(width, height);
    }

    public static FrameLayout.LayoutParams getFllpSS(int width, int height, int gravity){

        return new FrameLayout.LayoutParams(width, height, gravity);
    }

    /**
     * LinearLayout LayoutParams
     */

    public static LinearLayout.LayoutParams getLllpMM(){

        return new LinearLayout.LayoutParams(M, M);
    }

    public static LinearLayout.LayoutParams getLllpMM(int weight){

        return new LinearLayout.LayoutParams(M, M, weight);
    }

    public static LinearLayout.LayoutParams getLllpMW(){

        return new LinearLayout.LayoutParams(M, W);
    }

    public static LinearLayout.LayoutParams getLllpMW(int weight){

        return new LinearLayout.LayoutParams(M, W, weight);
    }

    public static LinearLayout.LayoutParams getLllpWW(){

        return new LinearLayout.LayoutParams(W, W);
    }

    public static LinearLayout.LayoutParams getLllpWM(){

        return new LinearLayout.LayoutParams(W, M);
    }

    public static LinearLayout.LayoutParams getLllpSS(int width, int height){

        return new LinearLayout.LayoutParams(width, height);
    }

    public static LinearLayout.LayoutParams getLllpSS(int width, int height, int weight){

        return new LinearLayout.LayoutParams(width, height, weight);
    }

    /**
     * RelativeLayout LayoutParams
     */

    public static RelativeLayout.LayoutParams getRllpMM(){

        return new RelativeLayout.LayoutParams(M, M);
    }

    public static RelativeLayout.LayoutParams getRllpMW(){

        return new RelativeLayout.LayoutParams(M, W);
    }

    public static RelativeLayout.LayoutParams getRllpWW(){

        return new RelativeLayout.LayoutParams(W, W);
    }

    public static RelativeLayout.LayoutParams getRllpWM(){

        return new RelativeLayout.LayoutParams(W, M);
    }

    public static RelativeLayout.LayoutParams getRllpSS(int width, int height){

        return new RelativeLayout.LayoutParams(width, height);
    }
}
