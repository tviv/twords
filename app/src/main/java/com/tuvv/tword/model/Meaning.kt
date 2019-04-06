package com.tuvv. tword.model

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.regex.Pattern

data class Meaning (
        @Expose val id: Int,
        @Expose var wordId: Int? = null,

        var parent: TWord? = null,

        @Expose var text: String? = null,

        @Expose val translation: TextAndNote? = null,

        @Expose val transcription: String? = null,

        @SerializedName("previewUrl")
         @Expose private var previewUrl_: String? = null,

        @SerializedName("imageUrl")
         @Expose private var imageUrl_: String? = null,

        @Expose private var images: List<SimpleUrl>? = null,

        @SerializedName("soundUrl")
         @Expose private var soundUrl_: String? = null,
        @Expose var definition: TextAndSound? = null,
        @Expose var examples: List<TextAndSound>? = null

) {

     data class TextAndNote(@Expose val text: String?, @Expose val note: String? )
     class TextAndSound(@Expose val text: String?,
                             @SerializedName("soundUrl")
                             @Expose var soundUrl_: String? = null ) {
         var soundUrl: String? //todo create distinct class
             set(value) {soundUrl_ = value}
             get() =  addHttpsIfNeeded(soundUrl_)
     }
    data class SimpleUrl(@Expose val url: String)
    data class SpannableTextAndSound(val text: SpannableString?, val soundUrl: String?)

     //code here is done because url can be without protocol name
     var previewUrl: String?
         set(value) {previewUrl_ = value}
         get() =  addHttpsIfNeeded(previewUrl_)
     var imageUrl: String?
         set(value) {imageUrl_ = value}
         get() = addHttpsIfNeeded(imageUrl_
                 //if short info was uploaded by android os, get image from other url
                 ?: if (images != null && images!!.count() > 0) {
                     images!![0].url
                 } else null)

     var soundUrl: String?
         set(value) {soundUrl_ = value}
         get() =  addHttpsIfNeeded(soundUrl_)

     val examplesText: String
         get() = examples?.joinToString("\n\n") {it.text ?: ""} ?: ""

    val spannableExamples
        get() = examples?.map { SpannableTextAndSound(replaceSquareBracesWithBoldText(it.text?:""), it.soundUrl) } ?: ArrayList()

    //bold font where word is in square braces
     val examplesSpannedText: SpannableString
         get() = replaceSquareBracesWithBoldText(examplesText)

     val translationText: String //text plus note in one string
        get() {
            val notePart = translation?.note.let {if (!it.isNullOrEmpty()) "\n($it)" else ""}

            return translation?.text.let { if (!it.isNullOrEmpty()) it + notePart  else "" }

        }


    fun copyIfNeeded(item: Meaning?) {
        item?.definition?.let { definition = it }
        item?.examples?.let { examples = it }
        item?.images?.let { images = it }
        item?.wordId?.let { wordId = it }
        item?.text?.let { text = it }
    }

    //aux funcs

     companion object {
         private fun addHttpsIfNeeded(url: String?) = url?.replace(Regex("^//"), "https://")

         fun replaceSquareBracesWithBoldText(text: CharSequence) : SpannableString {
             fun positionWithOffset(pos:Int, offset: Int):Int = pos - offset

             val ssb = SpannableStringBuilder(text)
             var offset = 0
             val m = Pattern.compile("\\[.+?]")
                     .matcher(text)
             while (m.find()) {
                 val start = positionWithOffset(m.start(), offset++)
                 ssb.replace(start, start + 1, "")
                 val end = positionWithOffset(m.end(), offset ++)
                 ssb.setSpan(
                         StyleSpan(Typeface.BOLD), // Span to add
                         start, // Start of the span (inclusive)
                         end,
                         Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                 ssb.replace(end-1, end, "")
             }
             return SpannableString(ssb)

         }

     }

     override fun toString(): String = "$text -> ${translation?.text}"

}

