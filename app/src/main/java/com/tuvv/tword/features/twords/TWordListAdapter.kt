package com.tuvv.tword.features.twords

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.tuvv.tword.R
import com.tuvv.tword.features.SoundState
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import com.tuvv.tword.utils.components.TextAndSoundPlayerButton
import com.tuvv.tword.utils.extentions.getVisible
import com.tuvv.tword.utils.extentions.setVisible
import kotlinx.android.synthetic.main.item_list_content.view.*
import kotlinx.android.synthetic.main.item_list_content_header.view.*


class TWordListAdapter (
        hValues: List<TWord>,
        private val presenter: TWordListContract.Presenter,
        private val itemListener: ItemListener
):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), TWordListContract.View.ItemView {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_MEANING = 1

    private val items: MutableList<Any>  = ArrayList()
    private val viewMapHolders: BiMap<Int, RecyclerView.ViewHolder> = HashBiMap.create()

    init {
        //need to hierarchy list (maybe better to have it in repository)
        hValues.forEach { x -> x.run  {
            if (x.text == null) return@run

            items.add(x)
            var isSharedTranscripion = true
            val  trans = if (x.meanings.count() > 0) x.meanings[0].transcription else ""
            x.meanings.forEach {isSharedTranscripion = isSharedTranscripion && (trans.equals(it.transcription) || it.transcription.isNullOrEmpty())}
            var isShowHeader = true
            x.meanings.forEach {
                if (it.transcription != null) {
                    items.add(Pair(it, isShowHeader && (!it.transcription.isNullOrEmpty() || !it.soundUrl.isNullOrEmpty())))
                    isShowHeader = !isSharedTranscripion
                }
            }
        }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items.get(position) is TWord) {
            return VIEW_TYPE_HEADER
        } else //if (items.get(position) is Meaning)
        {
            return VIEW_TYPE_MEANING
        }
        //return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_list_content_header, parent, false)
                return ViewHolderHeader(view)
            }
            VIEW_TYPE_MEANING -> {
                val view = inflater.inflate(R.layout.item_list_content, parent, false)
                return ViewHolderContent(view)
            }
            else -> {
                throw Exception("wrong type of content list for view hoder")
            }
        }
    }

    //todo use more presenter??
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = items[position]


        when (holder.itemViewType) {
            VIEW_TYPE_HEADER -> {
                item as TWord
                holder as ViewHolderHeader
                holder.titleView.text = item.text
            }
            VIEW_TYPE_MEANING -> {
                item as Pair<*, *>
                val isShowHeader = item.second as Boolean
                item = item.first as Meaning
                viewMapHolders.forcePut(item.id, holder)
                with (holder as ViewHolderContent) {

                    if (item.previewUrl != null) holder.imageView.setImageResource(R.drawable.image_placeholder)
                    presenter.refreshItem(item)

                    headerView.setVisible(isShowHeader)
                    headerMulti.setVisible(headerView.getVisible() && item.parent?.meanings?.count()!! > 1)
                    headerView2.setVisible(headerView.getVisible() && !headerMulti.getVisible())
                    playButton.state = SoundState.N.ordinal
                    playButton.setVisible(!item.soundUrl.isNullOrEmpty())

                    playButton.setOnClickListener { if(playButton.state == SoundState.N.ordinal) presenter.playMainSound(item) else presenter.stopMainSound(item)}
                    with(itemView) {
                        tag = item
                        setOnClickListener { itemListener.onItemClick(it.tag as Meaning) }
                    }
                }
            }
        }

    }

    override fun getItemCount() = items.size


    inner class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.title
    }

    inner class ViewHolderContent(view: View) : RecyclerView.ViewHolder(view) {
        val descriptionView: TextView = view.description
        val imageView: ImageView = view.imageView
        //val transcriptionView: TextView = view.item_transcription
        val headerView: View = view.cardHeader
        val headerView2: View = view.auxCardHeader
        val headerMulti: View = view.auxHeaderMulti
        val playButton: TextAndSoundPlayerButton = view.playButton
    }

    interface ItemListener {

        fun onItemClick(item: Meaning)

    }

    //view contract
    override fun showTranscription(id: Int, text: String) {
        //getContHolder(id)?.transcriptionView?.text = text
        getContHolder(id)?.playButton?.text = text
    }

    override fun showDescription(id: Int, text: String) {
        getContHolder(id)?.descriptionView?.text = text
    }

    override fun showImage(id: Int, img: Drawable?) {
        getContHolder(id)?.imageView?.apply { setImageDrawable(img); setVisible(img != null);}
    }

    override fun showStateMainSound(id: Int, state: SoundState) {
        getContHolder(id)?.playButton?.state = state.ordinal
    }

    //end contract

    private fun getContHolder(id: Int) = (viewMapHolders.get(id) as? ViewHolderContent)

}


