package com.androidex.view.cardview;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Interface provided by CardView to implementations.
 * <p>
 * Necessary to resolve circular dependency between base CardView and platform implementations.
 */
interface CardViewDelegate {
    Drawable getCardBackground();

    void setCardBackground(Drawable drawable);

    boolean getUseCompatPadding();

    boolean getPreventCornerOverlap();

    void setShadowPadding(int left, int top, int right, int bottom);

    void setMinWidthHeightInternal(int width, int height);

    View getCardView();
}
