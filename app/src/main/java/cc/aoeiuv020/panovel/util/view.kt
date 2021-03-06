@file:Suppress("DEPRECATION", "unused")

package cc.aoeiuv020.panovel.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.BaseBundle
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cc.aoeiuv020.panovel.R
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder


/**
 *
 * Created by AoEiuV020 on 2017.10.02-21:50:34.
 */
fun Context.loading(dialog: ProgressDialog, id: Int) = loading(dialog, getString(R.string.loading, getString(id)))

fun Context.loading(dialog: ProgressDialog, str: String) = dialog.apply {
    setMessage(str)
    show()
}

fun Context.alertError(dialog: AlertDialog, str: String, e: Throwable) = alert(dialog, str + "\n" + e.message)
fun Context.alert(dialog: AlertDialog, messageId: Int) = alert(dialog, getString(messageId))
fun Context.alert(dialog: AlertDialog, messageId: Int, titleId: Int) = alert(dialog, getString(messageId), getString(titleId))
fun Context.alert(dialog: AlertDialog, message: String, title: String? = null) = dialog.apply {
    setMessage(message)
    title?.let {
        setTitle(title)
    }
    show()
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.setHeight(height: Int) {
    layoutParams = layoutParams.also { it.height = height }
}

fun Context.alertColorPicker(initial: Int, callback: (color: Int) -> Unit) = ColorPickerDialogBuilder.with(this)
        .initialColor(initial)
        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
        .setOnColorChangedListener(callback)
        .setPositiveButton(R.string.select) { _, color, _ -> callback(color) }
        .setNegativeButton(R.string.cancel) { _, _ -> callback(initial) }
        .build().show()

fun Context.notify(id: Int, text: String? = null, title: String? = null, icon: Int = R.mipmap.ic_launcher_foreground) {
    val channelId = "channel_default"
    val name = "default"
    val nb = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
    nb.apply {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setLargeIcon(getBitmapFromVectorDrawable(icon))
            setSmallIcon(R.mipmap.ic_launcher_round)
        } else {
            setSmallIcon(icon)
        }
    }
    val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            manager.createNotificationChannel(channel)
        }
    }
    manager.notify(id, nb.build())
}

/**
 * https://stackoverflow.com/a/38244327/5615186
 */
fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(this, drawableId)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable).mutate()
    }

    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun EditText.showKeyboard() {
    if (hasFocus()) {
        clearFocus()
    }
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

@Suppress("UNCHECKED_CAST")
fun Bundle.toMap(): Map<String, Any?>
        = BaseBundle::class.java.getDeclaredField("mMap").apply { isAccessible = true }
        .get(this) as Map<String, *>