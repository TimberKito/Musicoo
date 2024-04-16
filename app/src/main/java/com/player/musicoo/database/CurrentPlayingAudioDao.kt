package com.player.musicoo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.player.musicoo.bean.CurrentPlayingAudio

@Dao
interface CurrentPlayingAudioDao {
    @Query("SELECT * FROM current_playing_audio LIMIT 1")
    suspend fun getCurrentPlayingAudio(): CurrentPlayingAudio?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentPlayingAudio(audio: CurrentPlayingAudio)

    @Update
    suspend fun updateCurrentPlayingAudio(audio: CurrentPlayingAudio)

    @Delete
    suspend fun deleteCurrentPlayingAudio(audio: CurrentPlayingAudio)
}
