package com.amherickeyboard.voicetyping.translator.NativeAds

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import com.amherickeyboard.voicetyping.translator.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView


class NativeAd {
    var currentNativeAd: NativeAd? = null

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }



        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd()
                }
            }
        }
    }

    fun refreshAd(
        context: Context,
        adView: NativeAdView,
        ad_frame: FrameLayout,
        shimmer: ShimmerFrameLayout
    ) {
        shimmer.startShimmer()

        with(context)
        {
            val builder = AdLoader.Builder(this, getString(R.string.admob_native_ad_id))
                .forNativeAd { nativeAd ->
                    currentNativeAd?.destroy()
                    currentNativeAd = nativeAd
                    populateNativeAdView(nativeAd, adView)
                    ad_frame.removeAllViews()
                    ad_frame.addView(adView)

                }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {}

                override fun onAdLoaded() {
                    shimmer.visibility = View.GONE
                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun refreshAdSmall(context: Context, adView: NativeAdView, ad_frame: FrameLayout) {

        with(context)
        {
            val builder = AdLoader.Builder(this, getString(R.string.admob_native_ad_id))
                .forNativeAd { nativeAd ->
                    currentNativeAd?.destroy()
                    currentNativeAd = nativeAd
                    populateNativeAdView(nativeAd, adView)
                    ad_frame.removeAllViews()
                    ad_frame.addView(adView)
                }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun refreshAdSmallRecycle(
        context: Context,
        adView: NativeAdView,
        shimmer: ShimmerFrameLayout,
        adCard: NativeAdView
    ) {
        shimmer.startShimmer()
        with(context)
        {
            val builder = AdLoader.Builder(this, getString(R.string.admob_native_ad_id))
                .forNativeAd { nativeAd ->
                    currentNativeAd?.destroy()
                    currentNativeAd = nativeAd
                    populateNativeAdView(nativeAd, adView)
                }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                }

                override fun onAdLoaded() {
                    shimmer.visibility = View.GONE
                    adCard.visibility = View.VISIBLE

                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())

        }
    }
}