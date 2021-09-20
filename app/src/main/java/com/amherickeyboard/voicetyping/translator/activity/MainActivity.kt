package com.amherickeyboard.voicetyping.translator.activity

import android.app.Activity
import android.content.*
import android.icu.text.DateTimePatternGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.isSplashScreenActivity
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.settingButtonPressed
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.UncachedInputMethodManagerUtils
import com.amherickeyboard.voicetyping.translator.firebase.Analytics
import com.amherickeyboard.voicetyping.translator.ime.SimpleIME
import com.amherickeyboard.voicetyping.translator.service.MyService
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.*

class MainActivity : AppCompatActivity(), InstallReferrerStateListener {

    var settingsButton: RelativeLayout? = null
    private var mReceiver: InputMethodChangeReceiver? = null
    private var mImm: InputMethodManager? = null
    var setupStep = 1
    private var referrerClient: InstallReferrerClient? = null
    private var sharedPreferences: SharedPreferences? = null
    private var adFrame: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isSplashScreenActivity = false;
        val analytics = Analytics(this@MainActivity)
        analytics.sendScreenAnalytics(this, "MainScreen")
        adFrame = findViewById(R.id.ad_frame_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbarKeyboard)
        toolbar.title = "Keyboard Settings"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient?.startConnection(this)
        mReceiver = InputMethodChangeReceiver()
        val filter = IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED)
        registerReceiver(mReceiver, filter)
        activity = this

        val nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        val nativeAd = NativeAd()
        val container = findViewById<ShimmerFrameLayout>(R.id.shimmerEffectMain)
        nativeAd.refreshAd(this, nativeAdView, adFrame!!, container)

        ///////////////     setup input and language activity launch        ////////////////////////
        mImm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        settingsButton = findViewById(R.id.setting_button)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        settingsButton?.setOnClickListener {
            if (setupStep == 1) {
                settingButtonPressed = true
                enableKeyBoard()
                val intent = Intent(
                    this@MainActivity,
                    MyService::class.java
                )
                startService(intent)
            } else {
                val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                im.showInputMethodPicker()
            }
        }
    }

    //////////////////////////////////      to check custom ime enabled or not       /////////////////////////////////
    val isInputMethodEnabled: Boolean
        get() {
            val id = Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )
            val defaultInputMethod = ComponentName.unflattenFromString(id)
            val myInputMethod = ComponentName(this, SimpleIME::class.java)
            return myInputMethod == defaultInputMethod
        }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                launchActivity<HomeScreen> {}
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onInstallReferrerSetupFinished(responseCode: Int) {
        when (responseCode) {
            DateTimePatternGenerator.PatternInfo.OK -> Log.i("Status", "Connection ok")
            InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> Log.i("Status", "Not Supported")
            InstallReferrerResponse.SERVICE_UNAVAILABLE -> Log.i("Status", "Unavailable")
        }
    }

    override fun onInstallReferrerServiceDisconnected() {}

    //////////////////////////      input method change receiver to listen ime changes      //////////////////////////////////////
    inner class InputMethodChangeReceiver : BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_INPUT_METHOD_CHANGED) {
                settingButtonPressed = false
                setupStep = if (isInputMethodEnabled) {
                    3
                } else {
                    2
                }
                setScreen()
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver) //  unregister receiver
        super.onDestroy()
    }

    ///////////////////////     launch input and language settings      //////////////////////////////////
    private fun enableKeyBoard() {
        startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
    }

    //////////////////////      to check custom keyboard is added to settings or not        /////////////////////////////////////
    private fun checkKeyboardExit() {
        if (UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            setupStep = 2
            if (isInputMethodEnabled) {
                setupStep = 3
            }
        } else {
            setupStep = 1
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onResume() {
        super.onResume()
        checkKeyboardExit()
        setScreen()
    }

    /////////////////////////      show current setup screen according to enabled/disabled settings     //////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setScreen() {
        when (setupStep) {
            1 -> setupScreen_1()
            2 -> setupScreen_2()
            3 -> setupScreen_3()
        }
    }

    ///////////////////////////     setup screen 1       ////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setupScreen_1() {
        (findViewById<View>(R.id.instructions_text_top) as TextView).setText(R.string.step_1_instructions)
    }

    ///////////////////////////     setup screen 2       ////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setupScreen_2() {
        (findViewById<View>(R.id.instructions_text_top) as TextView).setText(R.string.step_2_instructions)
        settingsButton!!.background = resources.getDrawable(R.drawable.ic_enable_keyboard)
    }

    ///////////////////////////     setup screen 3       ////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun setupScreen_3() {
        if (isFirstTime){
            val editor = getSharedPreferences("finishKeyboard", MODE_PRIVATE).edit()
            editor.putBoolean("finishKeyboard", true)
            editor.apply()
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
        (findViewById<View>(R.id.instructions_text_top) as TextView).setText(R.string.disable_message)
        settingsButton!!.background = resources.getDrawable(R.drawable.ic_disable_keyboard)
    }

    override fun onBackPressed() {
        launchActivity<HomeScreen> {}
    }

    private inline fun <reified T>launchActivity(intentFunc: Intent.() -> Unit?) {
        val intent = Intent(this@MainActivity, T::class.java)
        intent.intentFunc()
        startActivity(intent)
    }

    companion object {
        private var activity: Activity? = null
        const val PREFS_NAME = "AmhericKeyboard"
        var isFirstTime = false

        @JvmStatic
        fun finishActivity() {
            activity!!.finish()
        }
    }
}