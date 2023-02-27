package com.primapp.ui.todo

import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentTodoListBinding
import com.primapp.extensions.loanLoacalGIF
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.ViewTodoTask
import com.primapp.model.todo.TodoTaskItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.todo.adapter.TodoTaskAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.TodoTasksViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {

    private val adapterInProgressTask by lazy { TodoTaskAdapter { item -> onItemClick(item) } }
    private val adapterCompletedTask by lazy { TodoTaskAdapter { item -> onItemClick(item) } }

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
        binding.rvTodoListInProgress.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setDivider(R.drawable.recyclerview_divider)
        }
        binding.rvTodoListInProgress.adapter = adapterInProgressTask

        binding.rvTodoListCompleted.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setDivider(R.drawable.recyclerview_divider)
        }
        binding.rvTodoListCompleted.adapter = adapterCompletedTask
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
                            binding.llEmptyList.isVisible = it.inprogressTasks.isNullOrEmpty()
                            if (it.inprogressTasks.isNullOrEmpty() && it.completedTasks.isNullOrEmpty()) {
                                // binding.ivEmptyTodo.setImageResource(R.drawable.ic_empty_todo)
                                binding.ivEmptyTodo.loanLoacalGIF(R.raw.shibaslides)
                                binding.ivEmptyTodo.scaleType = ImageView.ScaleType.FIT_XY
                                binding.tvEmptyTodo.text = getString(R.string.todo_list_started)
                                binding.tvGreatJob.isVisible = false
                                binding.btnSeeCompletedTask.isVisible = false
                            } else {
                                //binding.ivEmptyTodo.setImageResource(R.drawable.ic_todo_completed)
                                binding.ivEmptyTodo.loanLoacalGIF(R.raw.shibasleeps)
                                binding.tvEmptyTodo.text = getString(R.string.todo_list_completed)
                                binding.tvGreatJob.isVisible = true
                                binding.btnSeeCompletedTask.isVisible = true
                            }

                            binding.llActions.isVisible = !it.inprogressTasks.isNullOrEmpty()
                            adapterInProgressTask.addData(it.inprogressTasks)
                            adapterCompletedTask.addData(it.completedTasks)
                        }
                    }
                }
            }
        })

        viewModel.markTodoCompletedLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.let {
                            showEditButton()
                            adapterInProgressTask.toggleCheckbox()
                            refreshData()
                        }
                    }
                }
            }
        })

        viewModel.deleteTodoLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.let {
                            showEditButton()
                            adapterInProgressTask.toggleCheckbox()
                            refreshData()
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
        showEditOptions()
        adapterInProgressTask.toggleCheckbox()
    }

    fun onDone() {
        val selectedItems = adapterInProgressTask.list.filter { it -> it.isSelected }.map { item -> item.id }

        if (selectedItems.isNotEmpty()) {
            viewModel.markTodoCompleted(selectedItems)
        } else {
            showEditButton()
            adapterInProgressTask.toggleCheckbox()
        }
    }

    fun onDelete() {
        var stringToDisplay = ""
        val selectedItems = adapterInProgressTask.list.filter { it.isSelected }
        if (selectedItems.isEmpty()) {
            DialogUtils.showCloseDialog(requireActivity(), R.string.no_todo_selected)
        } else {
            selectedItems.forEach { it ->
                stringToDisplay = if (it.id == selectedItems.first().id) {
                    "\u2022 ${it.taskName}\n"
                } else {
                    "${stringToDisplay}\u2022 ${it.taskName}\n"
                }
            }
            val idsToSend = selectedItems.map { it.id }
            DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_todo_msg, stringToDisplay, {
                viewModel.deleteTodos(idsToSend)
            })
        }
    }

    private fun showEditOptions() {
        binding.ivEdit.isVisible = false
        binding.ivDone.isVisible = true
        binding.ivDelete.isVisible = true
    }

    private fun showEditButton() {
        //binding.ivEdit.isVisible = true
        binding.ivDone.isVisible = false
        binding.ivDelete.isVisible = false
    }

    fun toggleCompletedView() {
        if (binding.rvTodoListCompleted.isVisible) {
            binding.tvCompleted.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_24, 0)
        } else {
            binding.tvCompleted.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_24, 0)
        }
        binding.rvTodoListCompleted.isVisible = !binding.rvTodoListCompleted.isVisible
    }

    fun viewCompletedTasks() {
        binding.llEmptyList.isVisible = false
    }

    fun onItemClick(item: Any?) {
        when (item) {
            is ViewTodoTask -> {
                val bundle = Bundle()
                bundle.putSerializable("todoTaskItem", item.todoTaskItem)
                findNavController().navigate(R.id.viewTodoTaskFragment, bundle)
            }

            is TodoTaskItem -> {
                //check change listener
                val itemSelected: List<TodoTaskItem> = adapterInProgressTask.list.filter { it.isSelected }
                if (itemSelected.isEmpty()) {
                    showEditButton()
                } else {
                    showEditOptions()
                }
            }
        }
    }

}