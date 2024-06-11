package com.tom.npm;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

public class Utils {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size loadWinSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return new Size(outMetrics.widthPixels, outMetrics.heightPixels);
        }
        return null;  // Return null explicitly if wm is null
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size fitPhotoSize(StreamConfigurationMap map, Size mWinSize) {
        if (mWinSize == null) {
            return null;  // Return null or handle appropriately if mWinSize is null
        }

        List<Size> sizes = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));
        if (sizes == null || sizes.isEmpty()) {
            return null;  // Return null if no sizes are available
        }

        int minIndex = 0;
        int minDx = Integer.MAX_VALUE;
        int minDy = Integer.MAX_VALUE;
        int[] dxs = new int[sizes.size()];
        int justW = mWinSize.getHeight() * 2;
        int justH = mWinSize.getWidth() * 2;

        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            if (size != null) {
                dxs[i] = size.getWidth() - justW;
            }
        }

        for (int i = 0; i < dxs.length; i++) {
            int abs = Math.abs(dxs[i]);
            if (abs < minDx) {
                minIndex = i;
                minDx = abs;
            }
        }

        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            if (size != null && size.getWidth() == sizes.get(minIndex).getWidth()) {
                int dy = Math.abs(justH - size.getHeight());
                if (dy < minDy) {
                    minIndex = i;
                    minDy = dy;
                }
            }
        }

        return sizes.get(minIndex);
    }
}
