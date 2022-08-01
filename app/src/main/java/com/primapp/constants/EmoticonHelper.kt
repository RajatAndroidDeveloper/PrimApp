package com.primapp.constants

import com.primapp.model.chat.EmojiModel

object EmoticonHelper {
    fun getEmojis(): ArrayList<EmojiModel> {
        val list: ArrayList<EmojiModel> = ArrayList()
        list.add(EmojiModel("sendbird_emoji_heart_eyes", "https://static.sendbird.com/icons/emoji_heart_eyes.png"))
        list.add(EmojiModel("emoji_laughing", "https://static.sendbird.com/icons/emoji_laughing.png"))
        //list.add(EmojiModel("sendbird_emoji_rage", "https://static.sendbird.com/icons/emoji_rage.png"))
        list.add(EmojiModel("sendbird_emoji_sob", "https://static.sendbird.com/icons/emoji_sob.png"))
        list.add(EmojiModel("sendbird_emoji_sweat_smile", "https://static.sendbird.com/icons/emoji_sweat_smile.png"))
        list.add(EmojiModel("sendbird_emoji_thumbsup", "https://static.sendbird.com/icons/emoji_thumbsup.png"))
        //list.add(EmojiModel("sendbird_emoji_thumbsdown", "https://static.sendbird.com/icons/emoji_thumbsdown.png"))
        return list
    }

    @JvmStatic
    fun getEmojiUrl(key: String):String? {
        val item = getEmojis().find { it.key == key }
        return item?.url
    }

    //Local variables
    const val ADD_REACTION = "add_reaction"
    const val REMOVE_REACTION = "remove_reaction"
    const val UPDATE_REACTION = "update_reaction"
}