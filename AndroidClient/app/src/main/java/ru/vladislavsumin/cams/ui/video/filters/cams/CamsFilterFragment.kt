package ru.vladislavsumin.cams.ui.video.filters.cams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_cams_filter.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.ui.ListAdapter
import ru.vladislavsumin.core.mvp.BaseFragment
import ru.vladislavsumin.core.ui.recyclerview.RWBaseViewHolder

class CamsFilterFragment : BaseFragment() {
    private var mCams: List<CameraWithSelection> = emptyList()
    private val mAdapter = Adapter()
    private var mCallback: ((cams: List<CameraEntity>) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cams_filter, container, false)
    }

    override fun setupUi() {
        super.setupUi()
        cams_filter_list.adapter = mAdapter

        val layoutManager = LinearLayoutManager(this.context)
        cams_filter_list.layoutManager = layoutManager
    }

    @UiThread
    fun setCams(cams: List<CameraEntity>) {
        mCams = cams.map { CameraWithSelection(it) }
        mAdapter.items = mCams
    }

    @AnyThread
    fun setCallback(callback: ((cams: List<CameraEntity>) -> Unit)?) {
        mCallback = callback
    }

    private fun notifyChanges() {
        //TODO async this
        val callback = mCallback ?: return
        val list = mCams.filter { it.selected }.map { it.cameraEntity }
        callback.invoke(list)
    }

    private inner class Adapter : ListAdapter<CameraWithSelection, ViewHolder>(ViewHolderFactory())

    private inner class ViewHolder(view: View) : RWBaseViewHolder<CameraWithSelection>(view) {
        private val mCheckedTextView = view as AppCompatCheckBox
        private lateinit var mItem: CameraWithSelection

        init {
            mCheckedTextView.setOnClickListener {
                it as AppCompatCheckBox
                mItem.selected = it.isChecked
                notifyChanges()
            }
        }

        override fun bind(item: CameraWithSelection) {
            mItem = item
            mCheckedTextView.text = item.cameraEntity.name
            mCheckedTextView.isChecked = item.selected
        }

    }

    private inner class ViewHolderFactory : RWBaseViewHolder.ViewHolderFactory<ViewHolder> {
        override fun getInstance(parent: ViewGroup): ViewHolder {
            val view = AppCompatCheckBox(parent.context)
            view.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            return ViewHolder(view)
        }
    }


    private data class CameraWithSelection(val cameraEntity: CameraEntity,
                                           var selected: Boolean = false)
}