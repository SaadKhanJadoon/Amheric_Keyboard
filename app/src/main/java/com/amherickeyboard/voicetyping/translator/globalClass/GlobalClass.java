package com.amherickeyboard.voicetyping.translator.globalClass;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp;
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated;

public class GlobalClass extends Application implements LifecycleObserver {
    public static boolean isFourgrounded = false;
    OpenApp openApp;

    @Override
    public void onCreate() {
        super.onCreate();
        InterstitialAdUpdated.Companion.getInstance().loadInterstitialAd(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        openApp = new OpenApp(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded(){
        isFourgrounded = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppFourgrounded(){
        isFourgrounded = true;
    }
}
