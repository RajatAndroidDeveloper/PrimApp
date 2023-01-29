package com.primapp.ui.todo

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.primapp.R
import com.primapp.databinding.FragmentTodoListBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.viewmodels.TodoTasksViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {

    val viewModel by viewModels<TodoTasksViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_todo_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.todo_list), toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        ivEndIcon.setImageResource(R.drawable.ic_add_circle_filled_24)
        ivEndIcon.setOnClickListener {
            addNewTask()
        }

        refreshData()
    }

    private fun setObserver() {
        viewModel.todoTasksListLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            binding.llEmptyList.isVisible =
                                it.completedTasks.isNullOrEmpty() && it.inprogressTasks.isNullOrEmpty()

                        }
                    }
                }
            }
        })
    }

    fun addNewTask() {

    }

    fun refreshData() {
        viewModel.getListOfTodoTasks()
    }

}