package com.amherickeyboard.voicetyping.translator.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.amherickeyboard.voicetyping.translator.AppOpenAds.OpenApp.Companion.selectButtonPressed
import com.amherickeyboard.voicetyping.translator.EditImageActivity
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.amherickeyboard.voicetyping.translator.activity.TextOnImagesActivity
import com.amherickeyboard.voicetyping.translator.copyUriToExternalFilesDir
import com.amherickeyboard.voicetyping.translator.getFileNameByUri
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import ja.burhanrashid52.photoeditor.PhotoEditorView
import kotlinx.android.synthetic.main.fragment_text_on_images.*

class TextOnImagesFragment : Fragment() {

    private lateinit var selectAlbum: LinearLayout
    private lateinit var savedPhotos: LinearLayout
    var mPhotoEditorView: PhotoEditorView? = null
    val buttonClick = AlphaAnimation(5f, 0.8f)
    private lateinit var ad_frame: FrameLayout
    private lateinit var nativeAd: NativeAd
    private lateinit var containerShimmer: ShimmerFrameLayout
    private lateinit var nativeAdView: NativeAdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_text_on_images, container, false)
        selectAlbum = view.findViewById(R.id.select_album)
        savedPhotos = view.findViewById(R.id.imageView2)
        ad_frame = view.findViewById(R.id.ad_frame_Images)
        isHomeFragment = false
        mPhotoEditorView = view.findViewById(R.id.photoEditorView)
        selectAlbum.setOnClickListener {
            it.startAnimation(buttonClick)
            selectButtonPressed = true
            selectAlbum.isClickable = false
            Handler(Looper.myLooper()!!).postDelayed({
                selectAlbum.isClickable = true
            }, 1000)

            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(), this)

        }
        savedPhotos.setOnClickListener {
            it.startAnimation(buttonClick)
            val intent = Intent(context, TextOnImagesActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        containerShimmer = view.findViewById(R.id.shimmerEffectImages)
        nativeAd = NativeAd()
        nativeAd.refreshAd(requireContext(), nativeAdView, ad_frame_Images, containerShimmer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri? = result?.uriContent
                result?.getUriFilePath(requireContext())
                val fileName = requireActivity().getFileNameByUri(resultUri!!)
                requireActivity().copyUriToExternalFilesDir(resultUri, fileName)
                startActivity(
                    Intent(context, EditImageActivity::class.java).putExtra(
                        "filePath",
                        requireActivity().externalCacheDir.toString() + "/$fileName"
                    )
                )
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
                Toast.makeText(activity, "Cropping failed: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}