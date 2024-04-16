package com.player.musicoo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.player.musicoo.R
import com.player.musicoo.activity.MainActivity

class AudioPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = AudioPlayerBinder()

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun playAudio(audioUri: String) {
        mediaPlayer?.let {
            it.stop()
            it.reset() // 重置 MediaPlayer，确保处于空闲状态
            it.release()
        }
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            val assetFileDescriptor = assets.openFd(audioUri)
            setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            prepareAsync()
            setOnPreparedListener { start() }
            isLooping = true // 开启重复播放
        }
        startForegroundService()
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "audio_player_channel"
            val channelName = "Audio Player"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("正在播放音频")
                .setContentText("点击以返回应用")
                .setSmallIcon(R.mipmap.musicoo_logo_img)
                .setContentIntent(pendingIntent)
                .build()

            startForeground(1, notification)
        }
    }
}
