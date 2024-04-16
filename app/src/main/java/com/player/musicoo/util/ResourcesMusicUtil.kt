package com.player.musicoo.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import com.player.musicoo.bean.Audio
import com.player.musicoo.bean.Category
import com.player.musicoo.bean.ResourcesList
import org.json.JSONObject
import java.io.File
import java.io.InputStream

fun parseResources(context: Context, jsonString: String): ResourcesList {
    val jsonObject = JSONObject(jsonString)
    val categoriesArray = jsonObject.getJSONArray("categories")
    val categories = mutableListOf<Category>()

    for (i in 0 until categoriesArray.length()) {
        val categoryObject = categoriesArray.getJSONObject(i)
        val categoryName = categoryObject.getString("name")
        val audiosArray = categoryObject.getJSONArray("audios")
        val audios = mutableListOf<Audio>()

        for (j in 0 until audiosArray.length()) {
            val audioObject = audiosArray.getJSONObject(j)
            val audioName = audioObject.getString("name")
            val audioFile = audioObject.getString("file")
            val audioImage = audioObject.getString("image")
            val audio = Audio(
                audioName,
                audioFile,
                audioImage,
                getAudioDurationFromAssets(context, audioFile),
                false
            )
            audios.add(audio)
        }

        val category = Category(categoryName, audios)
        categories.add(category)
    }

    return ResourcesList(categories)
}

fun getAudioDurationFromAssets(context: Context, fileName: String): Long {
    val assetFileDescriptor = context.assets.openFd(fileName)
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(
        assetFileDescriptor.fileDescriptor,
        assetFileDescriptor.startOffset,
        assetFileDescriptor.length
    )

    val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val duration = durationString?.toLong() ?: 0

    retriever.release()
    assetFileDescriptor.close()

    return duration
}

fun containsContent(str: String): Boolean {
    return str.contains("content://")
}

fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
    return try {
        context.contentResolver.openInputStream(uri)
    } catch (e: Exception) {
        null
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val filePathColumn = it.getColumnIndex(MediaStore.Images.Media.DATA)
            val filePath = it.getString(filePathColumn)
            return File(filePath)
        }
    }
    return null
}
