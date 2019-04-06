package com.tuvv.tword.features.twords

import android.app.SearchManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.squareup.picasso.Picasso
import com.tuvv.tword.R
import com.tuvv.tword.features.meaning.MeaningDetailsActivity
import com.tuvv.tword.features.meaning.MeaningDetailsFragment
import com.tuvv.tword.model.Meaning
import com.tuvv.tword.model.TWord
import com.tuvv.tword.utils.extentions.replaceFragmentInActivity
import com.tuvv.tword.utils.extentions.showErrorMessage
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_item_list.*

import kotlinx.android.synthetic.main.item_list.*
import javax.inject.Inject






/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MeaningDetailsActivity] representing
 * item transcription. On tablets, the activity presents the list of items and
 * item transcription side-by-side using two vertical panes.
 */

class TWordListActivity : DaggerAppCompatActivity(), TWordListContract.View {

    companion object {
        private const val CURRENT_FILTERING_TEXT = "CURRENT_FILTERING_TEXT"
    }

    @Inject
    lateinit var presenter: TWordListContract.Presenter

    @Inject
    lateinit var detailFragment: MeaningDetailsFragment

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    var lastPos: Int = RecyclerView.NO_POSITION

    private var itemListener: TWordListAdapter.ItemListener = object : TWordListAdapter.ItemListener {
        override fun onItemClick(item: Meaning) {
            savePosition()
            presenter.openItemDetails(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            presenter.currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_TEXT)
                    as String
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.let {
                presenter.currentFiltering = it.toString()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

        initSearch()

        //for tablet
        twoPane = item_tword_detail_container != null

        presenter.subscribe(this)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putSerializable(CURRENT_FILTERING_TEXT, presenter.currentFiltering)
        })
    }

    public override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {presenter.openSettings(); true}
            android.R.id.home -> {onBackPressed(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }



    //contract of view
    override fun showList(items: List<TWord>) {
        handleNoResultLayout(visible = false)
        frameLayout.visibility = View.VISIBLE

        setupRecyclerView(item_list, items)

        restorePositionIfNeeded()

    }

    override fun goToItemDetails(id: Int) {
        if (twoPane) {
            val fragment = detailFragment
            fragment.apply {
                arguments = Bundle().apply {
                    putInt(MeaningDetailsFragment.KEY_TASK_ID, id)
                }
            }

            replaceFragmentInActivity(R.id.item_tword_detail_container, fragment)

        } else {
            val intent = Intent(this, MeaningDetailsActivity::class.java).apply {
                putExtra(MeaningDetailsFragment.KEY_TASK_ID, id)
            }
            this.startActivity(intent)
        }
    }

    override fun showError(text: String?) =
        showErrorMessage(text ?: getString(R.string.error_happened_default))


    override fun showEmptyResult(isFirstTime: Boolean) {
        handleNoResultLayout(visible = true, isFirstTime = isFirstTime)
        frameLayout.visibility = View.GONE
    }

    override fun hideEmptyResult() {
        handleNoResultLayout(visible = false)
        frameLayout.visibility = View.GONE
    }

    override fun setLoadingIndicator(active: Boolean) {
        loadingRefreshLayout.apply { post { isRefreshing = active }}
    }

    override val itemView: TWordListContract.View.ItemView?
        get() = (item_list.adapter as TWordListContract.View.ItemView)



    override fun showTutorial(searchTuto: Boolean, openDetailsTuto:Boolean):Boolean {
        val targetList: MutableList<TapTarget> = ArrayList()
        if (searchTuto) targetList.add(searchTutoTarget)
        if (openDetailsTuto) targetList.add(openDetailsTutoTarget)


        TapTargetSequence(this)
                .targets(targetList)
                .start()

        return openDetailsTuto
    }


    //end View Contract

    //todo move it into presenter
    @Inject
    lateinit var picasso: Picasso

    //recycle view
    private fun setupRecyclerView(recyclerView: RecyclerView, items: List<TWord>) {
        recyclerView.adapter = TWordListAdapter(items, presenter, itemListener)
    }


    //some init blocks for create
    private fun initSearch() {
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        (searchView as SearchView).apply {
            setQuery(presenter.currentFiltering, false)
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    applySearch(query)
                    searchView.clearFocus()

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    applySearch(newText)

                    return true
                }

            })
        }

        //aux funs

        //hide keybord of search view if we are scrolling
        item_list.setOnTouchListener { _, _ ->
            if (searchView.hasFocus())
                searchView.clearFocus()
            false
        }

        //and init refresh loading
        loadingRefreshLayout.apply {
            //under search bar
            setProgressViewEndTarget(false,  (resources.displayMetrics.density * 140).toInt())
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            setOnRefreshListener {
                presenter.refreshList()
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        searchView.clearFocus() //for that same activity (todo)
    }

    private fun applySearch(query: String?) {
        presenter.currentFiltering = query
    }

    private fun handleNoResultLayout(visible:Boolean, isFirstTime: Boolean = false) {
        noResultFrame.visibility = View.GONE
        noResultFrame_first.visibility = View.GONE

        with(if(isFirstTime) noResultFrame_first else noResultFrame) {
            if (visible) visibility = View.VISIBLE
        }
    }

    private fun savePosition() {
        lastPos = (item_list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    private fun restorePositionIfNeeded() {
        item_list.layoutManager?.apply {
            if (lastPos != RecyclerView.NO_POSITION) {
                scrollToPosition(lastPos)
                lastPos = RecyclerView.NO_POSITION
            }
        }
    }

    //tuto
    val searchTutoTarget: TapTarget get () =
        TapTarget.forView(searchTutorialAnchor, getString(R.string.start_from_search))
            .transparentTarget(true)
            .outerCircleColor(R.color.colorAccent)
            .outerCircleAlpha(0.75f)
            //.icon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_search))
            .cancelable(true)

    val openDetailsTutoTarget: TapTarget get () =
        TapTarget.forView(openTutorialAnchor, getString(R.string.open_details_by_clicking))
                .outerCircleColor(R.color.colorAccent)
                .icon(ContextCompat.getDrawable(this, R.drawable.ic_touch_app_24dp))
                .outerCircleAlpha(0.75f)
                .cancelable(true)

}
