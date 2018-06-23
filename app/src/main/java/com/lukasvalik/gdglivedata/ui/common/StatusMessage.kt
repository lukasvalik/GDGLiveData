package com.lukasvalik.gdglivedata.ui.common

import android.content.Context
import android.databinding.BindingAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.lukasvalik.gdglivedata.R
import com.lukasvalik.gdglivedata.vo.Status

class StatusMessage(val status: Status,
                    private val errorMessage: String?,
                    private val costMin: Int?,
                    private val costMax: Int?) {

    fun getMessage(context: Context) : String? {
        return when (status) {
            Status.SUCCESS -> String.format(context.getString(R.string.message_success), costMin, costMax)
            Status.ERROR -> errorMessage
            Status.LOADING -> null
        }
    }

    companion object {
        @JvmStatic @BindingAdapter("statusVisibility")
        fun setStatusVisibility(view: View, statusMessage: StatusMessage) {
            view.visibility = if (statusMessage.status != Status.LOADING) View.VISIBLE else View.GONE
        }

        @JvmStatic @BindingAdapter("statusText")
        fun setStatusText(textView: AppCompatTextView, statusMessage: StatusMessage) {
            textView.text = statusMessage.getMessage(textView.context)
        }

        @JvmStatic @BindingAdapter("statusColor")
        fun setStatusColor(view: View, statusMessage: StatusMessage) {
            view.setBackgroundColor(
                    if (statusMessage.status == Status.SUCCESS)
                        ContextCompat.getColor(view.context, R.color.success)
                    else
                        ContextCompat.getColor(view.context, R.color.error))
        }
    }
}