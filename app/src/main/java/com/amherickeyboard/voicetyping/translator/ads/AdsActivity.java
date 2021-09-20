package com.amherickeyboard.voicetyping.translator.ads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated;

public class AdsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InterstitialAdUpdated.Companion.getInstance().showInterstitialAdNew(this);
        finish();
    }
}
