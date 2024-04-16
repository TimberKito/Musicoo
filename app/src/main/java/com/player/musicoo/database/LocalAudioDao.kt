

package com.player.musicoo.database

import androidx.room.*
import com.player.musicoo.bean.Audio

@Dao
interface LocalAudioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioFile(barcode: Audio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioFiles(audios: List<Audio>)

    @Query("SELECT * FROM Audio")
    suspend fun getAllAudioFile(): List<Audio>

    @Delete
    suspend fun deleteAudioFile(barcode: Audio)

    @Query("DELETE FROM Audio")
    suspend fun deleteAllAudioFile()

    @Update
    suspend fun updateAudioFile(audioFile: Audio)

    @Query("SELECT * FROM Audio WHERE name = :path LIMIT 1")
    suspend fun getAudioFileByPath(path: String): Audio?

    @Query("SELECT * FROM Audio WHERE selected = 1")
    suspend fun getAudioBySelected(): List<Audio>
}
