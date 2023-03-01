package com.primapp.ui.todo

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.TodoTasksPriorityType
import com.primapp.databinding.FragmentViewTodoTaskBinding
import com.primapp.model.todo.TodoTaskItem
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.toSentenceCase
import kotlinx.android.synthetic.main.toolbar_community_back.*

class ViewTodoTaskFragment : BaseFragment<FragmentViewTodoTaskBinding>() {

    var todoTaskItem: TodoTaskItem? = null

    override fun getLayoutRes(): Int = R.layout.fragment_view_todo_task

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.view_task), toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
        setEditOptions()
        if (isLoaded && todoTaskItem != null) {
            refreshData()
            return
        }
        todoTaskItem = ViewTodoTaskFragmentArgs.fromBundle(requireArguments()).todoTaskItem

        refreshData()
    }

    private fun setEditOptions(){
        ivAdd.isVisible = false
        ivCreateNewMessage.isVisible = true

        ivCreateNewMessage.setOnClickListener {
            editTask()
        }
    }

    fun refreshData() {
        todoTaskItem?.let {
            binding.data = it
            binding.tvPriorityName.text = it.priority.toSentenceCase()
            binding.ivPriorityDot.setColorFilter(
                ContextCompat.getColor(requireContext(), TodoTasksPriorityType.getPriorityColor(it.priority)),
                PorterDuff.Mode.MULTIPLY
            )
            if (it.status.equals("COMPLETED", true)) {
                ivCreateNewMessage.setImageResource(R.drawable.ic_baseline_autorenew_24)
            } else {
                ivCreateNewMessage.setImageResource(R.drawable.edit)
            }
        }
    }

    fun editTask() {
        val bundle = Bundle()
        bundle.putSerializable("todoTaskItem", todoTaskItem)
        findNavController().navigate(R.id.addTodoTaskFragment, bundle)
    }
}