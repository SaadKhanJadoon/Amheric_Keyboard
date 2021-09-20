package com.amherickeyboard.voicetyping.translator

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.amherickeyboard.voicetyping.translator.EmojiBSFragment.EmojiListener
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated.Companion.getInstance
import com.amherickeyboard.voicetyping.translator.StickerBSFragment.StickerListener
import com.amherickeyboard.voicetyping.translator.activity.TextOnImagesActivity
import com.amherickeyboard.voicetyping.translator.base.BaseActivity
import com.amherickeyboard.voicetyping.translator.filters.FilterListener
import com.amherickeyboard.voicetyping.translator.filters.FilterViewAdapter
import com.amherickeyboard.voicetyping.translator.tools.EditingToolsAdapter
import com.amherickeyboard.voicetyping.translator.tools.EditingToolsAdapter.OnItemSelected
import com.amherickeyboard.voicetyping.translator.tools.ToolType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class EditImageActivity : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties, EmojiListener, StickerListener, OnItemSelected,
    FilterListener {
    var uri: Uri? = null
    var mPhotoEditor: PhotoEditor? = null
    var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private var mRvFilters: RecyclerView? = null
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false
    private val buttonClick = AlphaAnimation(5f, 0.8f)
    private var croppedFilePath: String? = null
    private var mSaveFileHelper: FileSaveHelper? = null
    private var imgSave: Button? = null
    private var imgClose: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        croppedFilePath = intent.getStringExtra("filePath")
        Log.d("#FILEpath", croppedFilePath!!)
        handleIntentImage(mPhotoEditorView!!.source)
        mWonderFont = Typeface.createFromAsset(assets, "beyond_wonderland.ttf")
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter
        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.layoutManager = llmFilters
        mRvFilters!!.adapter = mFilterViewAdapter

        val pinchTextScalable = intent.getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true)
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable)
            .build() // build photo editor sdk
        mPhotoEditor?.setOnPhotoEditorListener(this)
        mPhotoEditorView!!.source.setImageURI(Uri.parse(croppedFilePath))
        mSaveFileHelper = FileSaveHelper(this)
    }

    private fun handleIntentImage(source: ImageView) {
        val intent = intent
        if (intent != null) {
            if (Intent.ACTION_EDIT == intent.action || ACTION_NEXTGEN_EDIT == intent.action) {
                try {
                    val uri = intent.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val intentType = intent.type
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri = intent.data
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)
        val imgUndo: ImageView = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)
        val imgRedo: ImageView = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)
        imgSave = findViewById(R.id.imgSave)
        imgSave?.setOnClickListener(this)
        imgClose = findViewById(R.id.imgClose)
        imgClose?.setOnClickListener(this)
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText: String?, newColorCode: Int ->
            val styleBuilder = TextStyleBuilder()
            styleBuilder.withTextColor(newColorCode)
            mPhotoEditor!!.editText(rootView, inputText, styleBuilder)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgUndo -> mPhotoEditor!!.undo()
            R.id.imgRedo -> mPhotoEditor!!.redo()
            R.id.imgSave -> saveImageFile()
            R.id.imgClose -> onBackPressed()
        }
    }

    private fun saveImageFile() {
        imgSave!!.startAnimation(buttonClick)
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            showLoading("Saving...")
            val fileName = System.currentTimeMillis().toString() + ".png"
            val filePath = getExternalFilesDir("Photo").toString() + "/" + fileName

            GlobalScope.launch(Dispatchers.Main) {
                mPhotoEditor?.saveAsFile(filePath, object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        Log.e("PhotoEditor", "Image Saved Successfully")
                        showSnackbar("Image Saved Successfully")
                        val intent = Intent(this@EditImageActivity, TextOnImagesActivity::class.java)
                        intent.putExtra("fileName", fileName)
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            startActivity(intent)
                            finish()
                            hideLoading()
                            showAdd()
                        }, 1000)
                    }

                    override fun onFailure(exception: java.lang.Exception) {
                        Log.e("PhotoEditor", "Failed to save Image")
                        hideLoading()
                        showSnackbar("Failed to save Image")
                    }
                })
            }

        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor!!.addImage(bitmap)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String) {
        if (isGranted) {
            saveImageFile()
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.msg_save_image))
        builder.setPositiveButton("Save") { dialog: DialogInterface?, which: Int -> saveImageFile() }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setNeutralButton("Discard") { dialog: DialogInterface?, which: Int -> finish() }
        builder.create().show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.BRUSH -> {
                mPhotoEditor!!.setBrushDrawingMode(true)
                showBottomSheetDialogFragment(mPropertiesBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment = TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener { inputText: String?, colorCode: Int ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    mPhotoEditor!!.addText(inputText, styleBuilder)
                }
            }
            ToolType.ERASER -> {
                mPhotoEditor!!.brushEraser()
            }
            ToolType.FILTER -> {
                showFilter(true)
            }
            ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSFragment)
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.END)
        }
        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onBackPressed() {
        imgClose!!.startAnimation(buttonClick)
        if (mIsFilterVisible) {
            showFilter(false)
        } else if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showAdd() {
        getInstance().showInterstitialAdNew(this)
    }

    companion object {
        private val TAG = EditImageActivity::class.java.simpleName
        const val ACTION_NEXTGEN_EDIT = "action_nextgen_edit"
        const val PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE"
    }
}