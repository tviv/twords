package com.tuvv.tword.model

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import org.junit.Test

import org.junit.Assert.*
import java.util.regex.Pattern

class MeaningTest1 {

    @Test
    fun getExamplesSpannedText() {

        val meaning = Meaning(
                id = 1,
                examples = arrayListOf(
                        Meaning.TextAndSound("The collar has a [tab] with a button hole."),
                        Meaning.TextAndSound("Pull the [tab] to open the can."),
                        Meaning.TextAndSound("Files with a red [tab] will be stored separately.")
                ))


        val result = meaning.examplesSpannedText
        assertEquals("The collar has a tab with a button hole.\n" +
                "\n" +
                "Pull the tab to open the can.\n" +
                "\n" +
                "Files with a red tab will be stored separately.", result.toString())
    }


}