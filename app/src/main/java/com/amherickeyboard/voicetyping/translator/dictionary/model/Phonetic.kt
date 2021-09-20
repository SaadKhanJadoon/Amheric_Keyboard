package com.amherickeyboard.voicetyping.translator.dictionary

import com.google.gson.annotations.SerializedName

data class Phonetics (
        @SerializedName("text") val text : String,
        @SerializedName("audio") val audio : String
)
