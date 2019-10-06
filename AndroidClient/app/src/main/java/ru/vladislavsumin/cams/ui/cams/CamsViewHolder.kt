package ru.vladislavsumin.cams.ui.cams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.entity.CameraEntity
import ru.vladislavsumin.core.ui.recyclerview.RWBaseViewHolder

class CamsViewHolder private constructor(view: View) : RWBaseViewHolder<CameraEntity>(view) {
    companion object : ViewHolderFactory<CamsViewHolder> {

        private const val LAYOUT = R.layout.list_cams_element

        override fun getInstance(parent: ViewGroup): CamsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(LAYOUT, parent, false)
            return CamsViewHolder(view)
        }
    }


    private val name: TextView = view.findViewById(R.id.cam_name)
    private val address: TextView = view.findViewById(R.id.cam_ip_address)
    private val isDisabled: TextView = view.findViewById(R.id.cam_is_disabled)


    override fun bind(item: CameraEntity) {
        name.text = item.name
        address.text = address.context.getString(R.string.ip_and_port, item.ip, item.port)

        if (item.enabled) {

            name.setTextColor(name.context.getColor(R.color.black))
            address.setTextColor(address.context.getColor(R.color.black))
            isDisabled.visibility = View.GONE

        } else {

            name.setTextColor(name.context.getColor(R.color.grey))
            address.setTextColor(address.context.getColor(R.color.grey))
            isDisabled.visibility = View.VISIBLE

        }
    }
}