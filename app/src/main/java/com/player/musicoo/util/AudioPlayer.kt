package com.player.musicoo.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnCompletionListener {
                // 在播放完成时重置 MediaPlayer
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            }
        }
    }

    fun playAudio(fileName: String) {
        try {
            val assetFileDescriptor = context.assets.openFd(fileName)
            mediaPlayer?.apply {
                reset()
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                prepare()
                start()
                isLooping = true // 开启重复播放
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
    }

    fun resumeAudio() {
        mediaPlayer?.start()
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun currentPosition(): Int {
        return mediaPlayer?.currentPosition!!
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying!!
    }
}
