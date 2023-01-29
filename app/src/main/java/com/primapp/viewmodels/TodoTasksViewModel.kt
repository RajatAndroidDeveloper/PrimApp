package com.primapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primapp.model.todo.TodoListResponse
import com.primapp.repository.TodoTasksRepository
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoTasksViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: TodoTasksRepository
) : AndroidViewModel(application) {

    private var _todoTasksListLiveData = MutableLiveData<Event<Resource<TodoListResponse>>>()
    var todoTasksListLiveData: LiveData<Event<Resource<TodoListResponse>>> = _todoTasksListLiveData

    fun getListOfTodoTasks() = viewModelScope.launch {
        _todoTasksListLiveData.postValue(Event(Resource.loading(null)))
        _todoTasksListLiveData.postValue(Event(repo.getListOfTodoTasks()))
    }
}