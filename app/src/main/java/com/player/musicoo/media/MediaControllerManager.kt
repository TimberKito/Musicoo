package com.player.musicoo.media

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.bean.Audio
import com.player.musicoo.bean.CurrentPlayingAudio
import com.player.musicoo.service.PlaybackService
import com.player.musicoo.util.containsContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MediaControllerManager {
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var currentAudioFile = ""

    fun init(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            Log.d("ocean", "MediaControllerManager init")

            Log.d("ocean", "MediaController connected: ${mediaController?.isConnected}")
        }, MoreExecutors.directExecutor())
    }

    fun getController(): MediaController? {
        return if (mediaController != null && mediaController!!.isConnected) {
            mediaController
        } else {
            null
        }
    }

    fun setupMedia(context: Context, audio: Audio, listener: Player.Listener) {
        if (currentAudioFile != audio.file) {
            currentAudioFile = audio.file

            val uri: Uri? = if (containsContent(audio.file)) {
                Uri.parse(audio.file)
            } else {
                Uri.parse("file:///android_asset/$currentAudioFile")
            }

            Log.d("ocean","uri->$uri")

            val resourceId = R.mipmap.musicoo_logo_img
            val imgUri: Uri? = if (audio.image.isNotEmpty()) {
                Uri.parse("file:///android_asset/${audio.image}")
            } else {
                Uri.parse("android.resource://${context.packageName}/$resourceId")
            }

            val mediaItem =
                MediaItem.Builder()
                    .setUri(uri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(audio.name)
                            .setTitle(audio.name)
                            .setArtworkUri(imgUri)
                            .build()
                    )
                    .build()
            if (isConnected()) {
                mediaController?.let {
                    it.addListener(listener)
                    it.setMediaItem(mediaItem)
                    it.repeatMode = Player.REPEAT_MODE_ONE
                    it.prepare()
                    it.play()
                    val currentPlayingAudio =
                        CurrentPlayingAudio(
                            audio.id,
                            audio.name,
                            audio.file,
                            audio.image,
                            audio.duration,
                            false
                        )
                    CoroutineScope(Dispatchers.IO).launch {
                        App.currentAudioManager.setCurrentPlayingAudio(currentPlayingAudio)
                        withContext(Dispatchers.Main) {
                            App.initCurrentPlayingAudio()//更新到入口变量中
                        }
                    }
                }
            }
        }
    }

    fun setupMedia(context: Context, audio: CurrentPlayingAudio, listener: Player.Listener) {
        if (currentAudioFile != audio.file) {
            currentAudioFile = audio.file

            val uri: Uri? = if (containsContent(audio.file)) {
                Uri.parse(audio.file)
            } else {
                Uri.parse("file:///android_asset/$currentAudioFile")
            }

            Log.d("ocean","uri->$uri")

            val resourceId = R.mipmap.musicoo_logo_img
            val imgUri: Uri? = if (audio.image.isNotEmpty()) {
                Uri.parse("file:///android_asset/${audio.image}")
            } else {
                Uri.parse("android.resource://${context.packageName}/$resourceId")
            }

//            val uri = Uri.parse("file:///android_asset/$currentAudioFile")
////            val mediaItem = MediaItem.fromUri(uri)
//            val imgUri = Uri.parse("file:///android_asset/${audio.image}")
            val mediaItem =
                MediaItem.Builder()
                    .setUri(uri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(audio.name)
                            .setTitle(audio.name)
                            .setArtworkUri(imgUri)
                            .build()
                    )
                    .build()
            if (isConnected()) {
                mediaController?.let {
                    it.addListener(listener)
                    it.setMediaItem(mediaItem)
                    it.repeatMode = Player.REPEAT_MODE_ONE
                    it.prepare()
                    it.play()
                    val currentPlayingAudio =
                        CurrentPlayingAudio(
                            audio.id,
                            audio.name,
                            audio.file,
                            audio.image,
                            audio.duration,
                            false
                        )
                    CoroutineScope(Dispatchers.IO).launch {
                        App.currentAudioManager.setCurrentPlayingAudio(currentPlayingAudio)
                        withContext(Dispatchers.Main) {
                            App.initCurrentPlayingAudio()//更新到入口变量中
                        }
                    }

                }
            }
        }
    }

    fun isConnected(): Boolean {
        mediaController?.let {
            return it.isConnected
        }
        return false
    }


    fun isPlaying(): Boolean {
        mediaController?.let {
            return it.isPlaying
        }
        return false
    }
}
