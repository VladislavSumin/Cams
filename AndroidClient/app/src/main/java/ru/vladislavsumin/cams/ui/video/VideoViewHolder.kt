package ru.vladislavsumin.cams.ui.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.core.ui.recyclerview.RWBaseViewHolder
import java.text.SimpleDateFormat

class VideoViewHolder private constructor(itemView: View) : RWBaseViewHolder<RecordWithCamera>(itemView) {
    companion object : ViewHolderFactory<VideoViewHolder> {
        private const val LAYOUT = R.layout.list_videos_element

        private val dataFormat = SimpleDateFormat.getDateTimeInstance()

        override fun getInstance(parent: ViewGroup): VideoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(LAYOUT, parent, false)
            return VideoViewHolder(view)
        }
    }

    private val cameraName: TextView = itemView.findViewById(R.id.text_name)
    private val date: TextView = itemView.findViewById(R.id.text_date)
    private val recordName: TextView = itemView.findViewById(R.id.record_name)
    private val iconSaved: ImageView = itemView.findViewById(R.id.ic_saved)
    private val saveLayout: LinearLayout = itemView.findViewById(R.id.save_layout)

    fun setSelected(selected: Boolean) {
        itemView.setBackgroundColor(
                itemView.context.getColor(
                        if (selected) R.color.grey else android.R.color.white
                )
        )
    }

    override fun bind(item: RecordWithCamera) {
        saveLayout.visibility = if (item.record.keepForever || item.record.name != null) View.VISIBLE else View.GONE
        iconSaved.visibility = if (item.record.keepForever) View.VISIBLE else View.GONE

        recordName.text = item.record.name ?: "<no name>"

        val textColor = if (item.record.name != null) R.color.black else R.color.grey
        recordName.setTextColor(recordName.context.getColor(textColor))

        cameraName.text = item.camera?.name //TODO fix
        cameraName.textSize = if (item.record.keepForever) 20f else 22f

        date.text = dataFormat.format(item.record.timestamp)
    }
}
