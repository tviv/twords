package com.tuvv.tword.features.meaning

import android.graphics.drawable.Drawable
import android.text.SpannableString
import com.tuvv.tword.features.BasePresenter
import com.tuvv.tword.features.BaseView
import com.tuvv.tword.features.SoundState


interface MeaningDetailsContract {
    interface  View: BaseView {

        fun showWordText(text: String, withSoundButton: Boolean)

        fun showTranscription(text: String)

        fun showTranslation(text: String)

        fun showImage(img: Drawable?)

        fun showDefinition(text: String, withSoundButton: Boolean)

        fun showExamples(listTextAndSoundEnabling: List<Pair<SpannableString, Boolean>>)

        fun showMainSoundState(state: SoundState)

        fun showDefinitionSoundState(state: SoundState)

        fun showExampleSoundState(exampleIndex: Int, state: SoundState)
    }

    interface Presenter: BasePresenter<View> {

        fun setId(id: Int)

        fun playMainSound()

        fun playDefinitionSound()

        fun playExampleSound(exampleIndex: Int)

        fun stopPlaying()

    }
}