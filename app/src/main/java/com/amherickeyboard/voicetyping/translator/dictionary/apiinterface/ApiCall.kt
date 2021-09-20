package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiCall {
    fun response(result: ApiResultInterface): Callback<List<DictResponse?>?>? {
        return object : Callback<List<DictResponse?>?>
        {
            override fun onFailure(call: Call<List<DictResponse?>?>?, t: Throwable) {
                result.onFail(t.message)

            }

            override fun onResponse(call: Call<List<DictResponse?>?>?,
                                    response: Response<List<DictResponse?>?>?) {


                if(response!!.isSuccessful) {
                    result.onSuccess(response)
                    println("response//" + response.body())
                }else{
                    result.onFail("No Word Found")}

            }


        }
    }
}