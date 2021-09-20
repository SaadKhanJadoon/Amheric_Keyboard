package com.amherickeyboard.voicetyping.translator.AppOpenAds

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.amherickeyboard.voicetyping.translator.R


class AppOpenLoading(
        private var context: Activity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_app_open_loading)

        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun dismiss() {
        if(isShowing && !context.isFinishing)
            super.dismiss()
    }
}