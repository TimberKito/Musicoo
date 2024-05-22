package com.player.musicoo.database

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.player.musicoo.App
import com.player.musicoo.bean.Audio
import kotlinx.coroutines.launch

class CollectViewModel : ViewModel() {
    private var likeData: MutableLiveData<List<Audio>> = MutableLiveData<List<Audio>>()

    private val database = Room.databaseBuilder(
        App.appContext, AppDatabase::class.java, "local_audio_viewer_database"
    ).build()

    private val audioFileDao = database.localAudioDao()

    init {
        viewModelScope.launch {
            likeData.value = audioFileDao.getCollectData()
        }
    }

    fun update() {
        viewModelScope.launch {
            likeData.value = audioFileDao.getCollectData()
        }
    }

    fun getList() = likeData

    override fun onCleared() {
        super.onCleared()
    }

}