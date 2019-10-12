package ru.vladislavsumin.cams.ui.cams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_cams.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.impl.CamsManager
import ru.vladislavsumin.cams.ui.MutableListAdapter
import ru.vladislavsumin.cams.ui.ToolbarActivity
import ru.vladislavsumin.cams.ui.cams.details.CamDetailActivity
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.tag
import java.util.concurrent.TimeUnit


class CamsActivity : ToolbarActivity(), CamsView {
    companion object {
        private const val LAYOUT = R.layout.activity_cams

        private const val MENU_ADD_ITEM = 1
        private const val REQUEST_CODE_CAMERA_DETAILS = 1
        private const val REQUEST_CODE_CAMERA_ADD_NEW = 2

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
        // setup adapter
        mCamsAdapter = CamsListAdapter()
        cams_list.adapter = mCamsAdapter

        // setup layout manager
        val layoutManager = LinearLayoutManager(this)
        cams_list.layoutManager = layoutManager

        // setup line divider
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
                val launchIntent = CamDetailActivity.getLaunchIntent(this)
                startActivityForResult(launchIntent, REQUEST_CODE_CAMERA_ADD_NEW)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showList(cams: List<CameraEntity>) {
        cams_list.visibility = View.VISIBLE
        mCamsAdapter.items = cams as MutableList //TODO FIX
    }

    private fun showDatabaseUpdateState(state: CamsManager.DatabaseUpdateState) {
        mSnackbar = when (state) {
            CamsManager.DatabaseUpdateState.UPDATED -> {
                mSnackbar?.dismiss()
                null
            }
            else -> {
                makeSnackbar(state)
            }
        }
    }

    private fun makeSnackbar(state: CamsManager.DatabaseUpdateState): Snackbar {
        val text = when (state) {//TODO move to resources
            CamsManager.DatabaseUpdateState.UPDATED -> throw RuntimeException("Unsupported state")
            CamsManager.DatabaseUpdateState.UPDATING -> "Updating database"
            CamsManager.DatabaseUpdateState.NOT_UPDATED -> "Database not updated"
            CamsManager.DatabaseUpdateState.ERROR -> "Error on update database"
        }

        val snackbar = Snackbar.make(cams_root_view, text, Snackbar.LENGTH_INDEFINITE)

        if (state == CamsManager.DatabaseUpdateState.NOT_UPDATED)
            snackbar.setAction("Update") { mPresenter.updateDatabase() }

        if (state == CamsManager.DatabaseUpdateState.ERROR)
            snackbar.setAction("Retry") { mPresenter.updateDatabase() }

        //TODO make custom layout
        snackbar.show()
        return snackbar
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 0) return

        val camera = data!!.getParcelableExtra<CameraEntity>(CamDetailActivity.EXTRA_CAM_DETAILS)!!

        if (resultCode == CamDetailActivity.RESULT_SAVE_OR_UPDATE)
            when (requestCode) {
                REQUEST_CODE_CAMERA_DETAILS -> {
                    mCamsAdapter.updateItemBy(camera) { it.id == camera.id }
                }

                REQUEST_CODE_CAMERA_ADD_NEW -> {
                    mCamsAdapter.addItem(camera)
                }
            }

        if (resultCode == CamDetailActivity.RESULT_DELETE)
            mCamsAdapter.removeItemBy { it.id == camera.id }
    }

    private inner class CamsListAdapter : MutableListAdapter<CameraEntity, CamsViewHolder>(CamsViewHolder.Companion) {

        override fun onBindViewHolder(holder: CamsViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            holder.itemView.setOnClickListener {
                val intent = CamDetailActivity.getLaunchIntent(it.context, items[position])
                startActivityForResult(intent, REQUEST_CODE_CAMERA_DETAILS)
            }
        }
    }
}
