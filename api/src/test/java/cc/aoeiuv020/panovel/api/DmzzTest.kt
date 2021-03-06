package cc.aoeiuv020.panovel.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 *
 * Created by AoEiuV020 on 2017.11.30-18:14:03.
 */
class DmzzTest {
    init {
        System.setProperty("org.slf4j.simpleLogger.log.Dmzz", "trace")
    }

    private lateinit var context: Dmzz
    @Before
    fun setUp() {
        context = Dmzz()
    }

    @Test
    fun getGenres() {
        val genres = context.getGenres()
        genres[0].let {
            assertEquals("恐怖", it.name)
            assertEquals("http://q.dmzj.com/tags/kongbu.shtml", it.requester.url)
        }
        genres[genres.lastIndex].let {
            assertEquals("奇幻", it.name)
            assertEquals("http://q.dmzj.com/tags/qihuan.shtml", it.requester.url)
        }
    }

    @Test
    fun getNovelList() {
        context.getNovelList(GenreListRequester("http://q.dmzj.com/tags/qihuan.shtml")).let {
            it.forEach { novelItem ->
                println(novelItem)
            }
        }
    }

    @Test
    fun searchNovelName() {
        context.getNovelList(context.searchNovelName("打工吧").requester).let {
            it.forEach { novelItem ->
                println(novelItem)
            }
            assertTrue(it.any { novelItem ->
                novelItem.novel.name == "打工吧魔王大人"
            })
        }
    }

    @Test
    fun searchNovelAuthor() {
        context.getNovelList(context.searchNovelAuthor("和原聪司").requester).let {
            it.forEach { novelItem ->
                println(novelItem)
            }
            assertTrue(it.any { novelItem ->
                novelItem.novel.name == "打工吧魔王大人"
            })
        }
    }

    @Test
    fun getNovelDetail() {
        context.getNovelDetail(DetailRequester("http://q.dmzj.com/2137/index.shtml")).let {
            assertEquals("GAMERS！电玩咖！", it.novel.name)
            assertEquals("葵せきな(葵关南)", it.novel.author)
            assertEquals("", it.introduction)
            assertEquals("http://xs.dmzj.com/img/webpic/4/games56989l.jpg", it.bigImg)
            println(it.update)
        }
    }

    @Test
    fun getNovelChaptersAsc() {
        context.getNovelChaptersAsc(ChaptersRequester("http://q.dmzj.com/2137/index.shtml")).let { list ->
            list.forEach {
                println(it)
            }
            assertEquals("转载信息", list.first().name)
        }
    }

    @Test
    fun regex() {
        val regex = Regex("chapter_list\\[\\d*\\]\\[\\d*\\] = '<a href=\"([^\"]*)\".*>(\\S*)</a>'.*;")
        val str = """chapter_list[0][7] = '<a href="/2137/7832/61328.shtml" alt="后记" title="后记">后记</a>';"""
        assertTrue(str.matches(regex))
    }

    @Test
    fun getNovelText() {
        context.getNovelText(TextRequester("http://q.dmzj.com/2137/8856/78730.shtml")).textList.let {
            assertEquals("「等、等很久了吗？」", it.first())
            assertEquals("——我们头顶上那片照耀着北方大陆的璀璨星空，今天异常闪耀。", it.last())
            assertEquals(629, it.size)
            it.forEach {
                println(it)
            }
        }
    }
}