
package com.player.musicoo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.player.musicoo.bean.CurrentPlayingAudio

@Database(entities = [CurrentPlayingAudio::class], version = 1, exportSchema = false)
abstract class CurrentAudioDatabase : RoomDatabase() {
    abstract fun currentPlayingAudioDao(): CurrentPlayingAudioDao
}