package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import android.util.Log
import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse

interface ReturningResponse {

    fun onSuccess(
        response: List<DictResponse?>?
    ) {
        Log.e("Respos", "working")
    }

    fun onFail(message: String?)
}
