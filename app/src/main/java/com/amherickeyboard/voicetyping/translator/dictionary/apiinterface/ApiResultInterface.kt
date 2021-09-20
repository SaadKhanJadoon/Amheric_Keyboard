package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse
import retrofit2.Response

interface ApiResultInterface {
    fun onSuccess(
            response: Response<List<DictResponse?>?>?
    )

    fun onFail(message: String?)
}