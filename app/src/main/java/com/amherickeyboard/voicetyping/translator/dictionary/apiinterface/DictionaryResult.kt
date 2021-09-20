package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import com.amherickeyboard.voicetyping.translator.dictionary.Constants
import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse
import retrofit2.Response

object DictionaryResult : ApiResultInterface {

    private  lateinit var returningResponse: ReturningResponse
    fun response(lang: String? = "en", word: String, returningResponse: ReturningResponse) {
        val completeurl = StringBuilder()
        completeurl.append(Constants.url).append(lang).append("/$word")
        Api.getInstance().fetching(completeurl.toString())?.
        enqueue(ApiCall.response(this@DictionaryResult)!!)
        DictionaryResult.returningResponse = returningResponse
    }

    override fun onSuccess(response: Response<List<DictResponse?>?>?) {
        println("response////"+response.toString())
        returningResponse.onSuccess(response!!.body())
    }

    override fun onFail(message: String?) {
        returningResponse.onFail(message)
    }
}