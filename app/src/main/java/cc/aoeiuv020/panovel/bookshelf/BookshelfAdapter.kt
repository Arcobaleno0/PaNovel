package cc.aoeiuv020.panovel.bookshelf

import android.content.Context
import android.view.ViewGroup
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.base.item.BaseItemListAdapter
import cc.aoeiuv020.panovel.base.item.DefaultItemViewHolder
import cc.aoeiuv020.panovel.detail.NovelDetailActivity
import cc.aoeiuv020.panovel.local.Bookshelf
import cc.aoeiuv020.panovel.local.Settings
import cc.aoeiuv020.panovel.local.Text
import cc.aoeiuv020.panovel.search.RefineSearchActivity
import cc.aoeiuv020.panovel.text.NovelTextActivity
import cc.aoeiuv020.panovel.util.hide
import cc.aoeiuv020.panovel.util.show
import cn.lemon.view.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.bookshelf_item_big.view.*
import org.jetbrains.anko.selector

/**
 *
 * Created by AoEiuV020 on 2017.10.14-21:54.
 */

class BookshelfItemListAdapter(context: Context, val presenter: BookshelfPresenter)
    : BaseItemListAdapter(context) {
    override fun onCreateBaseViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<NovelItem>
            = if (Settings.BookSmallLayout) {
        BookshelfItemViewHolder(presenter, context, parent, R.layout.bookshelf_item_small)
    } else {
        BookshelfItemViewHolder(presenter, context, parent, R.layout.bookshelf_item_big)
    }

}

class BookshelfItemViewHolder(itemListPresenter: BookshelfPresenter, ctx: Context, parent: ViewGroup?, layoutId: Int)
    : DefaultItemViewHolder<BookshelfItemPresenter>(itemListPresenter, ctx, parent, layoutId) {
    private val newChapterDot = itemView.newChapterDot
    private val progressBar = itemView.progressBar
    private val moreAction = itemView.lMoreAction

    init {
        moreAction.setOnClickListener {
            refresh()
        }

        moreAction.setOnLongClickListener {
            val list = listOf(R.string.read_continue to { readContinue() },
                    R.string.read_last_chapter to { readLastChapter() },
                    R.string.detail to { detail() },
                    R.string.export_exists_chapter_to_text_file to { exportExistsChapterToTextFile() },
                    R.string.refine_search to { refineSearch() },
                    R.string.refresh to { refresh() },
                    R.string.remove to { remove() })
            ctx.selector(ctx.getString(R.string.action), list.unzip().first.map { ctx.getString(it) }) { _, i ->
                list[i].second.invoke()
            }
            true
        }
    }

    override fun setData(data: NovelItem) {
        super.setData(data)
        newChapterDot.hide()
        progressBar.show()
    }

    override fun showChapter(chapters: List<NovelChapter>, progress: Int) {
        super.showChapter(chapters, progress)
        progressBar.hide()
        if (chapters.lastIndex > progress) {
            newChapterDot.show()
        }
    }

    private fun exportExistsChapterToTextFile() {
        Text.exportExistsChapterToTextFile(novelItem)
    }

    private fun remove() {
        Bookshelf.remove(novelItem)
        itemListPresenter.refresh()
    }

    private fun refresh() {
        setData(novelItem)
        presenter.forceRefresh(novelItem)
    }

    private fun detail() {
        NovelDetailActivity.start(ctx, novelItem)
    }

    private fun refineSearch() {
        RefineSearchActivity.start(ctx, novelItem.name, novelItem.author)
    }

    private fun readLastChapter() {
        NovelTextActivity.start(ctx, novelItem, -1)
    }

    private fun readContinue() {
        NovelTextActivity.start(ctx, novelItem)
    }
}
