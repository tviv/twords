package com.tuvv.tword.features.twords

import android.content.Context
import com.squareup.picasso.Picasso
import com.tuvv.tword.R
import com.tuvv.tword.features.GeneralMainPresenterWork
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import com.tuvv.tword.model.source.TWordContent
import com.tuvv.tword.utils.events.Events
import com.tuvv.tword.utils.events.RxBus
import com.tuvv.tword.utils.settings.Settings
import com.tuvv.tword.utils.sound.SoundPlayer
import com.tuvv.tword.utils.schedulers.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TWordListPresenter @Inject constructor(
        val content: TWordContent,
        val settings: Settings,
        context: Context,
        picasso: Picasso,
        private val schedule: SchedulerProvider,
        private val bus: RxBus,
        soundPlayer: SoundPlayer
): GeneralMainPresenterWork(context, soundPlayer, picasso), TWordListContract.Presenter {
    companion object {
        const val REQUEST_DELAY = 300L //millis
    }

    private var view: TWordListContract.View? = null

    private val contentCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var settingsEditDisposable: Disposable? = null
    private var wasSearchTutorial = false

    //queue for whom waiting details will be loaded from server
    private val toDetailsShowQueue:Stack<Int> = Stack()


    private fun loadList(force: Boolean = false) {
        contentCompositeDisposable.clear()
        toDetailsShowQueue.clear()
        view?.hideEmptyResult()

        val loadingIndicatorDisposal = Completable
                .timer(REQUEST_DELAY * 3, TimeUnit.MILLISECONDS)
                .observeOn(schedule.ui())
                .subscribe{ view?.setLoadingIndicator(true) }

        contentCompositeDisposable.add(loadingIndicatorDisposal)
        contentCompositeDisposable.add(content.getList(currentFiltering ?: "", force)
                .delaySubscription(REQUEST_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(schedule.io())
                .observeOn(schedule.ui())
                .doFinally {
                    loadingIndicatorDisposal.dispose()
                    view?.setLoadingIndicator(false)
                }
                .doOnNext {
                    items -> doAdditionlyAfterGettingList(items)
                    println("getList request")
                }
                .subscribe(
                        //onNext
                        {
                            items -> processList(items)
                        },
                        //error
                        {
                            view?.showError()
                            processList(ArrayList())
                        }
                )
        )

    }

    private fun getItemDetailsList(sitems: List<TWord>) {

        val idsArray:MutableList<Int> = ArrayList()
        TWordContent.callbackForMeanings(sitems) { _, _, meaning -> idsArray.add(meaning.id)}

        contentCompositeDisposable.add(content.getItems(ids = idsArray.toTypedArray())
                .subscribeOn(schedule.io())
                .observeOn(schedule.ui())
                .subscribe(
                        //onNext
                        {items ->
                            TWordContent.callbackForMeanings(sitems) { _, _, meaning -> meaning.copyIfNeeded(items?.get(meaning.id))}

                            while (toDetailsShowQueue.isNotEmpty()) {
                                val id = toDetailsShowQueue.pop()
                                items?.get(id)?.definition?.text?.let {
                                    text -> view?.itemView?.showDescription(id, text)
                                }
                            }
                        },
                        //onError
                        {view?.showError()}
                )
        )

    }

    //contract
    private var _currentFiltering: String = ""
    override var currentFiltering: String?
        get() = _currentFiltering
        set(value) {
            _currentFiltering = value ?: ""
            loadList()
        }

    override fun openItemDetails(item: Meaning) {
        view?.goToItemDetails(item.id)
    }
    override fun refreshList() {
        loadList(force = true)
    }

    override fun refreshItem(item: Meaning) {
        view?.itemView?.apply {
            showTranscription(item.id, item.transcription ?: "")
            if (settings.isRussianTranslation) {
                showDescription(item.id, item.translationText)
            } else {
                item.definition?.text?.let {text -> showDescription(item.id, text)}
                        ?: run {toDetailsShowQueue.push(item.id)}
            }

            getImageByUrl(
                    imgUrl = item.previewUrl,
                    successBlock = { showImage(item.id, it) },
                    errorBlock = {view?.showError(context.getString(R.string.image_loading_error))})
        }
    }

    private var firstProcessTime: Boolean = true
    private fun processList(items: List<TWord>) {
        if (items.count() > 0)  view?.showList(items)
        else view?.showEmptyResult(isFirstTime = firstProcessTime || currentFiltering.isNullOrEmpty())
        firstProcessTime = false

    }

    override fun subscribe(view: TWordListContract.View) {
        this.view = view
        toDetailsShowQueue.clear()
        settingsEditDisposable = bus.toObservable()
                .filter { it is Events.SettingManuallyChanged }
                .observeOn(SchedulerProvider.instance.ui())
                .subscribe {
                    wasSearchTutorial = false
                    loadList(force = true)
                }

        loadList()
    }

    override fun unsubscribe() {
        view = null
        settingsEditDisposable?.dispose()
        release()
    }

    override fun playMainSound(item: Meaning) {
        playRemoteSound(item.soundUrl, { view?.itemView?.showStateMainSound(item.id, it) }, { view?.showError() })
    }

    override fun stopMainSound(item: Meaning) {
        stopSound()
    }

    override fun openSettings() {
        settings.edit()
    }
    //end contract


    //aux funs

    private fun setParentRefIntoChildren(items: List<TWord>) {
        TWordContent.callbackForMeanings(items) { _, tword, meaning ->  meaning.apply {
            parent = tword
            text = tword.text
        }}
    }

    private fun doAdditionlyAfterGettingList(items: List<TWord>) {
        if (!settings.wasTutorial) {
            settings.wasTutorial = view?.showTutorial(
                    searchTuto = !wasSearchTutorial,
                    openDetailsTuto = items.isNotEmpty()
            ) ?: false
            wasSearchTutorial = true
        }

        setParentRefIntoChildren(items) //it is needed to show detail besides on detail api load result

        if (!settings.isRussianTranslation) { //then we will show definition instead of
            getItemDetailsList(items)
        }
    }


}