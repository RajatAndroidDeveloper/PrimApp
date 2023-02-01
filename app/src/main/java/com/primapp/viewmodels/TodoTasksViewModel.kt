package com.primapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.primapp.PrimApp
import com.primapp.R
import com.primapp.model.todo.CreateTodoTaskRequest
import com.primapp.model.todo.CreateTodoTaskResponse
import com.primapp.model.todo.MultipleTodoActionRequest
import com.primapp.model.todo.TodoListResponse
import com.primapp.repository.TodoTasksRepository
import com.primapp.retrofit.base.BaseDataModel
import com.primapp.retrofit.base.Event
import com.primapp.retrofit.base.Resource
import com.primapp.utils.ErrorFields
import com.primapp.utils.ValidationResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoTasksViewModel @Inject constructor(
    errorFields: ErrorFields,
    application: Application,
    val repo: TodoTasksRepository
) : AndroidViewModel(application) {

    private val context by lazy { getApplication<PrimApp>().applicationContext }

    val errorFieldsLiveData = MutableLiveData<ErrorFields>()

    val createTodoTaskRequestModel = MutableLiveData<CreateTodoTaskRequest>()

    init {
        errorFieldsLiveData.value = errorFields
        createTodoTaskRequestModel.value = CreateTodoTaskRequest(null, null, null, null)
    }

    fun validateData(): Boolean {
        val error = errorFieldsLiveData.value
        error?.errorTitle = null
        error?.errorDescription = null
        error?.errorPriority = null
        errorFieldsLiveData.value = error

        Log.i("anshul", "validating")

        val result = createTodoTaskRequestModel.value?.isValidFormData()

        Log.i("anshul", "$result")

        when (result) {
            ValidationResults.EMPTY_TITLE -> {
                errorFieldsLiveData.value?.errorTitle =
                    context.getString(R.string.valid_empty_title)
            }

            ValidationResults.EMPTY_DESCRIPTION -> {
                errorFieldsLiveData.value?.errorDescription =
                    context.getString(R.string.valid_empty_description)
            }

            ValidationResults.EMPTY_PRIORITY -> {
                errorFieldsLiveData.value?.errorPriority =
                    context.getString(R.string.valid_empty_priority)
            }

            ValidationResults.SUCCESS -> {
                Log.i("anshul", "Success Data : ${Gson().toJson(createTodoTaskRequestModel.value)}")
                return true
            }
        }
        return false
    }

    private var _todoTasksListLiveData = MutableLiveData<Event<Resource<TodoListResponse>>>()
    var todoTasksListLiveData: LiveData<Event<Resource<TodoListResponse>>> = _todoTasksListLiveData

    fun getListOfTodoTasks() = viewModelScope.launch {
        _todoTasksListLiveData.postValue(Event(Resource.loading(null)))
        _todoTasksListLiveData.postValue(Event(repo.getListOfTodoTasks()))
    }

    private var _createToDoTasksLiveData = MutableLiveData<Event<Resource<CreateTodoTaskResponse>>>()
    var createToDoTasksLiveData: LiveData<Event<Resource<CreateTodoTaskResponse>>> = _createToDoTasksLiveData

    fun createTodoTasks() = viewModelScope.launch {
        _createToDoTasksLiveData.postValue(Event(Resource.loading(null)))
        _createToDoTasksLiveData.postValue(Event(repo.createTodoTasks(createTodoTaskRequestModel.value!!)))
    }

    private var _markTodoCompletedLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var markTodoCompletedLiveData: LiveData<Event<Resource<BaseDataModel>>> = _markTodoCompletedLiveData

    fun markTodoCompleted(list: List<Int>) = viewModelScope.launch {
        _markTodoCompletedLiveData.postValue(Event(Resource.loading(null)))
        _markTodoCompletedLiveData.postValue(Event(repo.markTodoCompleted(MultipleTodoActionRequest(list))))
    }

    private var _deleteTodoLiveData = MutableLiveData<Event<Resource<BaseDataModel>>>()
    var deleteTodoLiveData: LiveData<Event<Resource<BaseDataModel>>> = _deleteTodoLiveData

    fun deleteTodos(list: List<Int>) = viewModelScope.launch {
        _deleteTodoLiveData.postValue(Event(Resource.loading(null)))
        _deleteTodoLiveData.postValue(Event(repo.deleteTodos(MultipleTodoActionRequest(list))))
    }

    private var _updateTodoLiveData = MutableLiveData<Event<Resource<CreateTodoTaskResponse>>>()
    var updateTodoLiveData: LiveData<Event<Resource<CreateTodoTaskResponse>>> = _updateTodoLiveData

    fun updateTodo(taskId: Int) = viewModelScope.launch {
        _updateTodoLiveData.postValue(Event(Resource.loading(null)))
        _updateTodoLiveData.postValue(Event(repo.updateTodoTask(taskId, createTodoTaskRequestModel.value!!)))
    }
}