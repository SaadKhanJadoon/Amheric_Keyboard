package com.amherickeyboard.voicetyping.translator.Fragments

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.TranslationKB
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.fragment_text_translate.*
import java.util.*

class TextTranslateFragment : Fragment(), AdapterView.OnItemSelectedListener,
    TranslationKB.TranslationComplete, TextToSpeech.OnInitListener {
    var languageInput = arrayOf(
        "Amharic", "Arabic", "Afrikaans", "Albanian", "Azerbaijani",
        "Basque", "Bengali", "Belarusian", "Bulgarian",
        "Catalan", "Chinese", "Croatian", "Czech",
        "Danish", "Dutch",
        "English", "Esperanto", "Estonian",
        "French", "Finnish",
        "Galician", "German", "Greek", "Georgian", "Gujarati",
        "Hebrew", "Hindi", "Hungarian",
        "Icelandic", "Indonesian", "Italian", "Irish", "Japanese",
        "Kannada", "Korean",
        "Latin", "Latvian", "Lithuanian",
        "Macedonian", "Malay", "Maltese", "Norwegian",
        "Persian", "Polish", "Portuguese", "Romanian", "Russian",
        "Serbian", "Filipino", "Slovak", "Slovenian", "Spanish",
        "Swahili", "Swedish", "Tamil", "Telugu", "Thai",
        "Turkish", "Urdu", "Ukrainian", "Vietnamese",
        "Welsh", "Yiddish"
    )
    var languageOutput = arrayOf(
        "Amharic", "Arabic", "Afrikaans", "Albanian", "Azerbaijani",
        "Basque", "Bengali", "Belarusian", "Bulgarian",
        "Catalan", "Chinese", "Croatian", "Czech",
        "Danish", "Dutch",
        "English", "Esperanto", "Estonian",
        "French", "Finnish",
        "Galician", "German", "Greek", "Georgian", "Gujarati",
        "Hebrew", "Hindi", "Hungarian",
        "Icelandic", "Indonesian", "Italian", "Irish", "Japanese",
        "Kannada", "Korean",
        "Latin", "Latvian", "Lithuanian",
        "Macedonian", "Malay", "Maltese", "Norwegian",
        "Persian", "Polish", "Portuguese", "Romanian", "Russian",
        "Serbian", "Filipino", "Slovak", "Slovenian", "Spanish",
        "Swahili", "Swedish", "Tamil", "Telugu", "Thai",
        "Turkish", "Urdu", "Ukrainian", "Vietnamese",
        "Welsh", "Yiddish"
    )
    lateinit var spinnerForInputLanguageSelection: Spinner
    lateinit var spinnerForOutputLanguageSelection: Spinner
    lateinit var viewTs: View
    lateinit var inputLanguageCode: String
    lateinit var outputLanguageCode: String
    lateinit var aa: ArrayAdapter<*>
    lateinit var bb: ArrayAdapter<*>
    lateinit var switcherButton: ImageView
    val buttonClick = AlphaAnimation(5f, 0.8f)
    lateinit var inputLanguagePosition: String
    lateinit var outputLanguagePosition: String
    lateinit var editTextTrans: EditText
    lateinit var resultText: EditText
    var translate: ImageView? = null
    lateinit var menuButton: ImageView
    private lateinit var ad_frame: FrameLayout
    private lateinit var nativeAd: NativeAd
    private lateinit var containerShimmer: ShimmerFrameLayout
    private lateinit var nativeAdView: NativeAdView
    private lateinit var ad_frame_small: FrameLayout
    private lateinit var nativeAdSmall: NativeAd
    private lateinit var nativeAdViewSmall: NativeAdView
    private var count = 0
    private var textToSpeech: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewTs = inflater.inflate(R.layout.fragment_text_translate, container, false)
        textToSpeech = TextToSpeech(activity, this)

        isHomeFragment = false
        editTextTrans = viewTs.findViewById(R.id.editTextTranslate)
        resultText = viewTs.findViewById(R.id.resultTextView)
        translate = viewTs.findViewById(R.id.translate_btn)
        menuButton = viewTs.findViewById(R.id.menuItem)
        ad_frame = viewTs.findViewById(R.id.ad_frame_TextTranslate)
        ad_frame_small = viewTs.findViewById(R.id.ad_frame_TextTranslate_small)
        switcherButton = viewTs.findViewById(R.id.switcher)
        spinnerForInputLanguageSelection = viewTs.findViewById(R.id.spinner)
        aa = ArrayAdapter<Any?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            languageInput
        )
        aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerForInputLanguageSelection.adapter = aa
        spinnerForInputLanguageSelection.onItemSelectedListener = this

        spinnerForOutputLanguageSelection = viewTs.findViewById(R.id.spinner2)
        bb = ArrayAdapter<Any?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            languageOutput
        )
        bb.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerForOutputLanguageSelection.adapter = bb
        spinnerForOutputLanguageSelection.setSelection(15)
        spinnerForOutputLanguageSelection.onItemSelectedListener = this

        translate?.setOnClickListener {
            it.startAnimation(buttonClick)
            val translationKB = TranslationKB(requireActivity())
            translationKB.setTranslationComplete(this)

            if (editTextTrans.text.isNotEmpty()) {
                hideKeyboard()
                translationKB.runTranslation(
                    editTextTrans.text.toString(),
                    outputLanguageCode,
                    inputLanguageCode
                )
            } else {
                Toast.makeText(activity, "Please Enter Text", Toast.LENGTH_SHORT).show()
            }
        }

        switcherButton.setOnClickListener {
            it.startAnimation(buttonClick)
            textToSpeech?.stop()
            val inputSelection = spinnerForInputLanguageSelection.getSelectedItemPosition()
            spinnerForInputLanguageSelection.setSelection(spinnerForOutputLanguageSelection.getSelectedItemPosition())
            spinnerForOutputLanguageSelection.setSelection(inputSelection)
            val temp = inputLanguageCode
            inputLanguageCode = outputLanguageCode
            outputLanguageCode = temp
        }

        menuButton.setOnClickListener {
            val popup = PopupMenu(activity, menuButton)
            popup.inflate(R.menu.adapter_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.speak -> {
                            it.startAnimation(buttonClick)
                            val text = resultText.text.toString()
                            speaker(text, outputLanguageCode)
                            return true
                        }
                        R.id.copy -> {
                            it.startAnimation(buttonClick)
                            val clipboard =
                                ContextCompat.getSystemService(
                                    requireActivity(),
                                    ClipboardManager::class.java
                                )
                            val clip = ClipData.newPlainText("label", resultText.text.toString())
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(activity, "Copied", Toast.LENGTH_SHORT).show()
                            }
                            return true
                        }
                        R.id.delete -> {
                            it.startAnimation(buttonClick)
                            editTextTranslate.setText("")
                            resultText.setText("")
                            ResultLayoutTextTranslate.visibility = View.GONE
                            textToSpeech?.stop()
                            ad_frame_small.visibility = View.GONE
                            ad_frame.visibility = View.VISIBLE
                            return true
                        }
                        R.id.share -> {
                            it.startAnimation(buttonClick)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Amheric Keyboard")
                            shareIntent.putExtra(Intent.EXTRA_TEXT, resultText.text.toString())
                            startActivity(Intent.createChooser(shareIntent, "Share via"))
                            return true
                        }
                        else -> false
                    }
                }
            })
            popup.show()
        }
        return viewTs
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        containerShimmer = view.findViewById(R.id.shimmerEffectTextTranslate)
        nativeAd = NativeAd()
        nativeAd.refreshAd(requireContext(), nativeAdView, ad_frame_TextTranslate, containerShimmer)
        nativeAdViewSmall = layoutInflater.inflate(R.layout.ad_unified_small, null) as NativeAdView
        nativeAdSmall = NativeAd()
        nativeAdSmall.refreshAdSmall(
            requireContext(),
            nativeAdViewSmall,
            ad_frame_TextTranslate_small
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spin = parent as Spinner
        (parent.getChildAt(0) as TextView).textSize = 18f

        if (spin.id == R.id.spinner) {
            inputLanguagePosition = languageInput[position]
            inputLanguageCode = when (inputLanguagePosition) {

                "Amharic" -> "am"
                "Afrikaans" -> "af"
                "Irish" -> "ga"
                "Albanian" -> "sq"
                "Italian" -> "it"
                "Arabic" -> "ar"
                "Japanese" -> "ja"
                "Azerbaijani" -> "az"
                "Kannada" -> "kn"
                "Basque" -> "eu"
                "Korean" -> "ko"
                "Bengali" -> "bn"
                "Latin" -> "la"
                "Belarusian" -> "be"
                "Latvian" -> "lv"
                "Bulgarian" -> "bg"
                "Lithuanian" -> "lt"
                "Catalan" -> "ca"
                "Macedonian" -> "mk"
                "Malay" -> "ms"
                "Maltese" -> "mt"
                "Croatian" -> "hr"
                "Norwegian" -> "no"
                "Czech" -> "cs"
                "Persian" -> "fa"
                "Danish" -> "da"
                "Polish" -> "pl"
                "Dutch" -> "nl"
                "Portuguese" -> "pt"
                "Romanian" -> "ro"
                "Esperanto" -> "eo"
                "Estonian" -> "et"
                "Serbian" -> "sr"
                "Filipino" -> "tl"
                "Slovak" -> "sk"
                "Finnish" -> "fi"
                "Slovenian" -> "sl"
                "French" -> "fr"
                "Spanish" -> "es"
                "Galician" -> "gl"
                "Swahili" -> "sw"
                "Georgian" -> "ka"
                "Swedish" -> "sv"
                "German" -> "de"
                "Tamil" -> "ta"
                "Greek" -> "el"
                "Telugu" -> "te"
                "Gujarati" -> "gu"
                "Thai" -> "th"
                "Turkish" -> "tr"
                "Hebrew" -> "iw"
                "Ukrainian" -> "uk"
                "Hindi" -> "hi"
                "Hungarian" -> "hu"
                "Vietnamese" -> "vi"
                "Icelandic" -> "is"
                "Welsh" -> "cy"
                "Indonesian" -> "id"
                "Yiddish" -> "yi"
                "English" -> "en-US"
                "Urdu" -> "ur-PK"
                "Chinese" -> "zh"
                "Russian" -> "ru"

                else -> "en-US"
            }

        }
        if (spin.id == R.id.spinner2) {
            outputLanguagePosition = languageOutput[position]
            outputLanguageCode = when (outputLanguagePosition) {
                "Amharic" -> "am"
                "Afrikaans" -> "af"
                "Irish" -> "ga"
                "Albanian" -> "sq"
                "Italian" -> "it"
                "Arabic" -> "ar"
                "Japanese" -> "ja"
                "Azerbaijani" -> "az"
                "Kannada" -> "kn"
                "Basque" -> "eu"
                "Korean" -> "ko"
                "Bengali" -> "bn"
                "Latin" -> "la"
                "Belarusian" -> "be"
                "Latvian" -> "lv"
                "Bulgarian" -> "bg"
                "Lithuanian" -> "lt"
                "Catalan" -> "ca"
                "Macedonian" -> "mk"
                "Malay" -> "ms"
                "Maltese" -> "mt"
                "Croatian" -> "hr"
                "Norwegian" -> "no"
                "Czech" -> "cs"
                "Persian" -> "fa"
                "Danish" -> "da"
                "Polish" -> "pl"
                "Dutch" -> "nl"
                "Portuguese" -> "pt"
                "Romanian" -> "ro"
                "Esperanto" -> "eo"
                "Estonian" -> "et"
                "Serbian" -> "sr"
                "Filipino" -> "tl"
                "Slovak" -> "sk"
                "Finnish" -> "fi"
                "Slovenian" -> "sl"
                "French" -> "fr"
                "Spanish" -> "es"
                "Galician" -> "gl"
                "Swahili" -> "sw"
                "Georgian" -> "ka"
                "Swedish" -> "sv"
                "German" -> "de"
                "Tamil" -> "ta"
                "Greek" -> "el"
                "Telugu" -> "te"
                "Gujarati" -> "gu"
                "Thai" -> "th"
                "Turkish" -> "tr"
                "Hebrew" -> "iw"
                "Ukrainian" -> "uk"
                "Hindi" -> "hi"
                "Hungarian" -> "hu"
                "Vietnamese" -> "vi"
                "Icelandic" -> "is"
                "Welsh" -> "cy"
                "Indonesian" -> "id"
                "Yiddish" -> "yi"
                "English" -> "en-US"
                "Urdu" -> "ur"
                "Chinese" -> "zh"
                "Russian" -> "ru"

                else -> "en-US"
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    fun speaker(text: String, code: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech!!.language = Locale.forLanguageTag(code)
            textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun translationCompleted(translation: String, language: String) {
        if (translation === "0") {
            Toast.makeText(activity, "internet connection error", Toast.LENGTH_SHORT).show()
        } else {
            count++
            if (count == 1) {
                showAdd()
                count = -1
            }
            ad_frame.visibility = View.GONE
            shimmerEffectTextTranslate.visibility = View.GONE
            ResultLayoutTextTranslate.visibility = View.VISIBLE
            ad_frame_small.visibility = View.VISIBLE
            resultText.setText(translation)
        }
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(viewTs.applicationWindowToken, 0)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = outputLanguageCode
            val result: Int = textToSpeech!!.setLanguage(Locale(lang))
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.e("**error", "This Language is not supported")
            }
        } else Log.e("**error", "Initilization Failed!")
    }

    private fun showAdd() {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(requireActivity())
    }

    override fun onPause() {
        textToSpeech?.stop()
        super.onPause()
    }
}