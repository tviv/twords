package com.tuvv.tword.features.meaning

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import com.squareup.picasso.Picasso
import com.tuvv.tword.R
import com.tuvv.tword.features.GeneralMainPresenterWork
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.source.TWordContent
import com.tuvv.tword.utils.settings.Settings
import com.tuvv.tword.utils.sound.SoundPlayer
import com.tuvv.tword.utils.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MeaningDetailsPresenter
    @Inject constructor(
            val content: TWordContent,
            val settings: Settings,
            picasso: Picasso,
            context: Context,
            val schedule: SchedulerProvider,
            soundPlayer: SoundPlayer
    )
    : GeneralMainPresenterWork(context, soundPlayer, picasso),  MeaningDetailsContract.Presenter {
    private var view: MeaningDetailsContract.View? = null

    private var id: Int? = null
    private var currentItem: Meaning? = null

    private var compositeDisposable: CompositeDisposable? = null

    override fun setId(id: Int) {
        this.id = id
        this.currentItem = null
        view?.let { processItem() }
    }

    override fun subscribe(view: MeaningDetailsContract.View) {
        this.view = view
        compositeDisposable = CompositeDisposable()
        processItem()
    }

    override fun unsubscribe() {
        view = null
        compositeDisposable?.dispose()
        compositeDisposable = null
        release()
    }

    fun processItem() {
        view?.setLoadingIndicator(true)
        var imageAlreadyLoaded = false;
        id?.let {
            compositeDisposable?.add(content.getItem(it)
                    .subscribeOn(schedule.io())
                    .observeOn(schedule.ui())
                    .doFinally { view?.setLoadingIndicator(false) }
                    .subscribe(
                            //onNext
                            {item ->
                                currentItem = item
                                view?.showWordText(ot(item.text), !TextUtils.isEmpty(item.soundUrl))
                                view?.showTranscription(ot(item.transcription))
                                if (settings.isRussianTranslation) view?.showTranslation(ot(item.translationText))
                                item.imageUrl?.let { if (!imageAlreadyLoaded) showImage(item.imageUrl) }
                                        .also { imageAlreadyLoaded = true }

                                view?.showDefinition(item.definition?.let { ot(it.text) }
                                        ?: NO_TEXT_STR, !TextUtils.isEmpty(item.definition?.soundUrl))

                                view?.showExamples(item.spannableExamples.map {
                                    ex -> Pair(ot(ex.text), !TextUtils.isEmpty(ex.soundUrl))
                                })
                            },
                            //onError
                            {view?.showError()}
                    )
            )
        }
    }

    override fun playMainSound() {
        playRemoteSound(currentItem?.soundUrl, { view?.showMainSoundState(it) }, { view?.showError() })
    }

    override fun playDefinitionSound() {
        playRemoteSound(currentItem?.definition?.soundUrl, { view?.showDefinitionSoundState(it) }, { view?.showError() })
    }

    override fun playExampleSound(exampleIndex: Int) {
        playRemoteSound(currentItem?.examples?.get(exampleIndex)?.soundUrl, { view?.showExampleSoundState(exampleIndex, it) }, { view?.showError() })
    }

    override fun stopPlaying() {
        stopSound()
    }

    fun showImage(imgUrl: String?) {
        getImageByUrl(imgUrl, {view?.showImage(it)}, {view?.showError(context.getString(R.string.image_loading_error))})
    }



    companion object {
        private const val NO_TEXT_STR = "-"

        //default value for empty objects
        private fun ot(value: String?): String = value?.let { if (it.length >= 0) it else NO_TEXT_STR } ?: NO_TEXT_STR
        private fun ot(value: SpannableString?): SpannableString = value?.let { if (it.length >= 0) it else SpannableString(NO_TEXT_STR) } ?: SpannableString(NO_TEXT_STR)


    }
}