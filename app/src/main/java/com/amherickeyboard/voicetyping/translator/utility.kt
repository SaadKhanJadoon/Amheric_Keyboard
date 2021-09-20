package com.amherickeyboard.voicetyping.translator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Activity.sendEmail() {
    val mIntent = Intent(Intent.ACTION_SEND)
    mIntent.data = Uri.parse("mailto:")
    mIntent.type = "text/plain"
    mIntent.setPackage("com.google.android.gm");
    mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("toolplaza7@gmail.com"))
    mIntent.putExtra(Intent.EXTRA_SUBJECT, "Feed back Amharic Voice Keyboard")
    mIntent.putExtra(
        Intent.EXTRA_TEXT,
        "Tell us which issues you are facing using Amharic Voice Keyboard App?"
    )
    startActivity(mIntent)
}

fun Context.showShortToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.rateUs() {
    val uri = Uri.parse("market://details?id=$packageName")
    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(myAppLinkToMarket)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show()
    }
}