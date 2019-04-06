package com.tuvv.tword.features.meaning

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tuvv.tword.R
import com.tuvv.tword.features.SoundState
import com.tuvv.tword.utils.extentions.showErrorMessage
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.*
import javax.inject.Inject
import android.text.SpannableString
import com.tuvv.tword.utils.components.TextAndSoundPlayerButton
import com.tuvv.tword.utils.extentions.setVisible
import android.support.design.widget.AppBarLayout


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [MeaningDetailsActivity]
 * in two-pane mode (on tablets) or a [MeaningDetailsActivity]
 * on handsets.
 */
class MeaningDetailsFragment @Inject constructor() : DaggerFragment(), MeaningDetailsContract.View {

    companion object {
        const val KEY_TASK_ID = "TASK_ID"
    }

    @Inject
    internal lateinit var presenter: MeaningDetailsContract.Presenter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        arguments?.let {
            if (it.containsKey(KEY_TASK_ID)) {
                presenter.setId(it.getInt(KEY_TASK_ID))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.tuvv.tword.R.layout.item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleHeight = resources.getDimension(R.dimen.detail_title_layout_height)
        activity?.app_bar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val deltaOfOffsetAndTitle = Math.abs(verticalOffset) - titleHeight * 1.5
            val alpha: Float = (deltaOfOffsetAndTitle/ titleHeight * 2).toFloat()

            playButton?.setVisible(deltaOfOffsetAndTitle >= 0) //started collapsed
            playButton?.alpha = if (alpha >= 0 && alpha <= 1) alpha else 1f
        }) ?: playButton?.setVisible(true)
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe(this)
    }

    override fun onPause() {
        presenter.unsubscribe()
        super.onPause()
    }


    //view countract
    override fun showWordText(text: String, withSoundButton: Boolean) {
        activity?.apply {
            toolbar_layout?.title = text
            item_wordText?.apply {
                this.text = text
                buttonVisible = withSoundButton
                onPlayBlock = {presenter.playMainSound()}
                onStopBlock = {presenter.stopPlaying()}
            }
        }
        playButton?.apply {
            buttonVisible = withSoundButton
            onPlayBlock = {presenter.playMainSound()}
            onStopBlock = {presenter.stopPlaying()}
        }
    }

    override fun showImage(img: Drawable?) {
        activity?.imageView?.setImageDrawable(img)
        activity?.top_darker_layout?.setVisible(img != null)
    }

    override fun showTranscription(text: String) {
        item_transcription?.text = text
    }

    override fun showTranslation(text: String) {
        item_translation?.text = text

        item_translation_title.setVisible(true)
        item_translation.setVisible(true)
    }

    override fun showDefinition(text: String, withSoundButton: Boolean) {
        item_definition?.apply {
            this.text = text
            buttonVisible = withSoundButton
            onPlayBlock = {presenter.playDefinitionSound()}
            onStopBlock = {presenter.stopPlaying()}

        }

    }

    override fun showExamples(listTextAndSoundEnabling: List<Pair<SpannableString, Boolean>>) {
        examplesTitle.setVisible(listTextAndSoundEnabling.isNotEmpty())

        examplesContainer?.removeAllViews()
        listTextAndSoundEnabling.forEach {
            examplesContainer?.addView(createTextSoundItemForExample(it.first, it.second, examplesContainer.childCount,  exampleTemplate))
        }
    }

    override fun showMainSoundState(state: SoundState) {
        activity?.item_wordText?.state = state.ordinal
        playButton?.state = state.ordinal
    }

    override fun showDefinitionSoundState(state: SoundState) {
        item_definition?.state = state.ordinal
    }

    override fun showExampleSoundState(exampleIndex: Int, state: SoundState) {
        (examplesContainer.getChildAt(exampleIndex) as? TextAndSoundPlayerButton)?.state = state.ordinal
    }

    //now we only are to hide more detail fields (don't use for brief fields - most of time they are loaded inside already)
    override fun setLoadingIndicator(active: Boolean) {
        moreDetailsLayout?.visibility = if (active) View.GONE else View.VISIBLE
    }

    override fun showError(text: String?) {
        context?.showErrorMessage(text ?: getString(R.string.error_happened_default))
    }

    //end of view contract block


    //aux funcs
    private fun createTextSoundItemForExample(text: CharSequence, withSoundButton: Boolean, exampleIndex: Int, exampleTemplate: TextAndSoundPlayerButton): View {
        return TextAndSoundPlayerButton(this.context, null).apply {
            this.text = text
            this.buttonVisible = withSoundButton
            exampleTemplate.let {
                textAppearance = it.textAppearance
                setPadding(it.paddingLeft, it.paddingTop, it.paddingRight, it.paddingBottom)
                isTextSelectable = it.isTextSelectable
            }
            onPlayBlock = {presenter.playExampleSound(exampleIndex)}
            onStopBlock = {presenter.stopPlaying()}
        }
    }
}
