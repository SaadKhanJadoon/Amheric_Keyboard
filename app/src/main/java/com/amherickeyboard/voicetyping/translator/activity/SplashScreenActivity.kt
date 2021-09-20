package com.amherickeyboard.voicetyping.translator.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.isSplashScreenActivity
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    lateinit var adFrame: FrameLayout
    lateinit var nativeAd: NativeAd
    private val buttonClick = AlphaAnimation(5f, 0.8f)
    private var permissionGranted = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        requestRecordAudioPermission()
        isSplashScreenActivity = true;

        val prefs = getSharedPreferences("finishKeyboard", MODE_PRIVATE)
        finishButton = prefs.getBoolean("finishKeyboard", false)

        GlobalScope.launch(Dispatchers.Main) {
            adFrame = findViewById(R.id.ad_frame_splash)
            val nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
            val container = findViewById<ShimmerFrameLayout>(R.id.shimmerEffectSplash)
            nativeAd = NativeAd()
            nativeAd.refreshAd(this@SplashScreenActivity, nativeAdView, adFrame, container)
        }

        FirebaseAnalytics.getInstance(this)
        val progressbar = findViewById<ProgressBar>(R.id.progressBar)
        val letsStartBtn = findViewById<ImageView>(R.id.letsStartBtn)
        val letsStartText = findViewById<TextView>(R.id.textView5)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            progressbar.visibility = View.GONE
            letsStartBtn.visibility = View.VISIBLE
            letsStartText.visibility = View.VISIBLE
        }, 6000)
        findViewById<View>(R.id.letsStartBtn).setOnClickListener {
            it.startAnimation(buttonClick)
            if (finishButton) {
                val activityAdd = intent.getIntExtra("activityAddress", 0)
                val dialogIntent = Intent(applicationContext, HomeScreen::class.java)
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                dialogIntent.putExtra("activityAddress", activityAdd)
                startActivity(dialogIntent)
            } else {
                if (!permissionGranted) {
                    requestRecordAudioPermission()
                } else {
                    val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    MainActivity.isFirstTime = true
                    this@SplashScreenActivity.startActivity(i)
                    showInterstitialAd()
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // This method is called when the  permissions are given
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestRecordAudioPermission() {
        val requiredPermission = Manifest.permission.RECORD_AUDIO
        if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
        } else {
            Toast.makeText(
                this,
                "This app needs to record audio through the microphone",
                Toast.LENGTH_SHORT
            ).show()
            requestPermissions(arrayOf(requiredPermission), 101)
        }
    }

    private fun showInterstitialAd() {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(this)
    }

    companion object {
        var finishButton = false
    }
}