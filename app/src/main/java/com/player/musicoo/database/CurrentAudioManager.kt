package com.player.musicoo.database

import android.content.Context
import androidx.room.Room
import com.player.musicoo.bean.CurrentPlayingAudio

class CurrentAudioManager private constructor(context: Context) {
    private val database: CurrentAudioDatabase = Room.databaseBuilder(
        context.applicationContext,
        CurrentAudioDatabase::class.java, "current_audio_app_database"
    ).build()

    private val currentPlayingAudioDao: CurrentPlayingAudioDao = database.currentPlayingAudioDao()

    suspend fun getCurrentPlayingAudio(): CurrentPlayingAudio? {
        return currentPlayingAudioDao.getCurrentPlayingAudio()
    }

    suspend fun setCurrentPlayingAudio(audio: CurrentPlayingAudio) {
        val currentAudio = getCurrentPlayingAudio()
        if (currentAudio == null) {
            currentPlayingAudioDao.insertCurrentPlayingAudio(audio)
        } else {
            // 如果已有数据，先删除现有数据，然后再插入新数据
            currentPlayingAudioDao.deleteCurrentPlayingAudio(currentAudio)
            currentPlayingAudioDao.insertCurrentPlayingAudio(audio)
        }
    }

    companion object {
        @Volatile private var instance: CurrentAudioManager? = null

        fun getInstance(context: Context): CurrentAudioManager {
            return instance ?: synchronized(this) {
                instance ?: CurrentAudioManager(context).also { instance = it }
            }
        }
    }
}