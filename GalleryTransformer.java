package com.example.npm;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class GalleryTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.5F;

    @Override
    public void transformPage(View page, float position) {
        if (position < 0F) {
            page.setScaleX(MIN_SCALE * position + 1.5F);
            page.setScaleY(MIN_SCALE * position + 1.5F);
            page.setAlpha(MIN_SCALE * position + 1);
            page.setRotation(30 * position);
        } else {
            page.setScaleX(-MIN_SCALE * position + 1.5F);
            page.setScaleY(-MIN_SCALE * position + 1.5F);
            page.setAlpha(-MIN_SCALE * position + 1);
            page.setRotation(30 * position);
        }

        page.setElevation(page.getScaleX());
    }
}
