package com.miroslavkacera.rxapp.model

data class Hotel(val name: String,
                 val description: String,
                 val url: String,
                 val price: Int,
                 var seen: Boolean)