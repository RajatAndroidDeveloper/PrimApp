package com.primapp.ui.todo

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.TodoTasksPriorityType
import com.primapp.databinding.FragmentAddTodoTaskBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.ui.todo.adapter.AutoCompleteTodoPriorityListArrayAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.TodoTasksViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class AddTodoTaskFragment : BaseFragment<FragmentAddTodoTaskBinding>() {

    val adapterPriority by lazy {
        AutoCompleteTodoPriorityListArrayAdapter(requireContext(), R.layout.item_todo_priority_list)
    }

    val viewModel by viewModels<TodoTasksViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_add_todo_task

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.add_task), toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setAdapter() {
        context?.apply {
            binding.mAutoCompletePriority.setAdapter(adapterPriority)

            binding.mAutoCompletePriority.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = adapterPriority.contains(p0.toString())
                    val data = viewModel.createTodoTaskRequestModel.value
                    if (isDataValid) {
                        data?.priority = adapterPriority.getItemKey(p0.toString())
                    } else {
                        data?.priority = null
                    }
                    viewModel.createTodoTaskRequestModel.value = data

                    return isDataValid
                }
            }
        }

        adapterPriority.addAll(TodoTasksPriorityType.getPriorityList())
    }

    private fun setObserver() {
        viewModel.createToDoTasksLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            DialogUtils.showCloseDialog(requireActivity(), R.string.todo_task_added) {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        })
    }

    fun addNewTask() {
        binding.mAutoCompletePriority.clearFocus()
        if (viewModel.validateData()) {
            viewModel.createTodoTasks()
        }
    }
}