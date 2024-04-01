package com.hisense.einkservice.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eink_apps")
data class EinkApp(
    @PrimaryKey val packageName: String,
    var preferredSpeed: Int
)