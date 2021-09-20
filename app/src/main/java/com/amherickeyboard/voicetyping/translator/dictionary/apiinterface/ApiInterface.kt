package com.amherickeyboard.voicetyping.translator.dictionary.apiinterface

import com.amherickeyboard.voicetyping.translator.dictionary.model.DictResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiInterface {
    @GET
    fun fetching(@Url url:String): Call<List<DictResponse?>?>?
}