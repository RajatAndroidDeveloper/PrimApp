package com.primapp.ui.todo

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.TodoTasksPriorityType
import com.primapp.databinding.FragmentViewTodoTaskBinding
import com.primapp.model.todo.TodoTaskItem
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.toSentenceCase
import com.primapp.viewmodels.TodoTasksViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

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
        if (isLoaded || todoTaskItem != null) {
            refreshData()
            return
        }
        todoTaskItem = ViewTodoTaskFragmentArgs.fromBundle(requireArguments()).todoTaskItem

        binding.ivPriorityDot.setColorFilter(
            ContextCompat.getColor(requireContext(), TodoTasksPriorityType.getPriorityColor(todoTaskItem!!.priority)),
            PorterDuff.Mode.MULTIPLY
        )
        refreshData()
    }

    fun refreshData() {
        todoTaskItem?.let {
            binding.data = it
            binding.tvPriorityName.text = it.priority.toSentenceCase()
        }
    }

    fun editTask() {
        val bundle = Bundle()
        bundle.putSerializable("todoTaskItem", todoTaskItem)
        findNavController().navigate(R.id.addTodoTaskFragment, bundle)
    }
}