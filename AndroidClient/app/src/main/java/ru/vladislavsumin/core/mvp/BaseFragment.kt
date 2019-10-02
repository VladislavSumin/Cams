package ru.vladislavsumin.core.mvp

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import com.arellomobile.mvp.MvpAppCompatFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : MvpAppCompatFragment() {
    private val disposables = CompositeDisposable()

    @UiThread
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    @UiThread
    protected fun Disposable.autoDispose() {
        disposables.add(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupUx()
    }

    protected open fun setupUi() {}
    protected open fun setupUx() {}

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}