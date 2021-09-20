package com.amherickeyboard.voicetyping.translator.activity

import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.isSplashScreenActivity
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.RateDialog

class HomeScreen : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var likeButton: ImageView
    private val buttonClick = AlphaAnimation(5f, 0.8f)

    companion object {
        var isHomeFragment = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        isSplashScreenActivity = false

        val toolbar = findViewById<Toolbar>(R.id.tb)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        likeButton = findViewById(R.id.likeButton)

        try {
            val activityAdd = intent.getIntExtra("activityAddress", 0)
            intentForActivity(activityAdd)
        } catch (e: Exception) {
            Log.e("TAG", "onCreate: $e")
        }

        likeButton.setOnClickListener {
            it.startAnimation(buttonClick)
            likeAlert()
        }
    }

    private fun intentForActivity(activityAddress: Int) {
        when (activityAddress) {
            1 -> {
                navController.navigate(R.id.action_homeFragment_to_conversationFragment)
            }
            2 -> {
                navController.navigate(R.id.action_homeFragment_to_textTranslateFragment)
            }
            3 -> {
                navController.navigate(R.id.action_homeFragment_to_dictionaryFragment)
            }
            4 -> {
                navController.navigate(R.id.action_homeFragment_to_themesFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (isHomeFragment) {
            exitAlert()
        } else {
            super.onBackPressed()
        }
    }

    private fun exitAlert() {
        val rateDialog = RateDialog(this)
        rateDialog.createRateUsDialog(true)
    }

    private fun likeAlert() {
        val rateDialog = RateDialog(this)
        rateDialog.createRateUsDialog(false)
    }
}