package com.primapp.utils

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.primapp.R
import com.primapp.constants.EmoticonHelper
import com.primapp.model.chat.EmojiModel
import com.primapp.ui.chat.adapter.EmojiOptionsAdapter
import com.sendbird.android.SendbirdChat
import com.sendbird.android.message.BaseMessage
import kotlinx.android.synthetic.main.bottom_sheet_chat_options.view.*
import java.io.Serializable

class BottomSheetChatOptions : BottomSheetDialogFragment() {
    val TAG = "anshul_bottomSheet"
    val adapter by lazy { EmojiOptionsAdapter { item -> onItemClick(item) } }
    var message: BaseMessage? = null
    private val emojiList by lazy { prepareEmojiList() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.bottom_sheet_chat_options, container, false)

        //Set Views
        val isFileMessage = BottomSheetChatOptionsArgs.fromBundle(requireArguments()).isFileMessage
        val isMyMessage = BottomSheetChatOptionsArgs.fromBundle(requireArguments()).isMyMessage
        val tempMsg: Serializable? = arguments?.getSerializable("message")
        tempMsg?.let {
            message = BaseMessage.buildFromSerializedData(it as? ByteArray)
        }

        view.llCopyMessage.isVisible = !isFileMessage
        view.llDeleteMessage.isVisible = isMyMessage

        //Set Adapter
        view.rvEmoticons.apply {
            layoutManager = GridLayoutManager(requireContext(), 5)
        }
        view.rvEmoticons.adapter = adapter
        adapter.addData(emojiList)
        //Clicks for action
        setClicks(view)

        return view
    }

    private fun prepareEmojiList(): ArrayList<EmojiModel> {
        val emojiList = EmoticonHelper.getEmojis()
        message?.reactions?.forEach {
            val emojiItem = emojiList.find { item -> item.key == it.key }
            emojiItem?.isSelected = it.userIds.contains(SendbirdChat.currentUser?.userId)
        }
        return emojiList
    }

    private fun setClicks(view: View) {
        view.llCopyMessage.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("copyMessage", message?.serialize())
            findNavController().popBackStack()
        }

        view.llDeleteMessage.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("deleteMessage", message?.serialize())
            findNavController().popBackStack()
        }
    }

    fun onItemClick(any: Any) {
        when (any) {
            is EmojiModel -> {
                if (any.isSelected) {
                    //remove the reaction
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("emoticonToSend", EmoticonHelper.REMOVE_REACTION)
                } else {
                    //Find emoji data where icon is already added by this user
                    val item: EmojiModel? = emojiList.find { it.key != any.key && it.isSelected }
                    val isReactionAddedInAnyEmojiForUser: Boolean = item?.isSelected ?: false
                    if (isReactionAddedInAnyEmojiForUser){
                        //Update reaction
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("emoticonToSend", EmoticonHelper.UPDATE_REACTION)
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("oldEmojiKey", item!!.key)
                    }else{
                        //Add reaction
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("emoticonToSend", EmoticonHelper.ADD_REACTION)
                    }
                }

                findNavController().previousBackStackEntry?.savedStateHandle?.set("emojiKey", any.key)
                findNavController().previousBackStackEntry?.savedStateHandle?.set("message", message?.serialize())
                findNavController().popBackStack()
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}
