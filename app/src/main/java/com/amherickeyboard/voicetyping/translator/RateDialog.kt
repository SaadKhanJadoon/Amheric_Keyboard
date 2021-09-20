package com.amherickeyboard.voicetyping.translator

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.RatingBar
import androidx.core.content.res.ResourcesCompat
import com.amherickeyboard.voicetyping.translator.databinding.DialogRateUsBinding
import kotlinx.android.synthetic.main.dialog_rate_us.*

class RateDialog(var context: Activity) : Dialog(context), RatingBar.OnRatingBarChangeListener {

    private var isRateUs = false
    private lateinit var binding: DialogRateUsBinding
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("hindikeyboard", Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor? = sharedPreferences.edit()

    fun createRateUsDialog(closeApp: Boolean = false) {
        binding = DialogRateUsBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCancelable(true)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {
            RatingBar.onRatingBarChangeListener = this@RateDialog
            submitRating_.setOnClickListener {
                if (closeApp) {
                    if (isRateUs) {
                        context.rateUs()
                        editor?.putBoolean("isRateCompleted", true)
                        editor?.apply()
                    } else {
                        context.sendEmail()
                    }
                     context.finishAffinity()
                } else {
                    if (isRateUs) {
                        context.rateUs()
                        editor?.putBoolean("isRateCompleted", true)
                        editor?.apply()
                    } else {
                        context.sendEmail()
                    }
                     dismiss()
                }


            }
            notNowButton.setOnClickListener {
                if (closeApp) {
                    dismiss()
                    context.finishAffinity()
                } else {
                    dismiss()
                }
            }
        }

        if (!context.isFinishing) {
            show()
        }
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        binding.apply {
            if (rating.toInt() <= 4) {
                submitRating_.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_feedback, context.theme))
//                rateMsgText.visibility = View.GONE
            } else {
                isRateUs = true
//                rateMsgText.visibility = View.VISIBLE
                submitRating_.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_resource_continue, context.theme))
            }
        }
    }
}
