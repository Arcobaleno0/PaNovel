package cc.aoeiuv020.panovel.local

import cc.aoeiuv020.panovel.api.NovelDetail
import cc.aoeiuv020.panovel.api.NovelItem


/**
 * 书架，
 * Created by AoEiuV020 on 2017.10.04-19:23:01.
 */

@Suppress("MemberVisibilityCanPrivate")
object Bookshelf : LocalSource {
    fun contains(novelItem: NovelItem): Boolean = gsonExists(novelItem.bookId.toString())

    fun add(novelDetail: NovelDetail) = add(novelDetail.novel)
    fun add(novelItem: NovelItem) = gsonSave(novelItem.bookId.toString(), novelItem)

    fun remove(novelDetail: NovelDetail) = remove(novelDetail.novel)
    fun remove(novelItem: NovelItem) = gsonRemove(novelItem.bookId.toString())

    fun list(): List<NovelItem> = gsonList()
}
