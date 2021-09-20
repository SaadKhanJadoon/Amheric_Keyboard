package com.amherickeyboard.voicetyping.translator.adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.amherickeyboard.voicetyping.translator.NativeAds.NativeAd
import com.amherickeyboard.voicetyping.translator.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.*


class RecyclerAdapter(
    private var activity: Activity,
    private var inputConversation: ArrayList<String>,
    private var outputConversation: ArrayList<String>,
    private var countryInput : ArrayList<String>,
    private var countryOutput: ArrayList<String>,
    private var textToSpeech: TextToSpeech?,
    private var code: String,
    private var imagesInp: ArrayList<Int>,
    private var imagesOut: ArrayList<Int>,
    private var nativeAd: NativeAd
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val buttonClick = AlphaAnimation(5f, 0.8f)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemFlagInput: ImageView
        var itemFlagOutput: ImageView
        var countryInput: TextView
        var countryOutput: TextView
        var itemTranslationInput: TextView
        var itemTranslationOutput: TextView
        var menuButton: ImageView
        var adCard: NativeAdView
        var shimmerFrameLayout: ShimmerFrameLayout

        init {
            itemFlagInput = itemView.findViewById(R.id.flag_inp)
            itemFlagOutput = itemView.findViewById(R.id.flag_out)
            countryInput = itemView.findViewById(R.id.country_inp)
            countryOutput = itemView.findViewById(R.id.country_out)
            itemTranslationInput = itemView.findViewById(R.id.textView_inp)
            itemTranslationOutput = itemView.findViewById(R.id.textView_out)
            menuButton = itemView.findViewById(R.id.menuItem)
            adCard = itemView.findViewById(R.id.nativeAdCard)
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerEffectCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.conversation_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.shimmerFrameLayout.visibility = View.VISIBLE
            nativeAd.refreshAdSmallRecycle(
                activity,
                holder.adCard,
                holder.shimmerFrameLayout,
                holder.adCard
            )
        }

        holder.itemTranslationInput.text = inputConversation[position]
        holder.itemTranslationOutput.text = outputConversation[position]
        holder.itemFlagInput.setImageResource(imagesInp[position])
        holder.itemFlagOutput.setImageResource(imagesOut[position])
        holder.countryInput.text = countryInput[position]
        holder.countryOutput.text = countryOutput[position]

        holder.menuButton.setOnClickListener {
            val popup = PopupMenu(activity, holder.menuButton)
            popup.inflate(R.menu.adapter_menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.speak -> {
                            it.startAnimation(buttonClick)
                            speaker(outputConversation[position], code)
                            return true
                        }
                        R.id.copy -> {
                            copyButton(position)
                            return true
                        }
                        R.id.delete -> {
                            it.startAnimation(buttonClick)
                            removeAt(position)
                            return true
                        }
                        R.id.share -> {
                            it.startAnimation(buttonClick)
                            shareBtn(position)
                            return true
                        }
                        else -> false
                    }
                }
            })
            popup.show()
        }

    }

    override fun getItemCount(): Int {
        return inputConversation.size
    }

    interface onItemClick {
        fun onClick(position: Int)
    }

    fun removeAt(position: Int) {
        textToSpeech?.stop()
        inputConversation.removeAt(position)
        outputConversation.removeAt(position)
        imagesInp.removeAt(position)
        imagesOut.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, inputConversation.size)
        notifyItemRangeChanged(position, outputConversation.size)
    }

    fun copyButton(position: Int) {
        val clipboard = ContextCompat.getSystemService(activity, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("label", outputConversation[position])
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip)
            Toast.makeText(activity, "Copied", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareBtn(position: Int) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Arhamic Keyboard")
        shareIntent.putExtra(Intent.EXTRA_TEXT, outputConversation[position])
        val intent = Intent.createChooser(shareIntent, "Share via")
        startActivity(activity, intent, null)
    }

    fun speaker(text: String, code: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech!!.language = Locale.forLanguageTag(code)
            textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)

        }
    }
}