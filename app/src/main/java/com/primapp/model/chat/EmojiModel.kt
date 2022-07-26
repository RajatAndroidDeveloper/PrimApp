package com.primapp.model.chat

data class EmojiModel(
    val key: String,
    val url: String,
    var isSelected: Boolean = false
)