package cc.aoeiuv020.panovel.settings

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.pick
import kotlinx.android.synthetic.main.content_about.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.browse
import org.jetbrains.anko.yesButton
import java.util.regex.Pattern

/**
 *
 * Created by AoEiuV020 on 2017.11.23-10:26:44.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.content_about, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        tvVersion.text = activity.run {
            packageManager.getPackageInfo(packageName, 0).versionName
        }
        tvChangeLog.text = activity.assets.open("ChangeLog.txt").reader().readText()

        tvLicenses.setOnClickListener {
            // [jsoup](https://github.com/jhy/jsoup)
            val pattern = Pattern.compile("\\[(\\S*)\\]\\((\\S*)\\)")
            val (nameList, linkList) = activity.assets.open("Licenses.txt").reader().readLines()
                    .mapNotNull {
                        try {
                            val (name, link) = it.pick(pattern)
                            println("$name, $link")
                            Pair(name, link)
                        } catch (_: Exception) {
                            null
                        }
                    }.unzip()
            println("$nameList, $linkList")
            activity.alert {
                title = activity.getString(R.string.library)
                items(nameList) { _, i ->
                    activity.browse(linkList[i])
                }
                yesButton { it.dismiss() }
            }.show()
        }
    }
}