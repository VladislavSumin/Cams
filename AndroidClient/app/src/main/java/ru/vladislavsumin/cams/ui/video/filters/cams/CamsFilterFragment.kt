package ru.vladislavsumin.cams.ui.video.filters.cams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.core.mvp.BaseFragment

class CamsFilterFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cams_filter, container, false)
    }
}