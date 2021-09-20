package com.amherickeyboard.voicetyping.translator.InterstitialAds

import android.app.Activity
import android.content.Context
import android.util.Log
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.isInterstitialShown
import com.amherickeyboard.voicetyping.translator.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

open class InterstitialAdUpdated {

    var mInterstitialAd: InterstitialAd? = null
        private set

    companion object {
        @Volatile
        private var instance: InterstitialAdUpdated? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: InterstitialAdUpdated().also { instance = it }
                }

        var count = 0
    }

    fun loadInterstitialAd(context: Context) {

        context.let {

            InterstitialAd.load(
                    it, it.getString(R.string.admob_interistitial),
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {

                        override fun onAdFailedToLoad(ad: LoadAdError) {
                            count++
                            if (count < 3)
                                loadInterstitialAd(context)
                        }

                        override fun onAdLoaded(ad: InterstitialAd) {
                            mInterstitialAd = ad
                            mInterstitialAd?.fullScreenContentCallback =
                                    object : FullScreenContentCallback() {

                                        override fun onAdDismissedFullScreenContent() {
                                            isInterstitialShown = false
                                            mInterstitialAd = null
                                            loadInterstitialAd(context)
                                            Log.e("InterstiatialReload", "Reloaded___")

                                        }

                                        override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                                            isInterstitialShown = false
                                            super.onAdFailedToShowFullScreenContent(p0)

                                        }

                                        override fun onAdImpression() {
                                            isInterstitialShown = true
                                            super.onAdImpression()
                                        }


                                    }
                            Log.e("Interstitial____", "AdLoaded____")
                        }
                    })

        }


    }


    // to show Interstitial Ad Activity reference must be given

    fun showInterstitialAdNew(activity: Activity) {
        activity.let {
            mInterstitialAd?.show(it)
        }

    }

}

//InterstitialAdUpdated.getInstance().loadInterstitialAd(this@App)
//InterstitialAdUpdated.getInstance().showInterstitialAdNew(this)