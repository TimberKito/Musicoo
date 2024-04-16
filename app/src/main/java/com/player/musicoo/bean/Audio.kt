package com.player.musicoo.bean

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity
data class Audio(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "file") var file: String,
    @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "duration") var duration: Long,
    @ColumnInfo(name = "selected") var selected: Boolean
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}