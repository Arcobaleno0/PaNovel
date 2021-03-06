package cc.aoeiuv020.panovel.list

import cc.aoeiuv020.panovel.Presenter
import cc.aoeiuv020.panovel.api.NovelContext
import cc.aoeiuv020.panovel.api.NovelGenre
import cc.aoeiuv020.panovel.local.Cache
import cc.aoeiuv020.panovel.local.Selected
import cc.aoeiuv020.panovel.util.async
import io.reactivex.Observable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error

/**
 *
 * Created by AoEiuV020 on 2017.10.03-16:12:55.
 */
class NovelListPresenter : Presenter<NovelListFragment>(), AnkoLogger {
    private lateinit var context: NovelContext
    private lateinit var genre: NovelGenre

    fun requestNovelList(genre: NovelGenre) {
        saveGenre(genre)
        this.genre = genre
        Observable.fromCallable {
            NovelContext.getNovelContextByUrl(genre.requester.url).also { context = it }
                    .getNovelList(genre.requester).also { it.forEach { Cache.item.put(it.novel, it.novel) } }
        }.async().subscribe({ comicList ->
            view?.showNovelList(comicList)
        }, { e ->
            val message = "加载小说列表失败，"
            error(message, e)
            view?.showError(message, e)
        }).let { addDisposable(it, 0) }
    }

    private fun saveGenre(genre: NovelGenre) {
        Selected.genre = genre
    }

    fun loadNextPage() {
        debug { "加载下一页，${context.getNovelSite().name}: ${genre.name}" }
        Observable.create<NovelGenre> { em ->
            context.getNextPage(genre)?.let { em.onNext(it) }
            em.onComplete()
        }.async().toList().subscribe({ genres ->
            if (genres.isEmpty()) {
                debug { "没有下一页" }
                view?.showNoMore()
                return@subscribe
            }
            genre = genres.first()
            saveGenre(genre)
            view?.showUrl(genre.requester.url)
            Observable.fromCallable {
                context.getNovelList(genre.requester)
            }.async().subscribe({ comicList ->
                debug { "展示小说列表，数量：${comicList.size}" }
                view?.addNovelList(comicList)
            }, { e ->
                val message = "加载下一页小说列表失败，"
                error(message, e)
                view?.showError(message, e)
            })
        }, { e ->
            val message = "加载小说列表一下页地址失败，"
            error(message, e)
            view?.showError(message, e)
        }).let { addDisposable(it, 1) }
    }
}