package ru.vladislavsumin.cams.ui.video

import android.net.Uri
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.core.mvp.BaseView

interface VideoView : BaseView {
    fun setVideoList(videos: List<Record>)
    fun playVideo(uri: Uri)
    fun dismissSaveVideoDialog()
    fun stopSaveVideoDialogAnimation()
    fun updateSavedRecordListElement(record: Record)
}