package com.amherickeyboard.voicetyping.translator.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amherickeyboard.voicetyping.translator.Models.ThemesModel
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.ThemesSharedPreference
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.amherickeyboard.voicetyping.translator.adapter.ThemesAdapter
import com.amherickeyboard.voicetyping.translator.ads.AdaptiveAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.util.*


class ThemesFragment : Fragment() {

    lateinit var themeRecyclerview: RecyclerView
    private lateinit var themesImages: ArrayList<ThemesModel>
    lateinit var adContainerView: FrameLayout
    lateinit var adView: AdView
    private var selected_theme_index = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_themes, container, false)

        themesImages = ArrayList()
        themesImages.add(0, ThemesModel("Grey Theme", "Grey background theme", R.drawable.grey_keyboard, 1))
        themesImages.add(1, ThemesModel("", "", R.drawable.empty_drawable, 2))
        themesImages.add(2, ThemesModel("Blue Gradient", "Blue gradient theme", R.drawable.blue_gradiante, 1))
        themesImages.add(3, ThemesModel("Black Theme", "Black background theme", R.drawable.black_theme, 1))
        themesImages.add(4, ThemesModel("Material Background", "Google materialistic yellow background theme", R.drawable.theme_four, 1))
        themesImages.add(5, ThemesModel("Aqua Theme", "Google materialistic aqua blue theme", R.drawable.theme_five, 1))
        themesImages.add(6, ThemesModel("Red Blue Gradient", "Google materialistic red blue theme", R.drawable.theme_six, 1))

        themeRecyclerview = view.findViewById(R.id.themeRecyclerView)
        themeRecyclerview.layoutManager = LinearLayoutManager(activity)
        val themesAdapter = activity?.let { ThemesAdapter(it, themesImages) }
        themeRecyclerview.setHasFixedSize(true)
        themeRecyclerview.adapter = themesAdapter

        themesAdapter?.setOnItemClickListener { position ->
            if (selected_theme_index == position) {
                return@setOnItemClickListener
            }
            selected_theme_index = position
            Toast.makeText(activity, "Theme set successfully", Toast.LENGTH_SHORT).show()
            context?.let { ThemesSharedPreference(it).themePosition = position }
        }

        isHomeFragment = false
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adaptiveAds = AdaptiveAds(activity)
        adContainerView = view.findViewById(R.id.adContainerView)
        adView = AdView(context)
        adView.setAdUnitId(getString(R.string.admob_banner_id))
        adContainerView.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.adSize = adaptiveAds.adSize
        adView.loadAd(adRequest)
        super.onViewCreated(view, savedInstanceState)
    }
}