package com.player.musicoo.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_playing_audio")
data class CurrentPlayingAudio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "file") val file: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "duration") var duration: Long,
    @ColumnInfo(name = "selected") val selected: Boolean
)
