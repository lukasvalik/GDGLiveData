package com.lukasvalik.gdglivedata

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

class BindingAdapters {

    companion object {

        @JvmStatic @BindingAdapter("url")
        fun setImageByUrl(view: ImageView, url: String?) =
                url?.let { Glide.with(view.context).load(it).into(view) }

        @JvmStatic @BindingAdapter("android:visibility")
        fun setViewVisibility(view: View, visible: Boolean) {
            view.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }
}