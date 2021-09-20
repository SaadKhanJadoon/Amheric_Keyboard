package com.amherickeyboard.voicetyping.translator.dictionary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.amherickeyboard.voicetyping.translator.InterstitialAds.InterstitialAdUpdated
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.activity.HomeScreen.Companion.isHomeFragment
import com.amherickeyboard.voicetyping.translator.databinding.FragmentDictionaryBinding
import com.amherickeyboard.voicetyping.translator.dictionary.adapter.PhoneticAdapter
import com.amherickeyboard.voicetyping.translator.dictionary.adapter.SynonymAdapter
import com.amherickeyboard.voicetyping.translator.dictionary.apiinterface.DictionaryResult
import com.amherickeyboard.voicetyping.translator.dictionary.apiinterface.ReturningResponse
import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse
import com.amherickeyboard.voicetyping.translator.dictionary.model.PhoneticModel
import com.amherickeyboard.voicetyping.translator.dictionary.model.SynonymModel
import com.amherickeyboard.voicetyping.translator.isNetworkConnected
import com.amherickeyboard.voicetyping.translator.showShortToast
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.fragment_dictionary.*

class DictionaryFragment : Fragment(), ReturningResponse, LifecycleObserver {

    private lateinit var dictionaryBinding: FragmentDictionaryBinding
    private val speak = Constants()
    private var adMicCont = 0
    private var dictionaryModelList: List<DictResponse?>? = null
    private var synonymAdapter: SynonymAdapter? = null
    private var phoneticAdapter: PhoneticAdapter? = null
    var synlist: MutableList<SynonymModel> = ArrayList()
    private var phonlist: MutableList<PhoneticModel> = ArrayList()
    private var dataloaded: Boolean = false
    private lateinit var nativeAd: NativeAd
    private lateinit var nativeAdView: NativeAdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dictionaryBinding = FragmentDictionaryBinding.inflate(layoutInflater, container, false)
        isHomeFragment = false

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        activity?.let { speak.ttsInitialization(it) }

        dictionaryBinding.inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (inputText.text.toString().isEmpty()) {
                    val imm =
                        activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    if (scrollCard.visibility == View.VISIBLE) {
                        scrollCard.visibility = View.GONE
                    }
                }
            }
        })

        dictionaryBinding.tvDefination.setOnClickListener {
            if (speak.getTts().isSpeaking)
                speak.getTts().stop()
            else {
                activity?.let { it1 ->
                    speak.forSpeak(
                        tvdefination.text.toString().replace("*", "").replace(
                            "^", ""
                        ).replace(
                            "!", ""
                        ).replace("~", ""), "eng", it1
                    )
                }
            }
        }

        dictionaryBinding.example.setOnClickListener {
            if (speak.getTts().isSpeaking)
                speak.getTts().stop()
            else
                activity?.let { it1 ->
                    speak.forSpeak(
                        tvexample.text.toString().replace("*", "").replace(
                            "^",
                            ""
                        ).replace(
                            "!",
                            ""
                        ).replace(
                            "~",
                            ""
                        ), "eng", it1
                    )
                }
        }

        dictionaryBinding.searchWord.setOnClickListener {
            if (dictionaryBinding.inputText.text.toString().isEmpty()) {
                activity?.showShortToast("please enter text")
            } else {
                if (activity?.isNetworkConnected() == true) {
                    adMicCont++
                    dictionaryBinding.progressBar.visibility = View.VISIBLE
                    dictionaryModelList = null
                    DictionaryResult.response(
                        word = inputText.text.toString().trim(),
                        returningResponse = this
                    )
                    if (adMicCont == 2) {
                        adMicCont = 0
                        showAd()
                    }
                } else {
                    activity?.showShortToast("no internet connection")
                }
            }
        }

        dictionaryBinding.dictionaryMic.setOnClickListener {
            dictionaryModelList = null
            voice()
        }

        if (activity?.isNetworkConnected() == true && !dataloaded) {
            dictionaryBinding.scrollCard.visibility = View.GONE
        }

        return dictionaryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nativeAdView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
        nativeAd = NativeAd()
        nativeAd.refreshAd(
            requireContext(),
            nativeAdView,
            dictionaryBinding.adFrameDictionary,
            dictionaryBinding.shimmerEffectDictionary
        )
    }

    private fun voice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Constants.inputLanguageCode)
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt)
        )
        try {
            this.startActivityForResult(intent, Constants.SpeechRequestCode)
        } catch (ex: Exception) {
            Toast.makeText(
                activity,
                getText(R.string.speech_not_supported),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onSuccess(response: List<DictResponse?>?) {
        super.onSuccess(response)
        dictionaryModelList = response
        if (!response?.isEmpty()!!) {
            dataloaded = true
            dictionaryBinding.scrollCard.visibility = View.VISIBLE
            displayDictionaryData(response)
        } else {
            dictionaryBinding.scrollCard.visibility = View.GONE

            Toast.makeText(
                activity,
                getText(R.string.no_result_found),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.SpeechRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val inputString = result!![0]
                    if (inputString.isNotEmpty()) {
                        val subPart = inputString.split(" ")
                        dictionaryBinding.inputText.setText(subPart[0])
                        dictionaryBinding.inputText.setSelection(subPart[0].length)
                        dictionaryBinding.progressBar.visibility = View.VISIBLE
                        DictionaryResult.response(
                            word = inputText.text.toString().trim(),
                            returningResponse = this
                        )
                        adMicCont++
                        if (adMicCont == 2) {
                            adMicCont = 0
                        }
                    } else {
                        dictionaryBinding.inputText.setText(getString(R.string.sorry))
                        dictionaryBinding.inputText.setSelection(inputString.length)
                        Toast.makeText(
                            activity,
                            "Input String not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun displayDictionaryData(dictionaryList: List<DictResponse?>?) {
        dictionaryBinding.dictionaryImage.visibility = View.GONE
        dictionaryBinding.progressBar.visibility = View.GONE
        dictionaryBinding.adFrameDictionary.visibility = View.GONE
        nativeAdView = layoutInflater.inflate(R.layout.ad_unified_small, null) as NativeAdView
        nativeAd = NativeAd()
        nativeAd.refreshAd(
            requireContext(),
            nativeAdView,
            dictionaryBinding.adFrameDictionarySmall,
            dictionaryBinding.shimmerEffectDictionary
        )
        dictionaryBinding.adFrameDictionarySmall.visibility = View.VISIBLE
        if (dictionaryList?.isNotEmpty() == true) {
            dictionaryBinding.wordTv.text = dictionaryList[0]?.meanings?.get(0)?.partOfSpeech
            dictionaryBinding.tvdefination.text =
                dictionaryList[0]?.meanings?.get(0)?.definitions?.get(0)?.definition
            dictionaryBinding.tvexample.text =
                dictionaryList[0]?.meanings?.get(0)?.definitions?.get(0)?.example
            if (dictionaryList[0]?.phonetics?.size == 0) {
                phonaticsgv.visibility = View.GONE
            } else {
                for (i in 0 until (dictionaryList[0]?.phonetics?.size!!)) {
                    phonlist.add(
                        PhoneticModel(
                            dictionaryList[0]?.phonetics?.get(i)?.text.toString(),
                            dictionaryList[0]?.phonetics?.get(i)?.audio.toString()
                        )
                    )
                }
                phoneticAdapter = PhoneticAdapter(requireContext(), phonlist)
                phonaticsgv?.adapter = phoneticAdapter
            }

            if (dictionaryList[0]?.meanings?.get(0)?.definitions?.get(0)?.synonyms.isNullOrEmpty()) {
            } else {
                for (i in 0 until dictionaryList[0]?.meanings?.get(0)?.definitions?.get(0)?.synonyms?.size!!) {
                    synlist.add(
                        SynonymModel(
                            dictionaryList[0]?.meanings?.get(0)?.definitions?.get(0)?.synonyms?.get(
                                i
                            )
                                .toString()
                        )
                    )
                }
                synonymAdapter = SynonymAdapter(requireContext(), synlist)
                Log.e("TAG", "displayDictionaryData: ${synlist.size}")
                synonimtv?.adapter = synonymAdapter
            }
        }
    }

    override fun onFail(message: String?) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onPause() {
        if (speak.getTts().isSpeaking) {
            speak.getTts().stop()
            speak.getTts().shutdown()
        }
        super.onPause()
    }

    private fun showAd() {
        InterstitialAdUpdated.getInstance().showInterstitialAdNew(requireActivity())
    }
}