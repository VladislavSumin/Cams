package ru.vladislavsumin.cams.ui.video

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.android.synthetic.main.activity_video.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.ui.ListAdapter
import ru.vladislavsumin.cams.ui.ToolbarActivity
import ru.vladislavsumin.cams.ui.view.DatabaseUpdateStateSnackbar
import ru.vladislavsumin.core.utils.observeOnMainThread
import java.util.*


class VideoActivity : ToolbarActivity(), VideoView {
    companion object {
        private const val LAYOUT = R.layout.activity_video

        fun getLaunchIntent(context: Context) = Intent(context, VideoActivity::class.java)
    }

    @InjectPresenter
    lateinit var mPresenter: VideoPresenter

    private val player = VideoFragment()

    private lateinit var mSelectedRecord: RecordWithCamera //TODO change

    private lateinit var mSaveVideoDialog: SaveVideoDialog

    private lateinit var adapter: Adapter

    private lateinit var mSnackbar: DatabaseUpdateStateSnackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)
    }

    override fun setupUi() {
        super.setupUi()
        supportFragmentManager.beginTransaction().replace(R.id.container, player).commitNow()

        val layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(list.context, layoutManager.orientation)
        list.addItemDecoration(dividerItemDecoration)

        adapter = Adapter()
        list.adapter = adapter

        checkbox_only_saved.isChecked = mPresenter.showOnlySaved

        calendar.currentDate = CalendarDay.today()
        calendar.addDecorator(EventDecorator(getColor(R.color.red)))

        mSnackbar = DatabaseUpdateStateSnackbar(records_root_view)
    }

    override fun setupUx() {
        super.setupUx()
        mPresenter.observeRecords()
                .map { it.toMutableList() }
                .observeOnMainThread()
                .subscribe {
                    adapter.items = it
                }
                .autoDispose()

        checkbox_only_saved.setOnCheckedChangeListener { _, isChecked ->
            mPresenter.showOnlySaved = isChecked
        }

        calendar.setOnDateChangedListener { _, date, _ ->
            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day, 0, 0, 0)
            mPresenter.dateFilter = calendar.timeInMillis
        }

        button_clear_date_filter.setOnClickListener {
            calendar.selectedDate = null
            mPresenter.dateFilter = 0
        }

        mPresenter.observeDatabaseStatus()
                .observeOnMainThread()
                .subscribe(mSnackbar::showState)
                .autoDispose()

        mSnackbar.setCallback { mPresenter.updateDatabase() }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                setToolbarVisibility(false)
                setFullscreen(true)
                container.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT //TODO fix
            }

            else -> {
                setToolbarVisibility(true)
                setFullscreen(false)
                container.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    override fun setVideoList(videos: List<RecordWithCamera>) {
        adapter.items = videos
    }

    override fun playVideo(uri: Uri) {
        player.playVideo(uri)
    }

    override fun dismissSaveVideoDialog() {
        mSaveVideoDialog.dismiss()
    }

    override fun stopSaveVideoDialogAnimation() {
        mSaveVideoDialog.setState(SaveVideoDialog.State.Edit)
    }

    private fun initSaveDialog(record: RecordWithCamera) {
        mSelectedRecord = record
        mSaveVideoDialog = SaveVideoDialog(this, record.record).apply {
            setOnCancelClickListener {
                mPresenter.onCancelSaveDialog()
                dismiss()
            }
            setOnSaveClickListener {
                mPresenter.onSaveRecord(mSelectedRecord.record.id, getName())
                setState(SaveVideoDialog.State.Saving)
            }
            setOnClickDeleteListener {
                mPresenter.onDeleteRecord(mSelectedRecord.record.id)
                setState(SaveVideoDialog.State.Deleting)
            }
        }
    }

    private inner class Adapter : ListAdapter<RecordWithCamera, VideoViewHolder>(VideoViewHolder.Companion) {
        private var selectedItemPos: Int = -1

        override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val record = items[position]
            holder.setSelected(position == selectedItemPos)

            holder.itemView.setOnClickListener {
                //Change selected item
                val previousSelectedViewHolder = list.findViewHolderForAdapterPosition(selectedItemPos)
                if (previousSelectedViewHolder is VideoViewHolder)
                    previousSelectedViewHolder.setSelected(false)
                holder.setSelected(true)
                selectedItemPos = position

                mPresenter.onSelectRecord(record)
            }

            holder.itemView.setOnLongClickListener {
                initSaveDialog(record)
                mSaveVideoDialog.show()
                true
            }
        }
    }

    private class EventDecorator(
            private val color: Int,
            private val today: CalendarDay = CalendarDay.today()
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == today
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5f, color))
        }
    }
}
