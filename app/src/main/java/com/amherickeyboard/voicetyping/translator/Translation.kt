package com.amherickeyboard.voicetyping.translator

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.AsyncTask.execute
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Translation(val context: Context)  {

    private var translationComplete: TranslationComplete? = null
    private var language = ""

    fun setTranslationComplete(translationComplete: TranslationComplete?) {
        this.translationComplete = translationComplete
    }

    private fun callUrlAndParseResult(word: String, outputLanguageCode: String): String {
        var text: String
        val response: StringBuffer
        if (word.contains("&") || word.contains("\n")) {
            text = word.trim { it <= ' ' }.replace("&", "^~^")
            text = text.trim { it <= ' ' }.replace("%", "!^")
            text = text.trim { it <= ' ' }.replace("\n", "~~")
            text = text.trim { it <= ' ' }.replace("-", "")
            text = text.trim { it <= ' ' }.replace("#", "")
        } else {
            text = word
        }
        val url: String
        url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=auto&" +
                "tl=" + outputLanguageCode +
                "&dt=t&q=" + text.trim { it <= ' ' }.replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8"
        Log.e("for_translation", url)
        val obj: URL
        try {
            obj = URL(url)
            val con = obj.openConnection() as HttpURLConnection
            con.setRequestProperty("User-Agent", "Mozilla/5.0")
            val `in` =
                    BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            response = StringBuffer()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return "0"
        } catch (e: IOException) {
            e.printStackTrace()
            return "0"
        }
        val outputString: String = parseResult(response.toString(), context, true)
        return if (outputString.isEmpty()) "Fail to Translate" else outputString
    }

    private fun callUrlAndParseResult(word: String, outputLanguageCode: String, inputLanguageCode: String): String {
        var text: String
        val response: StringBuffer
        if (word.contains("&") || word.contains("\n")) {
            text = word.trim { it <= ' ' }.replace("&", "^~^")
            text = text.trim { it <= ' ' }.replace("%", "!^")
            text = text.trim { it <= ' ' }.replace("\n", "~~")
            text = text.trim { it <= ' ' }.replace("-", "")
            text = text.trim { it <= ' ' }.replace("#", "")
        } else {
            text = word
        }
        val url: String
        url = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + inputLanguageCode + "&" +
                "tl=" + outputLanguageCode +
                "&dt=t&q=" + text.trim { it <= ' ' }.replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8"
        Log.d("uri", url)
        val obj: URL
        try {
            obj = URL(url)
            val con = obj.openConnection() as HttpURLConnection
            con.setRequestProperty("User-Agent", "Mozilla/5.0")
            val `in` =
                    BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            response = StringBuffer()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return "0"
        } catch (e: IOException) {
            e.printStackTrace()
            return "0"
        }
        val outputString: String = parseResult(response.toString(), context, false)
//        val outputString: String = parseResult(response.toString(), context!!, false)
        return if (outputString.isEmpty()) "2" else outputString
    }

    private fun parseResult(inputJson: String, context: Context, isAuto: Boolean): String {

        Log.e("result", inputJson)
        val tempData = StringBuilder()
        var data = ""
        try {
            val jsonArray = JSONArray(inputJson)
            val jsonArray2 = jsonArray[0] as JSONArray
            if (isAuto) {
                val language = jsonArray[jsonArray.length() - 1] as JSONArray
                val lang = language[0] as JSONArray
                this.language = lang[0].toString()
            }
            Log.d("Language_", language)
            for (i in 0 until jsonArray2.length()) {
                val jsonArray3 = jsonArray2[i] as JSONArray
                tempData.append(jsonArray3[0].toString())
            }
            data = tempData.toString()
        } catch (e: JSONException) {
            Log.e("error_", e.toString())
        } catch (e: Exception) {
            Log.e("error_s", e.toString())
            (context as Activity).runOnUiThread {
                Toast.makeText(
                        context,
                        "Something went wrong", Toast.LENGTH_SHORT
                ).show()
            }
        }
        data = data.replace("~~ ", "\n")
        data = data.replace("~ ~ ", "\n")
        data = data.replace("~ ~", "\n")
        data = data.replace("~~", "\n")
        data = data.replace(" !^ ", "%")
        data = data.replace(" ! ^ ", "%")
        data = data.replace("! ^", "%")
        data = data.replace(" ^ ~ ^ ", "&")
        data = data.replace("^ ~ ^", "&")
        data = data.replace(" ^~^ ", "&")
        data = data.replace("^~^", "&")
        return data
    }


    public fun translation() {

    }
    fun runTranslation(text: String, outputCode: String) {
        CoroutineScope(Dispatchers.Default).launch {

          val s=   callUrlAndParseResult(text, outputCode)
//            context.runOnUiThread {
                translationComplete?.translationCompleted(s, language)
//            }

        }
    }

    fun runTranslation(text: String, outputCode: String, inputCode: String)
    {
        CoroutineScope(Dispatchers.Default).launch {
           val s=  callUrlAndParseResult(text, outputCode,inputCode)

//            context.runOnUiThread {
                translationComplete?.translationCompleted(s, language)
//            }
        }
    }
    interface TranslationComplete {
        fun translationCompleted(translation: String, language: String)
    }
}