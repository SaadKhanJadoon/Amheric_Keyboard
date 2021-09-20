package com.amherickeyboard.voicetyping.translator.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.TranslationKB
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.amherickeyboard.voicetyping.translator.adapter.RecyclerAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.fragment_conversation.*
import java.util.*


class ConversationFragment : Fragment(), TranslationKB.TranslationComplete,
    AdapterView.OnItemSelectedListener, RecyclerAdapter.onItemClick, TextToSpeech.OnInitListener {

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

    lateinit var viewCs: View
    lateinit var micButtonInput: ImageView
    lateinit var micButtonOutput: ImageView
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private val REQUEST_CODE_SPEECH_OUTPUT = 2
    private lateinit var spinnerForInputLanguageSelection: Spinner
    private lateinit var spinnerForOutputLanguageSelection: Spinner
    private lateinit var inputLanguageCode: String
    private lateinit var outputLanguageCode: String
    private var inputFlag: Int = 0
    private var outputFlag: Int = 0
    private var inputCountry: String = ""
    private var outputCountry: String = ""
    private lateinit var inputCountryList: ArrayList<String>
    private lateinit var outputCountryList: ArrayList<String>
    private lateinit var aa: ArrayAdapter<*>
    private lateinit var bb: ArrayAdapter<*>
    private lateinit var switcherButton: ImageView
    private val buttonClick = AlphaAnimation(5f, 0.8f)
    private lateinit var inputLanguagePosition: String
    private lateinit var outputLanguagePosition: String
    lateinit var rv: RecyclerView
    private lateinit var inputText: ArrayList<String>
    private lateinit var outputText: ArrayList<String>
    private lateinit var inputImages: ArrayList<Int>
    private lateinit var outputImages: ArrayList<Int>
    private var textToSpeech: TextToSpeech? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null
    private lateinit var ad_frame: FrameLayout
    private lateinit var nativeAd: NativeAd
    private lateinit var containerShimmer: ShimmerFrameLayout
    private lateinit var nativeAdView: NativeAdView
    private lateinit var nativeAdSmall: NativeAd
    private lateinit var nativeAdViewSmall: NativeAdView
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewCs = inflater.inflate(R.layout.fragment_conversation, container, false)
        textToSpeech = TextToSpeech(activity, this)

        micButtonInput = viewCs.findViewById(R.id.mic_conversation_1)
        micButtonOutput = viewCs.findViewById(R.id.mic_conversation_2)

        ad_frame = viewCs.findViewById(R.id.ad_frame_conversation)

        isHomeFragment = false
        rv = viewCs.findViewById(R.id.recycler_view)
        inputText = arrayListOf()
        outputText = arrayListOf()
        inputCountryList = arrayListOf()
        outputCountryList = arrayListOf()
        inputImages = arrayListOf()
        outputImages = arrayListOf()
        switcherButton = viewCs.findViewById(R.id.switcherConversation)
        switcherButton.setOnClickListener {
            it.startAnimation(buttonClick)
            textToSpeech?.stop()
            val inputSelection = spinnerForInputLanguageSelection.selectedItemPosition
            spinnerForInputLanguageSelection.setSelection(spinnerForOutputLanguageSelection.selectedItemPosition)
            spinnerForOutputLanguageSelection.setSelection(inputSelection)
            val temp = inputLanguageCode
            inputLanguageCode = outputLanguageCode
            outputLanguageCode = temp
        }

        spinnerForInputLanguageSelection = viewCs.findViewById(R.id.spinnerConversation)
        aa = ArrayAdapter<Any?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            languageInput
        )
        aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerForInputLanguageSelection.adapter = aa
        spinnerForInputLanguageSelection.onItemSelectedListener = this
        spinnerForOutputLanguageSelection = viewCs.findViewById(R.id.spinnerConversation2)
        bb = ArrayAdapter<Any?>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            languageOutput
        )
        bb.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerForOutputLanguageSelection.adapter = bb
        spinnerForOutputLanguageSelection.setSelection(15)
        spinnerForOutputLanguageSelection.onItemSelectedListener = this
        micButtonInput.setOnClickListener {
            it.startAnimation(buttonClick)
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, inputLanguageCode)
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")

            try {
                startActivityForResult(speechRecognizerIntent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        micButtonOutput.setOnClickListener {
            it.startAnimation(buttonClick)
            val speechRecognizerIntentOut = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntentOut.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechRecognizerIntentOut.putExtra(RecognizerIntent.EXTRA_LANGUAGE, outputLanguageCode)
            speechRecognizerIntentOut.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak")

            try {
                startActivityForResult(speechRecognizerIntentOut, REQUEST_CODE_SPEECH_OUTPUT)
            } catch (e: Exception) {
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        layoutManager = LinearLayoutManager(activity)
        rv.layoutManager = layoutManager
        return viewCs
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        containerShimmer = view.findViewById(R.id.shimmerEffectConversation)
        nativeAd = NativeAd()
        nativeAd.refreshAd(requireContext(), nativeAdView, ad_frame_conversation, containerShimmer)
        nativeAdViewSmall = layoutInflater.inflate(R.layout.ad_unified_small, null) as NativeAdView
        nativeAdSmall = NativeAd()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    recycler_view.visibility = View.VISIBLE
                    adLayoutConversation.visibility = View.GONE
                    shimmerEffectConversation.visibility = View.GONE
                    inputText.add(0, result!![0])
                    inputCountryList.add(0, inputCountry)
                    outputCountryList.add(0, outputCountry)
                    inputImages.add(0, inputFlag)
                    outputImages.add(0, outputFlag)
                    val translationKB = TranslationKB(requireActivity())
                    translationKB.setTranslationComplete(this)
                    translationKB.runTranslation(result[0], outputLanguageCode, inputLanguageCode)
                    count++
                    if (count == 1) {
                        showAdd()
                        count = -1
                    }
                }
            }
            REQUEST_CODE_SPEECH_OUTPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    recycler_view.visibility = View.VISIBLE
                    adLayoutConversation.visibility = View.GONE
                    shimmerEffectConversation.visibility = View.GONE
                    inputText.add(0, result!![0])
                    inputCountryList.add(0, inputCountry)
                    outputCountryList.add(0, outputCountry)
                    inputImages.add(0, outputFlag)
                    outputImages.add(0, inputFlag)
                    Log.d("READ", outputLanguageCode)
                    val translationKB = TranslationKB(requireActivity())
                    translationKB.setTranslationComplete(this)
                    translationKB.runTranslation(result[0], inputLanguageCode, outputLanguageCode)
                    count++
                    if (count == 1) {
                        showAdd()
                        count = -1
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spin = parent as Spinner
        (parent.getChildAt(0) as TextView).textSize = 18f

        if (spin.id == R.id.spinnerConversation) {
            inputLanguagePosition = languageInput[position]
            inputCountry = when (inputLanguagePosition) {
                "Amharic" -> "Amharic"
                "Afrikaans" -> "Afrikaans"
                "Irish" -> "Irish"
                "Albanian" -> "Albanian"
                "Italian" -> "Italian"
                "Arabic" -> "Arabic"
                "Japanese" -> "Japanese"
                "Azerbaijani" -> "Azerbaijani"
                "Kannada" -> "Kannada"
                "Basque" -> "Basque"
                "Korean" -> "Korean"
                "Bengali" -> "Bengali"
                "Latin" -> "Latin"
                "Belarusian" -> "Belarusian"
                "Latvian" -> "Latvian"
                "Bulgarian" -> "Bulgarian"
                "Lithuanian" -> "Lithuanian"
                "Catalan" -> "Catalan"
                "Macedonian" -> "Macedonian"
                "Malay" -> "Malay"
                "Maltese" -> "Maltese"
                "Croatian" -> "Croatian"
                "Norwegian" -> "Norwegian"
                "Czech" -> "Czech"
                "Persian" -> "Persian"
                "Danish" -> "Danish"
                "Polish" -> "Polish"
                "Dutch" -> "Dutch"
                "Portuguese" -> "Portuguese"
                "Romanian" -> "Romanian"
                "Esperanto" -> "Esperanto"
                "Estonian" -> "Estonian"
                "Serbian" -> "Serbian"
                "Filipino" -> "Filipino"
                "Slovak" -> "Slovak"
                "Finnish" -> "Finnish"
                "Slovenian" -> "Slovenian"
                "French" -> "French"
                "Spanish" -> "Spanish"
                "Galician" -> "Galician"
                "Swahili" -> "Swahili"
                "Georgian" -> "Georgian"
                "Swedish" -> "Swedish"
                "German" -> "German"
                "Tamil" -> "Tamil"
                "Greek" -> "Greek"
                "Telugu" -> "Telugu"
                "Gujarati" -> "Gujarati"
                "Thai" -> "Thai"
                "Turkish" -> "Turkish"
                "Hebrew" -> "Hebrew"
                "Ukrainian" -> "Ukrainian"
                "Hindi" -> "Hindi"
                "Hungarian" -> "Hungarian"
                "Vietnamese" -> "Vietnamese"
                "Icelandic" -> "Icelandic"
                "Welsh" -> "Welsh"
                "Indonesian" -> "Indonesian"
                "Yiddish" -> "Yiddish"
                "English" -> "English"
                "Urdu" -> "Urdu"
                "Chinese" -> "Chinese"
                "Russian" -> "Russian"
                else -> "English"
            }
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
            inputFlag = when (inputLanguagePosition) {
                "Amharic" -> R.drawable.amharic
                "Afrikaans" -> R.drawable.afrikaans
                "Irish" -> R.drawable.irish
                "Albanian" -> R.drawable.albanian
                "Italian" -> R.drawable.italian
                "Arabic" -> R.drawable.arabic
                "Japanese" -> R.drawable.japan
                "Azerbaijani" -> R.drawable.azerbaycan
                "Kannada" -> R.drawable.kannada
                "Basque" -> R.drawable.basque
                "Korean" -> R.drawable.korean
                "Bengali" -> R.drawable.bengali
                "Latin" -> R.drawable.greek
                "Belarusian" -> R.drawable.belarusian
                "Latvian" -> R.drawable.latvian
                "Bulgarian" -> R.drawable.bulgarian
                "Lithuanian" -> R.drawable.lithuanian
                "Catalan" -> R.drawable.catalan
                "Macedonian" -> R.drawable.macedonian
                "Malay" -> R.drawable.malay
                "Maltese" -> R.drawable.maltese
                "Croatian" -> R.drawable.croatian
                "Norwegian" -> R.drawable.norwegian
                "Czech" -> R.drawable.czech
                "Persian" -> R.drawable.persian
                "Danish" -> R.drawable.danish
                "Polish" -> R.drawable.polish
                "Dutch" -> R.drawable.dutch
                "Portuguese" -> R.drawable.portuguese
                "Romanian" -> R.drawable.romanian
                "Esperanto" -> R.drawable.esperanto
                "Estonian" -> R.drawable.estonian
                "Serbian" -> R.drawable.serbian
                "Filipino" -> R.drawable.filipino
                "Slovak" -> R.drawable.slovak
                "Finnish" -> R.drawable.finnish
                "Slovenian" -> R.drawable.slovenian
                "French" -> R.drawable.french
                "Spanish" -> R.drawable.spanish
                "Galician" -> R.drawable.galician
                "Swahili" -> R.drawable.swahili
                "Georgian" -> R.drawable.georgian
                "Swedish" -> R.drawable.swedish
                "German" -> R.drawable.german
                "Tamil" -> R.drawable.tamil
                "Greek" -> R.drawable.greek
                "Telugu" -> R.drawable.telugu
                "Gujarati" -> R.drawable.gujarati
                "Thai" -> R.drawable.thai
                "Turkish" -> R.drawable.turkish
                "Hebrew" -> R.drawable.hebrew
                "Ukrainian" -> R.drawable.ukrainian
                "Hindi" -> R.drawable.hindi
                "Hungarian" -> R.drawable.hungarian
                "Vietnamese" -> R.drawable.vietnamese
                "Icelandic" -> R.drawable.icelandic
                "Welsh" -> R.drawable.welsh
                "Indonesian" -> R.drawable.indonesia
                "Yiddish" -> R.drawable.german
                "English" -> R.drawable.english
                "Urdu" -> R.drawable.urdu
                "Chinese" -> R.drawable.chinese
                "Russian" -> R.drawable.russian

                else -> R.drawable.amharic
            }
        }
        if (spin.id == R.id.spinnerConversation2) {
            outputLanguagePosition = languageOutput[position]
            outputCountry = when (outputLanguagePosition) {
                "Amharic" -> "Amharic"
                "Afrikaans" -> "Afrikaans"
                "Irish" -> "Irish"
                "Albanian" -> "Albanian"
                "Italian" -> "Italian"
                "Arabic" -> "Arabic"
                "Japanese" -> "Japanese"
                "Azerbaijani" -> "Azerbaijani"
                "Kannada" -> "Kannada"
                "Basque" -> "Basque"
                "Korean" -> "Korean"
                "Bengali" -> "Bengali"
                "Latin" -> "Latin"
                "Belarusian" -> "Belarusian"
                "Latvian" -> "Latvian"
                "Bulgarian" -> "Bulgarian"
                "Lithuanian" -> "Lithuanian"
                "Catalan" -> "Catalan"
                "Macedonian" -> "Macedonian"
                "Malay" -> "Malay"
                "Maltese" -> "Maltese"
                "Croatian" -> "Croatian"
                "Norwegian" -> "Norwegian"
                "Czech" -> "Czech"
                "Persian" -> "Persian"
                "Danish" -> "Danish"
                "Polish" -> "Polish"
                "Dutch" -> "Dutch"
                "Portuguese" -> "Portuguese"
                "Romanian" -> "Romanian"
                "Esperanto" -> "Esperanto"
                "Estonian" -> "Estonian"
                "Serbian" -> "Serbian"
                "Filipino" -> "Filipino"
                "Slovak" -> "Slovak"
                "Finnish" -> "Finnish"
                "Slovenian" -> "Slovenian"
                "French" -> "French"
                "Spanish" -> "Spanish"
                "Galician" -> "Galician"
                "Swahili" -> "Swahili"
                "Georgian" -> "Georgian"
                "Swedish" -> "Swedish"
                "German" -> "German"
                "Tamil" -> "Tamil"
                "Greek" -> "Greek"
                "Telugu" -> "Telugu"
                "Gujarati" -> "Gujarati"
                "Thai" -> "Thai"
                "Turkish" -> "Turkish"
                "Hebrew" -> "Hebrew"
                "Ukrainian" -> "Ukrainian"
                "Hindi" -> "Hindi"
                "Hungarian" -> "Hungarian"
                "Vietnamese" -> "Vietnamese"
                "Icelandic" -> "Icelandic"
                "Welsh" -> "Welsh"
                "Indonesian" -> "Indonesian"
                "Yiddish" -> "Yiddish"
                "English" -> "English"
                "Urdu" -> "Urdu"
                "Chinese" -> "Chinese"
                "Russian" -> "Russian"
                else -> "English"
            }
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
                "Urdu" -> "ur-PK"
                "Chinese" -> "zh"
                "Russian" -> "ru"

                else -> "en-US"
            }
            outputFlag = when (outputLanguagePosition) {
                "Amharic" -> R.drawable.amharic
                "Afrikaans" -> R.drawable.afrikaans
                "Irish" -> R.drawable.irish
                "Albanian" -> R.drawable.albanian
                "Italian" -> R.drawable.italian
                "Arabic" -> R.drawable.arabic
                "Japanese" -> R.drawable.japan
                "Azerbaijani" -> R.drawable.azerbaycan
                "Kannada" -> R.drawable.kannada
                "Basque" -> R.drawable.basque
                "Korean" -> R.drawable.korean
                "Bengali" -> R.drawable.bengali
                "Latin" -> R.drawable.greek
                "Belarusian" -> R.drawable.belarusian
                "Latvian" -> R.drawable.latvian
                "Bulgarian" -> R.drawable.bulgarian
                "Lithuanian" -> R.drawable.lithuanian
                "Catalan" -> R.drawable.catalan
                "Macedonian" -> R.drawable.macedonian
                "Malay" -> R.drawable.malay
                "Maltese" -> R.drawable.maltese
                "Croatian" -> R.drawable.croatian
                "Norwegian" -> R.drawable.norwegian
                "Czech" -> R.drawable.czech
                "Persian" -> R.drawable.persian
                "Danish" -> R.drawable.danish
                "Polish" -> R.drawable.polish
                "Dutch" -> R.drawable.dutch
                "Portuguese" -> R.drawable.portuguese
                "Romanian" -> R.drawable.romanian
                "Esperanto" -> R.drawable.esperanto
                "Estonian" -> R.drawable.estonian
                "Serbian" -> R.drawable.serbian
                "Filipino" -> R.drawable.filipino
                "Slovak" -> R.drawable.slovak
                "Finnish" -> R.drawable.finnish
                "Slovenian" -> R.drawable.slovenian
                "French" -> R.drawable.french
                "Spanish" -> R.drawable.spanish
                "Galician" -> R.drawable.galician
                "Swahili" -> R.drawable.swahili
                "Georgian" -> R.drawable.georgian
                "Swedish" -> R.drawable.swedish
                "German" -> R.drawable.german
                "Tamil" -> R.drawable.tamil
                "Greek" -> R.drawable.greek
                "Telugu" -> R.drawable.telugu
                "Gujarati" -> R.drawable.gujarati
                "Thai" -> R.drawable.thai
                "Turkish" -> R.drawable.turkish
                "Hebrew" -> R.drawable.hebrew
                "Ukrainian" -> R.drawable.ukrainian
                "Hindi" -> R.drawable.hindi
                "Hungarian" -> R.drawable.hungarian
                "Vietnamese" -> R.drawable.vietnamese
                "Icelandic" -> R.drawable.icelandic
                "Welsh" -> R.drawable.welsh
                "Indonesian" -> R.drawable.indonesia
                "Yiddish" -> R.drawable.german
                "English" -> R.drawable.english
                "Urdu" -> R.drawable.urdu
                "Chinese" -> R.drawable.chinese
                "Russian" -> R.drawable.russian

                else -> R.drawable.amharic
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun translationCompleted(translation: String, language: String) {
        outputText.add(0, translation)
        adapter = RecyclerAdapter(
            requireActivity(),
            inputText,
            outputText,
            inputCountryList,
            outputCountryList,
            textToSpeech,
            outputLanguageCode,
            inputImages,
            outputImages,
            nativeAdSmall
        )
        rv.adapter = adapter
    }

    override fun onClick(position: Int) {}

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = "en-US"
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