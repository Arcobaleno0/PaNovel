package cc.aoeiuv020.pager.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import cc.aoeiuv020.pager.PagerDrawer
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit

/**
 *
 * Created by AoEiuV020 on 2017.12.02-17:58:54.
 */
class NumberPagerDrawer(context: Context) : PagerDrawer(), AnkoLogger {
    private var n = 0
    private var m = 0
    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val textPaint = Paint().apply {
        color = 0xffff0000.toInt()
        textSize = context.sp(70).toFloat()
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.apply {
            drawColor(0xffffffff.toInt())
            drawRect(Rect(0, 0, width, height), strokePaint.apply {
                color = 0xff0000ff.toInt()
            })
            drawCircle(width.toFloat() / 2, height.toFloat() / 2, minOf(width, height).toFloat() / 5, strokePaint.apply {
                color = 0xff00ff00.toInt()
            })
        }
    }

    private fun drawNumber(canvas: Canvas) {
        info { "drawNumber <$n, $m>" }
        canvas.apply {
            drawRect(Rect(0, 0, width, height), strokePaint.apply {
                color = 0xffff0000.toInt()
            })
            drawText(n.toString(), width.toFloat() / 2, height.toFloat() / 2, textPaint)
            drawText(m.toString(), width.toFloat() / 2, height.toFloat() / 2 + textPaint.textSize, textPaint)
            doAsync {
                TimeUnit.SECONDS.sleep(1)
                m = n * n
                uiThread {
                    info { "refresh <$n, $m>" }
                    pager?.refresh()
                }
            }
        }
    }

    override fun drawCurrentPage(background: Canvas, content: Canvas) {
        debug { "drawCurrentPage $n" }
        drawBackground(background)
        drawNumber(content)
    }

    override fun scrollToPrev(): Boolean {
        debug { "scrollToPrev $n" }
        if (n < -5) return false
        n--
        return true
    }

    override fun scrollToNext(): Boolean {
        debug { "scrollToNext $n" }
        if (n > 5) return false
        n++
        return true
    }
}