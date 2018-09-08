package com.miroslavkacera.rxapp.utils

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class BindingAdapters {

    companion object {

        @JvmStatic
        @BindingAdapter("url")
        fun setImageByUrl(view: ImageView, url: String?) =
                url?.let { Glide.with(view.context).load(it).into(view) }

        @JvmStatic
        @BindingAdapter("backgroundColorResource")
        fun setText(view: View, @ColorRes color: Int) =
                view.setBackgroundColor(ContextCompat.getColor(view.context, color))

        @JvmStatic
        @BindingAdapter("android:visibility")
        fun setViewVisibility(view: View, visible: Boolean) {
            view.visibility = if (visible) View.VISIBLE else View.GONE
        }

        @InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
        @JvmStatic
        fun isRefreshing(view: SwipeRefreshLayout) = view.isRefreshing

        @BindingAdapter("refreshingAttrChanged")
        @JvmStatic
        fun setListener(view: SwipeRefreshLayout, attrChange: InverseBindingListener) =
                view.setOnRefreshListener { attrChange.onChange() }

        @JvmStatic
        @BindingAdapter("refreshSensitivity")
        fun setSwipeRefreshSensibility(view: SwipeRefreshLayout, sensitivityLevel: Float) {
            val viewTreeObserver = view.viewTreeObserver
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)

                    val distance = view.height / Math.max(1.0f, Math.min(10.0f, sensitivityLevel))
                    view.setDistanceToTriggerSync(distance.toInt())
                    return true
                }
            })
        }

        @JvmStatic
        @BindingAdapter("android:text")
        fun setText(view: TextView, string: FormattedString?) {
            view.text = string?.getFormattedString(view.context) ?: ""
        }
    }
}