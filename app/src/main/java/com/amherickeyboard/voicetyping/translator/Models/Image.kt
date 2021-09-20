package com.amherickeyboard.voicetyping.translator.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
        val imageUrl: String,
        val imgName: String
) : Parcelable