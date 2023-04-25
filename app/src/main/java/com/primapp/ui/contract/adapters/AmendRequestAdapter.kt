package com.primapp.ui.contract.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.ItemAmendRequestLayoutBinding
import com.primapp.extensions.getHighlightedText
import com.primapp.model.contract.AmendRequestItem
import kotlinx.android.synthetic.main.item_amend_request_layout.view.*

class AmendRequestAdapter(private val amendRequestList : ArrayList<AmendRequestItem>, private var context: Context, private var onButtonCLickCallback: OnButtonClickCallback): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val loadMoreBinding = ItemAmendRequestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmendRequestViewHolder(loadMoreBinding)
    }

    override fun getItemCount(): Int {
        return amendRequestList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: AmendRequestViewHolder = holder as AmendRequestViewHolder
        viewHolder.bindData(amendRequestList[position])

        viewHolder.itemView.tvDeclineAmendRequest.setOnClickListener {
            onButtonCLickCallback.onButtonCLickCallback(amendRequestList[position], "DECLINED")
        }

        viewHolder.itemView.tvAcceptAmendRequets.setOnClickListener {
            onButtonCLickCallback.onButtonCLickCallback(amendRequestList[position], "ACCEPTED")
        }

        viewHolder.itemView.tvPayNow.setOnClickListener {
            onButtonCLickCallback.onButtonCLickCallback(amendRequestList[position], "PAY_NOW")
        }
    }

    inner class AmendRequestViewHolder(val binding: ItemAmendRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(data: AmendRequestItem){
            binding.data = data
            binding.user = UserCache.getUser(context)
            val colorToHighlight = ContextCompat.getColor(context, R.color.textColor)
            binding.tvAmendReason.text = "Reason: ${getHighlightedText(colorToHighlight,data.reason?:"")}"
        }
    }
}

interface OnButtonClickCallback {
    fun onButtonCLickCallback(data: AmendRequestItem, type: String)
}