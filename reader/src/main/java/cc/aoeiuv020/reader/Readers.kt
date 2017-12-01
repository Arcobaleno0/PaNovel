package cc.aoeiuv020.reader

import android.content.Context
import android.view.ViewGroup
import cc.aoeiuv020.reader.simple.SimpleConfig
import cc.aoeiuv020.reader.simple.SimpleReader

/**
 *
 * Created by AoEiuV020 on 2017.12.01-01:16:49.
 */
object Readers {
    fun getSimpleReader(ctx: Context, novel: Novel, parent: ViewGroup, requester: TextRequester, config: SimpleConfig)
            : INovelReader
            = SimpleReader(ctx, novel, parent, requester, config)
}
