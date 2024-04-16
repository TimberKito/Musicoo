package com.player.musicoo.database

import android.content.Context
import androidx.room.Room
import com.player.musicoo.bean.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseManager private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "local_audio_viewer_database"
    ).build()

    private val audioFileDao = database.localAudioDao()

    suspend fun insertAudioFile(audio: Audio) {
        withContext(Dispatchers.IO) {
            val existingAudioFile = getAudioFileByPath(audio.name)
            if (existingAudioFile == null) {
                audioFileDao.insertAudioFile(audio)
            } else {
                audioFileDao.updateAudioFile(audio)
            }
        }
    }

    suspend fun insertAudioFiles(audios: List<Audio>) {
        withContext(Dispatchers.IO) {
            for (audio in audios) {
                val existingAudioFile = getAudioFileByPath(audio.file)
                if (existingAudioFile == null) {
                    audioFileDao.insertAudioFile(audio)
                } else {
                    audioFileDao.updateAudioFile(audio)
                }
            }
        }
    }

    suspend fun getAllAudioFiles(): List<Audio> {
        return withContext(Dispatchers.IO) {
            audioFileDao.getAllAudioFile()
        }
    }

    suspend fun deleteAudioFile(audioFile: Audio) {
        withContext(Dispatchers.IO) {
            audioFileDao.deleteAudioFile(audioFile)
        }
    }

    suspend fun deleteAllAudioFiles() {
        withContext(Dispatchers.IO) {
            audioFileDao.deleteAllAudioFile()
        }
    }

    suspend fun updateAudioFiles(audioFile: Audio) {
        withContext(Dispatchers.IO) {
            audioFileDao.updateAudioFile(audioFile)
        }
    }

    suspend fun getAudioFileByPath(path: String): Audio? {
        return audioFileDao.getAudioFileByPath(path)
    }

    suspend fun getAudioBySelect(): List<Audio> {
        return audioFileDao.getAudioBySelected()
    }

    companion object {
        @Volatile
        private var instance: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager {
            return instance ?: synchronized(this) {
                instance ?: DatabaseManager(context).also { instance = it }
            }
        }
    }
}
