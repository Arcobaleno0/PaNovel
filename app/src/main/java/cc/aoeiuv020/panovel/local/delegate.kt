package cc.aoeiuv020.panovel.local

import android.net.Uri
import cc.aoeiuv020.panovel.App
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import java.io.Serializable
import kotlin.reflect.KProperty

/**
 *
 * Created by AoEiuV020 on 2017.10.07-22:48:22.
 */

/**
 * 一个Delegate只用在一个字段，
 * 只用在原始类型，
 * 不要用在自定义的Serializable类，
 * 非空，
 */
class PrimitiveDelegate<T : Serializable>(private val default: T) : AnkoLogger {
    private var backingField: T? = null
    operator fun getValue(thisRef: LocalSource, property: KProperty<*>): T {
        // 优先返回幕后字段，为空则读取，读出空则替换成默认，读出的设到幕后字段，
        // 线程不安全，同时多次get可能多次读取，
        return (backingField ?: (thisRef.primitiveLoad(property.name) ?: default).also { backingField = it }).also {
            debug { "${property.name} > $it" }
        }
    }

    operator fun setValue(thisRef: LocalSource, property: KProperty<*>, value: T) {
        debug { "${property.name} < $value" }
        if (backingField != value) {
            backingField = value
            thisRef.primitiveSave(property.name, value)
        }
    }
}

/**
 * 只用在原始类型，
 * 不要用在自定义的Serializable类，
 * 可空，
 */
@Suppress("unused")
class NullablePrimitiveDelegate<T : Serializable>(private val default: T? = null) {
    private var backingField: T? = null
    operator fun getValue(thisRef: LocalSource, property: KProperty<*>): T? {
        return backingField ?: (thisRef.primitiveLoad(property.name) ?: default)?.also { backingField = it }
    }

    operator fun setValue(thisRef: LocalSource, property: KProperty<*>, value: T?) {
        backingField = value
        thisRef.primitiveSave(property.name, value)
    }
}

/**
 * 用gson序列化对象储存json字符串，
 * 非空，
 */
class GsonDelegate<T>(private val default: T, private val type: Class<T>) {
    companion object {
        inline fun <reified T> new(default: T) = GsonDelegate(default, T::class.java)
    }

    operator fun getValue(thisRef: LocalSource, property: KProperty<*>): T {
        return thisRef.gsonLoad(property.name, type) ?: default
    }

    operator fun setValue(thisRef: LocalSource, property: KProperty<*>, value: T) {
        thisRef.gsonSave(property.name, value)
    }
}

/**
 * 用gson序列化对象储存json字符串，
 * 可空，
 */
class NullableGsonDelegate<T>(private val default: T? = null, private val type: Class<T>) {
    companion object {
        inline fun <reified T> new(default: T? = null) = NullableGsonDelegate(default, T::class.java)
    }

    operator fun getValue(thisRef: LocalSource, property: KProperty<*>): T? {
        return thisRef.gsonLoad(property.name, type) ?: default
    }

    operator fun setValue(thisRef: LocalSource, property: KProperty<*>, value: T?) {
        thisRef.gsonSave(property.name, value)
    }
}

class UriDelegate : AnkoLogger {
    private var backingField: Uri? = null
    operator fun getValue(thisRef: LocalSource, property: KProperty<*>): Uri? {
        return backingField ?: thisRef.openFile(property.name).takeIf { it.exists() }?.let { Uri.fromFile(it) }.also {
            debug { "${property.name} > $it" }
        }
    }

    operator fun setValue(thisRef: LocalSource, property: KProperty<*>, value: Uri?) {
        debug { "${property.name} < $value" }
        if (backingField != value) {
            backingField = null
            val file = thisRef.openFile(property.name)
            if (value == null) {
                file.delete()
            } else {
                App.ctx.contentResolver.openInputStream(value).copyTo(file.outputStream())
                backingField = getValue(thisRef, property)
            }
        }
    }
}

