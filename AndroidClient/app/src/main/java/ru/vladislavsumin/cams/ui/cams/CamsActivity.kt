package ru.vladislavsumin.cams.ui.cams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_cams.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.DatabaseUpdateState
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.impl.CamsManager
import ru.vladislavsumin.cams.ui.ListAdapter
import ru.vladislavsumin.cams.ui.ToolbarActivity
import ru.vladislavsumin.cams.ui.cams.details.CamDetailActivity
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.tag
import java.util.concurrent.TimeUnit


class CamsActivity : ToolbarActivity(), CamsView {
    companion object {
        private const val LAYOUT = R.layout.activity_cams

        private const val MENU_ADD_ITEM = 1

        val TAG = tag<CamsActivity>()

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, CamsActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: CamsPresenter

    private lateinit var mCamsAdapter: CamsListAdapter

    private var mSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(LAYOUT)
        super.onCreate(savedInstanceState)
    }

    override fun setupUi() {
        super.setupUi()
        setupCamsList()
    }

    override fun setupUx() {
        super.setupUx()
        mPresenter.observeCamsList()
                .observeOnMainThread()
                .subscribe(this::showList)
                .autoDispose()

        mPresenter.observeDatabaseStatus()
                //This need to minimize snackbar blinking
                .delaySubscription(1, TimeUnit.SECONDS)
                .observeOnMainThread()
                .subscribe(this::showDatabaseUpdateState)
                .autoDispose()
    }

    private fun setupCamsList() {
        mCamsAdapter = CamsListAdapter()
        cams_list.adapter = mCamsAdapter

        val layoutManager = LinearLayoutManager(this)
        cams_list.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        cams_list.addItemDecoration(dividerItemDecoration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(Menu.NONE, MENU_ADD_ITEM, Menu.NONE, R.string.add)
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_ADD_ITEM -> {
                startActivity(CamDetailActivity.getLaunchIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showList(cams: List<CameraEntity>) {
        mCamsAdapter.items = cams
    }

    private fun showDatabaseUpdateState(state: DatabaseUpdateState) {
        mSnackbar = when (state) {
            DatabaseUpdateState.UPDATED -> {
                mSnackbar?.dismiss()
                null
            }
            else -> {
                makeSnackbar(state)
            }
        }
    }

    private fun makeSnackbar(state: DatabaseUpdateState): Snackbar {
        val text = when (state) {//TODO move to resources
            DatabaseUpdateState.UPDATED -> throw RuntimeException("Unsupported state")
            DatabaseUpdateState.UPDATING -> "Updating database"
            DatabaseUpdateState.NOT_UPDATED -> "Database not updated"
            DatabaseUpdateState.ERROR -> "Error on update database"
        }

        val snackbar = Snackbar.make(cams_root_view, text, Snackbar.LENGTH_INDEFINITE)

        if (state == DatabaseUpdateState.NOT_UPDATED)
            snackbar.setAction("Update") { mPresenter.updateDatabase() }

        if (state == DatabaseUpdateState.ERROR)
            snackbar.setAction("Retry") { mPresenter.updateDatabase() }

        //TODO make custom layout
        snackbar.show()
        return snackbar
    }

    private inner class CamsListAdapter : ListAdapter<CameraEntity, CamsViewHolder>(CamsViewHolder.Companion) {

        override fun onBindViewHolder(holder: CamsViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            holder.itemView.setOnClickListener {
                val intent = CamDetailActivity.getLaunchIntent(it.context, items[position])
                startActivity(intent)
            }
        }
    }
}
