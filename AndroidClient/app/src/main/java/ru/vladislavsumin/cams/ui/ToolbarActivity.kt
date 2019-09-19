package ru.vladislavsumin.cams.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.core.mvp.BaseActivity


abstract class ToolbarActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar

    companion object {
        private const val TOOLBAR = R.id.toolbar

        fun tintMenuItemIcon(color: Int, item: MenuItem?) {

            if (item == null) return

            val drawable = item.icon
            if (drawable != null) {
                val wrapped = DrawableCompat.wrap(drawable)
                drawable.mutate()
                DrawableCompat.setTint(wrapped, color)
                item.icon = drawable
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        toolbar = findViewById<Toolbar>(TOOLBAR)
            ?: throw NoSuchElementException("Check include toolbar layout with id layout")

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    protected fun setToolbarVisibility(isVisible: Boolean) {
        toolbar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}