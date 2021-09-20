package com.amherickeyboard.voicetyping.translator.dictionary.model

import com.google.gson.annotations.SerializedName

data class Meaning (
        @SerializedName("partOfSpeech") val partOfSpeech : String,
        @SerializedName("definitions") val definitions : List<Definitions>
)