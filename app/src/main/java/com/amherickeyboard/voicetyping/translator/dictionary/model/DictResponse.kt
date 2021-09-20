package com.amherickeyboard.voicetyping.translator.dictionary.model

import com.amherickeyboard.voicetyping.translator.dictionary.Phonetics
import com.google.gson.annotations.SerializedName

data class DictResponse (
        @SerializedName("word") val word : String,
        @SerializedName("phonetics") val phonetics : List<Phonetics>,
        @SerializedName("meanings") val meanings : List<Meaning>
)