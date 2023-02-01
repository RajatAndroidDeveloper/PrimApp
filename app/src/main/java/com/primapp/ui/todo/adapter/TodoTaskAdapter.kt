package com.primapp.ui.todo.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.TodoTasksPriorityType
import com.primapp.databinding.ItemTodoTaskBinding
import com.primapp.model.ViewTodoTask
import com.primapp.model.todo.TodoTaskItem
import com.primapp.utils.toSentenceCase
import javax.inject.Inject

class TodoTaskAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<TodoTaskAdapter.TaskViewHolder>() {

    var isCheckBoxVisible: Boolean = false
    val list = ArrayList<TodoTaskItem>()

    fun addData(listData: ArrayList<TodoTaskItem>?) {
        list.clear()
        isCheckBoxVisible = false
        listData?.let {
            list.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun toggleCheckbox() {
        if (!isCheckBoxVisible) {
            list.map { item -> item.isSelected = false }
        }
        isCheckBoxVisible = !isCheckBoxVisible
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_todo_task,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class TaskViewHolder(val binding: ItemTodoTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TodoTaskItem) {
            binding.data = data
            binding.tvPriorityName.text = data.priority.toSentenceCase()
            binding.ivPriorityDot.setColorFilter(
                ContextCompat.getColor(binding.root.context, TodoTasksPriorityType.getPriorityColor(data.priority)),
                PorterDuff.Mode.MULTIPLY
            )

            binding.checkboxTodo.isVisible = isCheckBoxVisible

            binding.root.setOnClickListener {
                onItemClick(ViewTodoTask(data))
            }
        }
    }
}