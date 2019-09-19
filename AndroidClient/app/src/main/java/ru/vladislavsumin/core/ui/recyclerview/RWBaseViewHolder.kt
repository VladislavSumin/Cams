package ru.vladislavsumin.core.ui.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @param T - item type
 */
abstract class RWBaseViewHolder<T : Any> protected constructor(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)

    /**
     * Create ViewHolder
     *
     * @param T - ViewHolder
     */
    interface ViewHolderFactory<T : RWBaseViewHolder<*>> {
        fun getInstance(parent: ViewGroup): T
    }
}