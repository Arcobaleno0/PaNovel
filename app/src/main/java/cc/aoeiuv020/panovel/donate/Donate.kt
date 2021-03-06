package cc.aoeiuv020.panovel.donate

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import cc.aoeiuv020.panovel.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast


/**
 *
 * Created by AoEiuV020 on 2017.11.25-12:55:16.
 */
sealed class Donate {
    companion object {
        val paypal = Paypal()
        val alipay = Alipay()
        val weChatPay = WeChatPay()
    }

    abstract fun start(context: Context)

    class Paypal : Donate() {
        companion object {
            private val name = "AoEiuV020"
        }

        override fun start(context: Context) {
            context.browse("https://www.paypal.me/$name")
        }
    }

    /**
     * https://github.com/didikee/AndroidDonate/blob/master/donate/src/main/java/android/didikee/donate/AlipayDonate.java
     */
    class Alipay : Donate() {
        companion object {
            private val payCode = "FKX01135QSJ7NYBPR0PK01"
        }

        override fun start(context: Context) {
            context.browse("https://QR.ALIPAY.COM/$payCode")
        }
    }

    /**
     * https://github.com/didikee/AndroidDonate/blob/master/donate/src/main/java/android/didikee/donate/WeiXinDonate.java
     */
    class WeChatPay : Donate() {
        companion object {
            private val qrcodeId = R.mipmap.qrcode_wechatpay
            private val TENCENT_PACKAGE_NAME = "com.tencent.mm"
            private val TENCENT_ACTIVITY_BIZSHORTCUT = "com.tencent.mm.action.BIZSHORTCUT"
            private val TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT = "LauncherUI.From.Scaner.Shortcut"
        }

        override fun start(context: Context) {
            context.alert {
                val ivQR = ImageView(context)
                ivQR.setImageResource(qrcodeId)
                customView = ivQR
                positiveButton(R.string.jump_to_we_chat) {
                    val intent = Intent(TENCENT_ACTIVITY_BIZSHORTCUT)
                    intent.`package` = TENCENT_PACKAGE_NAME
                    intent.putExtra(TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        context.toast("你好像没有安装微信")
                    }
                }
            }.show()
        }
    }

}