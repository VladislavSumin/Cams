package ru.vladislavsumin.core.utils

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RxListFilter<T>(
        private val parentList: Observable<List<T>>,
        vararg filters: Observable<(T) -> Boolean>
) {
    private val filterChain: List<Observable<(T) -> Boolean>> = listOf(*filters)

    fun observeFiltered(): Observable<List<T>> {
        return Observable
                .combineLatest(filterChain) {
                    @Suppress("UNCHECKED_CAST")
                    listOf(it) as List<(T) -> Boolean>
                }
                .observeOn(Schedulers.computation())
                .map<(T) -> Boolean> { filterChain ->
                    filter@{ item: T ->
                        filterChain.forEach {
                            if (!it(item)) return@filter false;
                        }
                        return@filter true
                    }
                }
                .switchMap { filter ->
                    parentList.map { it.filter(filter) }
                }
    }
}