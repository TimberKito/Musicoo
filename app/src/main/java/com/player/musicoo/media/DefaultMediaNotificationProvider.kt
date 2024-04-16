package com.player.musicoo.media

import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.player.musicoo.R

@UnstableApi
class MyMediaNotificationProvider(val context: Context) : MediaNotification.Provider {
    companion object {
        private const val CHANNEL_ID = "musicoo_notification_channel"
        private const val NOTIFICATION_ID = 1231
    }

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {

        val customLayoutRes = R.layout.my_notification_layout
        val customLayoutView = RemoteViews(context.packageName, customLayoutRes)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Custom Notification Title")
            .setContentText("Custom Notification Text")
            .setSmallIcon(R.mipmap.musicoo_logo_img)
            .setCustomContentView(customLayoutView)
            .build()

        return MediaNotification(NOTIFICATION_ID, notification)
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean {
        // 处理自定义命令
        return false
    }
}
