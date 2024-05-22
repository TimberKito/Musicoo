package com.player.musicoo

import android.app.Application
import android.content.Context
import android.util.Log
import com.player.musicoo.bean.Audio
import com.player.musicoo.bean.CurrentPlayingAudio
import com.player.musicoo.bean.ResourcesList
import com.player.musicoo.database.AppDatabase
import com.player.musicoo.database.CurrentAudioDatabase
import com.player.musicoo.database.CurrentAudioManager
import com.player.musicoo.database.DatabaseManager
import com.player.musicoo.media.MediaControllerManager
import com.player.musicoo.util.parseResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class App : Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var app: App
            private set
        lateinit var currentAudioManager: CurrentAudioManager
            private set
        lateinit var databaseManager: DatabaseManager
            private set
        var currentPlayingAudio: CurrentPlayingAudio? = null
            private set
        lateinit var importList: List<Audio>
            private set
        lateinit var resourcesList: ResourcesList
            private set

        lateinit var realHumanVoiceList: List<Audio>
            private set

        lateinit var soundsOfAppliancesList: List<Audio>
            private set

        lateinit var soundsOfNatureList: List<Audio>
            private set

        private var isInitialized = false

        fun initialize(context: Context) {
            if (!isInitialized) {
                val jsonString = readAssetsFile(context)
                resourcesList = parseResources(context, jsonString)
                splitResourcesList()
                isInitialized = true
            }
        }

        fun initCurrentPlayingAudio() {
            CoroutineScope(Dispatchers.IO).launch {
                currentPlayingAudio = currentAudioManager.getCurrentPlayingAudio()
            }
        }

        fun initImportAudio(callback: (List<Audio>) -> Unit = {}) {
            CoroutineScope(Dispatchers.IO).launch {
                importList = databaseManager.getAllAudioFiles()
                withContext(Dispatchers.Main) {
                    Log.d("ocean", "initImportAudio importList->${importList.size}")
                    callback(importList)
                }
            }
        }

        private fun readAssetsFile(context: Context): String {
            val assetManager = context.assets
            val inputStream = assetManager.open("resources.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            return stringBuilder.toString()
        }

        private fun splitResourcesList() {
            realHumanVoiceList = mutableListOf()
            soundsOfAppliancesList = mutableListOf()
            soundsOfNatureList = mutableListOf()

            for (category in resourcesList.categories) {
                when (category.name) {
                    "Sound of instrument" -> realHumanVoiceList = category.audios
                    "White noise" -> soundsOfAppliancesList = category.audios
                    "Voice of Nature" -> soundsOfNatureList = category.audios
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        appContext = this
        initialize(this)
        MediaControllerManager.init(this)
        currentAudioManager = CurrentAudioManager.getInstance(this)
        databaseManager = DatabaseManager.getInstance(this)
        initCurrentPlayingAudio()
        initImportAudio()
    }

}