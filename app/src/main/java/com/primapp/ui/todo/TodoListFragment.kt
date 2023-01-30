package com.primapp.ui.todo

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentTodoListBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.todo.adapter.TodoTaskAdapter
import com.primapp.viewmodels.TodoTasksViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {

    private val adapterInProgressTask by lazy { TodoTaskAdapter() }

    val viewModel by viewModels<TodoTasksViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_todo_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.todo_list), toolbar)
        setData()
        setAdapter()
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

    private fun setAdapter() {
        binding.rvTodoList.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setDivider(R.drawable.recyclerview_divider)
        }
        binding.rvTodoList.adapter = adapterInProgressTask
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
                            binding.llActions.isVisible = !it.inprogressTasks.isNullOrEmpty()
                            adapterInProgressTask.addData(it.inprogressTasks)
                        }
                    }
                }
            }
        })
    }

    fun addNewTask() {
        findNavController().navigate(R.id.addTodoTaskFragment)
    }

    fun refreshData() {
        viewModel.getListOfTodoTasks()
    }

    fun onEdit() {
        binding.ivEdit.isVisible = false
        binding.ivDone.isVisible = true
        binding.ivDelete.isVisible = true
    }

    fun onDone() {
        binding.ivEdit.isVisible = true
        binding.ivDone.isVisible = false
        binding.ivDelete.isVisible = false
    }

}