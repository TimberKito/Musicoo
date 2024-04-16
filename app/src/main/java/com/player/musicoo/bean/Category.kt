package com.player.musicoo.bean

import java.io.Serializable

data class Category(
    val name: String,
    val audios: List<Audio>
) : Serializable