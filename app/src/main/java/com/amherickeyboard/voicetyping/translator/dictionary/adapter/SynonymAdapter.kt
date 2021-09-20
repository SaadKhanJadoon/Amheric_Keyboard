package com.amherickeyboard.voicetyping.translator.dictionary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.amherickeyboard.voicetyping.translator.R
import com.amherickeyboard.voicetyping.translator.dictionary.model.SynonymModel
import kotlinx.android.synthetic.main.phonaticslayout.view.*

class SynonymAdapter(context: Context, var foodsList: MutableList<SynonymModel> = ArrayList()) :
    BaseAdapter() {
    var context: Context? = context

    override fun getCount(): Int {
        return foodsList.size
    }

    override fun getItem(position: Int): Any {
        return foodsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val foodView = inflater.inflate(R.layout.phonaticslayout, null)
        foodView.tvphonetics.text = foodsList[position].s
        return foodView
    }
}
