package ru.vladislavsumin.cams.ui

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import ru.vladislavsumin.core.ui.recyclerview.RWBaseAdapter
import ru.vladislavsumin.core.ui.recyclerview.RWBaseViewHolder

/**
 * @param Item - item type
 * @param ViewHolder - ViewHolder type
 */
abstract class ListAdapter<Item : Any, ViewHolder : RWBaseViewHolder<Item>>(
        viewHolderFactory: RWBaseViewHolder.ViewHolderFactory<ViewHolder>
) : RWBaseAdapter<Item, ViewHolder>(viewHolderFactory) {

    var items: List<Item> = listOf()
        @UiThread
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    final override fun getItemCount(): Int {
        return items.size
    }

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}