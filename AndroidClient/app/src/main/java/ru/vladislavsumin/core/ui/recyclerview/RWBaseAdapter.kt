package ru.vladislavsumin.core.ui.recyclerview

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

/**
 * @param Item - item type
 * @param ViewHolder - ViewHolder type
 */
abstract class RWBaseAdapter<Item : Any, ViewHolder : RWBaseViewHolder<Item>>(
    private val viewHolderFactory: RWBaseViewHolder.ViewHolderFactory<ViewHolder>
) : RecyclerView.Adapter<ViewHolder>() {

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return viewHolderFactory.getInstance(parent)
    }
}