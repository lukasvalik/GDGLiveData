package com.lukasvalik.gdglivedata.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Hotel(@PrimaryKey val name: String, val url: String, var seen: Boolean)