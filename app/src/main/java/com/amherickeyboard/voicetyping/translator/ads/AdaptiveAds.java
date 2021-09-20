package com.amherickeyboard.voicetyping.translator.ads;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.AdSize;

public class AdaptiveAds {
    Context contextA;

    public AdaptiveAds(Context context) {
        this.contextA = context;
    }

    public AdSize getAdSize() {
        WindowManager window = (WindowManager) contextA.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(contextA, adWidth);
    }
}