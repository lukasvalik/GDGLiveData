package com.miroslavkacera.rxapp.utils

import android.content.Context
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.util.Log
import java.util.*

open class FormattedString private constructor(@StringRes stringResource: Int = 0, vararg textData: Any) {

    companion object {

        @JvmStatic
        fun empty(): FormattedString {
            return FormattedString(0)
        }

        @JvmStatic
        fun from(@StringRes resId: Int): FormattedString {
            return FormattedString(resId)
        }

        @JvmStatic
        fun from(text: CharSequence): FormattedString {
            return FormattedString(0, text)
        }

        @JvmStatic
        fun from(@StringRes resId: Int, vararg textData: Any): FormattedString {
            return FormattedString(resId, *textData)
        }
    }

    @StringRes
    @get:StringRes
    var stringResource = 0
        private set

    @get:VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var stringData: Array<out Any>

    init {
        this.stringResource = stringResource
        this.stringData = textData
    }

    fun hasStringData() = !stringData.isEmpty()

    fun set(@StringRes stringResource: Int, vararg textData: Any): FormattedString {
        this.stringResource = stringResource
        stringData = textData
        return this
    }

    fun setData(vararg textData: Any): FormattedString {
        stringData = textData
        return this
    }

    open fun getFormattedString(context: Context): String {
        val text = if (stringResource == 0) "" else context.getString(stringResource)
        stringData.takeIf { it.isNotEmpty() }?.let {
            try {
                val list = arrayListOf<Any>()
                it.mapTo(list) { (it as? FormattedString)?.getFormattedString(context) ?: it }

                return String.format(if (text.isEmpty()) "%s" else text, *list.toArray())
            } catch (illegalFormatException: IllegalFormatConversionException) {
                Log.e("FormattedString", "Wrong text format: $text")
            } catch (missingArgumentException: MissingFormatArgumentException) {
                Log.e("FormattedString", "Missing arguments for text format: $text")
            }
        }

        return text
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FormattedString

        if (stringResource != other.stringResource) return false
        if (!Arrays.equals(stringData, other.stringData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stringResource
        result = 31 * result + Arrays.hashCode(stringData)
        return result
    }
}