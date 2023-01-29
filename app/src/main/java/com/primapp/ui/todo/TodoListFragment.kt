package com.primapp.ui.todo

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentTodoListBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_todo_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.todo_list), toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
        ivEndIcon.setImageResource(R.drawable.ic_add_circle_filled_24)
        ivEndIcon.setOnClickListener {
            addNewTask()
        }
    }

    fun addNewTask() {

    }

    fun refreshData() {

    }

}