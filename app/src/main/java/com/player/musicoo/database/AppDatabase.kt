
package com.player.musicoo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.player.musicoo.bean.Audio

@Database(entities = [Audio::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localAudioDao(): LocalAudioDao
}