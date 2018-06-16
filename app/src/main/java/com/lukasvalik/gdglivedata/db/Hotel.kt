package com.lukasvalik.gdglivedata.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Hotel(@PrimaryKey val name: String, val url: String, var seen: Boolean)