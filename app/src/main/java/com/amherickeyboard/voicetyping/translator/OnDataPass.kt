package com.amherickeyboard.voicetyping.translator

import com.amherickeyboard.voicetyping.translator.Models.Image

interface OnDataPass {
    fun onDataPass(newList:ArrayList<Image>)
}