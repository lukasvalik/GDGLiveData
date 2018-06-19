package com.lukasvalik.gdglivedata

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

class BindingAdapters {

    companion object {

        @JvmStatic @BindingAdapter("url")
        fun setImageByUrl(view: ImageView, url: String?) =
                url?.let { Glide.with(view.context).load(it).into(view) }
    }
}