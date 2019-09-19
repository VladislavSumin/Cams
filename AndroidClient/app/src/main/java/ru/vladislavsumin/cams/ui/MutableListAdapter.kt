package ru.vladislavsumin.cams.ui

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import ru.vladislavsumin.core.ui.recyclerview.RWBaseAdapter
import ru.vladislavsumin.core.ui.recyclerview.RWBaseViewHolder

/**
 * @param Item - item type
 * @param ViewHolder - ViewHolder type
 */
abstract class MutableListAdapter<Item : Any, ViewHolder : RWBaseViewHolder<Item>>(
    viewHolderFactory: RWBaseViewHolder.ViewHolderFactory<ViewHolder>
) : RWBaseAdapter<Item, ViewHolder>(viewHolderFactory) {

    var items: MutableList<Item> = mutableListOf()
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

    /**
     * Add element to end of list
     *
     * @return index of added element
     */
    @UiThread
    fun addItem(item: Item): Int {
        items.add(item)
        val index = items.size
        notifyItemInserted(index)
        return index
    }

    @UiThread
    fun updateItemBy(item: Item, predicate: (Item) -> Boolean): Boolean {
        val index = items.indexOfFirst(predicate)
        if (index < 0) return false
        items[index] = item
        notifyItemChanged(index)
        return true
    }

    @UiThread
    fun removeItemBy(predicate: (Item) -> Boolean): Boolean {
        val index = items.indexOfFirst(predicate)
        if (index < 0) return false
        items.removeAt(index)
        notifyItemRemoved(index)
        return true
    }
}