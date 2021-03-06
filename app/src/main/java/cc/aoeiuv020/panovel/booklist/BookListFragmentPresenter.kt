package cc.aoeiuv020.panovel.booklist

import cc.aoeiuv020.panovel.Presenter
import cc.aoeiuv020.panovel.local.BookList
import cc.aoeiuv020.panovel.util.async
import io.reactivex.Observable
import org.jetbrains.anko.error

/**
 *
 * Created by AoEiuV020 on 2017.11.22-14:31:17.
 */
class BookListFragmentPresenter : Presenter<BookListFragment>() {
    fun refresh() {
        requestBookListList()
    }

    private fun requestBookListList() {
        Observable.fromCallable {
            BookList.list()
        }.async().subscribe({ list ->
            view?.showBookListList(list)
        }, { e ->
            val message = "获取历史列表失败，"
            error(message, e)
            view?.showError(message, e)
        }).let { addDisposable(it) }

    }

}