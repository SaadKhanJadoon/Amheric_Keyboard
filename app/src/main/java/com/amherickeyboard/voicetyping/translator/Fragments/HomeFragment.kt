package com.amherickeyboard.voicetyping.translator.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.amherickeyboard.voicetyping.translator.activity.MainActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView

class HomeFragment : Fragment() {
    lateinit var keyboardSettings: LinearLayout
    lateinit var textTranslate: LinearLayout
    lateinit var conversation: LinearLayout
    lateinit var textOnImages: LinearLayout
    lateinit var voiceDictionary: LinearLayout
    lateinit var keyboardThemes: LinearLayout
    lateinit var ad_frame: FrameLayout
    lateinit var nativeAd: NativeAd
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        isHomeFragment = true
        conversation = view.findViewById(R.id.conversationCard)
        textTranslate = view.findViewById(R.id.textTranslateCard)
        textOnImages = view.findViewById(R.id.imagesOnTextCard)
        keyboardSettings = view.findViewById(R.id.settingsCard)
        voiceDictionary = view.findViewById(R.id.voiceDictionaryCard)
        keyboardThemes = view.findViewById(R.id.themesCard)
        ad_frame = view.findViewById(R.id.ad_frame_home)

        conversation.setOnClickListener {
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
            findNavController().navigate(R.id.action_homeFragment_to_conversationFragment)
        }

        textTranslate.setOnClickListener {
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
            findNavController().navigate(R.id.action_homeFragment_to_textTranslateFragment)
        }

        voiceDictionary.setOnClickListener {
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
            findNavController().navigate(R.id.action_homeFragment_to_dictionaryFragment)
        }

        keyboardThemes.setOnClickListener {
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
            findNavController().navigate(R.id.action_homeFragment_to_themesFragment)
        }

        textOnImages.setOnClickListener {
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
            findNavController().navigate(R.id.action_homeFragment_to_textOnImagesFragment)
        }

        keyboardSettings.setOnClickListener {
            val keyboardSetting = Intent(activity, MainActivity::class.java)
            MainActivity.isFirstTime = false
            startActivity(keyboardSetting)
            count++
            if (count == 2) {
                showAdd()
                count = 0
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        val container = view.findViewById<ShimmerFrameLayout>(R.id.shimmerEffectHome)
        nativeAd = NativeAd()
        nativeAd.refreshAd(requireContext(), nativeAdView, ad_frame, container)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showAdd() {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(requireActivity())
    }
}