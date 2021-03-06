package com.miroslavkacera.rxapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Hotel(@PrimaryKey val name: String,
                 val description: String,
                 val url: String,
                 val price: Int,
                 var seen: Boolean)