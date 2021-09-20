package com.amherickeyboard.voicetyping.translator.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.amherickeyboard.voicetyping.translator.OnDataPass
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.adapter.GalleryImageAdapter
import com.amherickeyboard.voicetyping.translator.Models.Image
import com.amherickeyboard.voicetyping.translator.helper.ZoomOutPageTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.image_fullscreen.view.*
import java.io.File


class GalleryFullscreenFragment() : DialogFragment() {

    private var imageList = ArrayList<Image>()
    private var selectedPosition: Int = 0

    lateinit var viewPager: ViewPager

    lateinit var shareImage: ImageView
    lateinit var deleteImage:ImageView

    lateinit var galleryPagerAdapter: GalleryPagerAdapter
    private val buttonClick = AlphaAnimation(5f, 0.8f)
    lateinit var galleryAdapter: GalleryImageAdapter

    lateinit var dataPasser: OnDataPass

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_fullscreen, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        shareImage = view.findViewById(R.id.shareImage)
        deleteImage = view.findViewById(R.id.deleteImage)

        galleryPagerAdapter = GalleryPagerAdapter()

        imageList = arguments?.getParcelableArrayList<Image>("images") as ArrayList<Image>
        selectedPosition = arguments?.getInt("position") ?: -1

        viewPager.adapter = galleryPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())

        val imageURI = Uri.parse(imageList[selectedPosition].imageUrl)

        galleryAdapter = GalleryImageAdapter(imageList)


        shareImage.setOnClickListener {

            it.startAnimation(buttonClick)

            shareImage(imageURI.toString())
        }

        deleteImage.setOnClickListener {
            it.startAnimation(buttonClick)
            deleteImage(imageURI,selectedPosition)
        }


        setCurrentItem(selectedPosition)

        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        dataPasser = context as OnDataPass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    // viewpager page change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
            object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {



        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }

        override fun onPageScrollStateChanged(arg0: Int) {
        }
    }

    constructor(parcel: Parcel) : this() {
        selectedPosition = parcel.readInt()
    }


    private fun shareImage(path: String) {
        val shareIntent = Intent()
        val uri = FileProvider.getUriForFile(requireContext(), "${context?.packageName}.fileProvider", File(path))

        shareImage.isClickable = false
        Handler(Looper.myLooper()!!).postDelayed({
            shareImage.isClickable = true
        }, 1000)
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        shareIntent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        context?.startActivity(shareIntent)
    }

    private fun deleteImage(uri: Uri,position: Int)

    {
        val fdelete = File(uri.path!!)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                imageList.removeAt(position)
                Toast.makeText(activity,"Image Deleted Successfully",Toast.LENGTH_SHORT).show()
                passData(imageList)
                dismiss()



            } else {
                Toast.makeText(activity,"Failed to delete",Toast.LENGTH_SHORT).show()
            }
        }

    }

    // gallery adapter
    inner class GalleryPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val layoutInflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.image_fullscreen, container, false)

            val image = imageList.get(position)

            // load image
            Glide.with(context!!)
                    .load(image.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.ivFullscreenImage)

            container.addView(view)

            return view
        }

        override fun getCount(): Int {
            return imageList.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    fun passData(mList:ArrayList<Image>){
        dataPasser.onDataPass(mList)
    }


    companion object CREATOR : Parcelable.Creator<GalleryFullscreenFragment> {
        override fun createFromParcel(parcel: Parcel): GalleryFullscreenFragment {
            return GalleryFullscreenFragment(parcel)
        }

        override fun newArray(size: Int): Array<GalleryFullscreenFragment?> {
            return arrayOfNulls(size)
        }
    }


}