package com.amherickeyboard.voicetyping.translator.activity

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.selectButtonPressed
import com.amherickeyboard.voicetyping.translator.Models.Image
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.OnDataPass
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.adapter.GalleryImageAdapter
import com.amherickeyboard.voicetyping.translator.fragment.GalleryFullscreenFragment
import com.amherickeyboard.voicetyping.translator.helper.GalleryImageClickListener
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.activity_text_on_images.*
import java.io.File

class TextOnImagesActivity() : AppCompatActivity(), GalleryImageClickListener, OnDataPass {

    private val buttonClick = AlphaAnimation(5f, 0.8f)
    private lateinit var backPhoto: ImageView
    lateinit var shareTitle: ImageView
    private lateinit var adFrameSmall: FrameLayout
    private lateinit var nativeAdSmall: NativeAd
    private lateinit var nativeAdViewSmall: NativeAdView
    private val SPANCOUNT = 2
    private var imageList = ArrayList<Image>()
    private lateinit var galleryAdapter: GalleryImageAdapter

    constructor(parcel: Parcel) : this() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_on_images)
        selectButtonPressed = false

        adFrameSmall = findViewById(R.id.ad_frame_SavedImages)
        nativeAdViewSmall = layoutInflater.inflate(R.layout.ad_unified_small, null) as NativeAdView
        nativeAdSmall = NativeAd()
        nativeAdSmall.refreshAdSmall(this, nativeAdViewSmall, ad_frame_SavedImages)

        backPhoto = findViewById(R.id.back_photo)

        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this

        // init recyclerview
        recyclerView.layoutManager = GridLayoutManager(this, SPANCOUNT)
        recyclerView.adapter = galleryAdapter

        backPhoto.setOnClickListener {
            it.startAnimation(buttonClick)
            onBackPressed()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        galleryAdapter.notifyDataSetChanged()
        val fileName = intent.getStringExtra("fileName")
        val filePath = getExternalFilesDir("Photo").toString()
        getAllFilesFrom(filePath)
    }

    fun getAllFilesFrom(path: String): ArrayList<Image> {
        imageList.clear()
        val dir = File(path)
        try {
            if (!dir.exists()) {
                dir.mkdir()
            }
            if (dir.isDirectory) {
                val directory = File(path)
                val files: Array<File>? = directory.listFiles()
                for (file in files!!) {
                    if (file.name.endsWith(".png") || file.name.endsWith(".jpg"))
                        imageList.add(
                            Image(path + "/" + file.name, file.name)
                        )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        galleryAdapter.notifyDataSetChanged()
        return imageList
    }


    override fun onClick(position: Int) {
        val bundle = Bundle()
        bundle.putParcelableArrayList("images", imageList)
        bundle.putInt("position", position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.arguments = bundle
        galleryFragment.show(fragmentTransaction, "Saved Images")
    }

    override fun onDataPass(newList: ArrayList<Image>) {
        val filePath = getExternalFilesDir("Photo").toString()
        getAllFilesFrom(filePath)
    }


    companion object CREATOR : Parcelable.Creator<TextOnImagesActivity> {
        override fun createFromParcel(parcel: Parcel): TextOnImagesActivity {
            return TextOnImagesActivity(parcel)
        }

        override fun newArray(size: Int): Array<TextOnImagesActivity?> {
            return arrayOfNulls(size)
        }
    }
}