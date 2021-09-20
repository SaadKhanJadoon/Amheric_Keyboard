package com.amherickeyboard.voicetyping.translator.dictionary.model

import com.google.gson.annotations.SerializedName

data class Definitions (
        @SerializedName("definition") val definition : String,
        @SerializedName("synonyms") val synonyms : List<String>,
        @SerializedName("example") val example : String
)
