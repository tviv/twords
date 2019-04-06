package com.tuvv.tword.features.twords

import android.graphics.drawable.Drawable
import com.tuvv.tword.features.BasePresenter
import com.tuvv.tword.features.BaseView
import com.tuvv.tword.features.SoundState
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord

interface TWordListContract {
    interface View: BaseView {
        fun showList(items: List<TWord>)

        fun goToItemDetails(id: Int)

        fun showEmptyResult(isFirstTime: Boolean)

        fun hideEmptyResult()

        val itemView: ItemView?

        fun showTutorial(searchTuto: Boolean, openDetailsTuto:Boolean): Boolean

        interface ItemView {

            fun showTranscription(id: Int, text:String)

            fun showDescription(id: Int, text:String)

            fun showImage(id: Int, img: Drawable?)

            fun showStateMainSound(id: Int, state: SoundState)

        }
    }


    interface Presenter: BasePresenter<View> {

        var currentFiltering: String?

        fun refreshList()

        fun refreshItem(item:Meaning)

        fun openItemDetails(item: Meaning)

        fun playMainSound(item: Meaning)

        fun stopMainSound(item: Meaning)

        fun openSettings()

    }
}