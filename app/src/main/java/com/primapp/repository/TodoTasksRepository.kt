package com.primapp.repository

import com.primapp.model.todo.TodoListResponse
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.Resource
import com.primapp.retrofit.base.ResponseHandler
import javax.inject.Inject

class TodoTasksRepository @Inject constructor(
    val apiService: ApiService,
    private val responseHandler: ResponseHandler
) {

    suspend fun getListOfTodoTasks(): Resource<TodoListResponse> {
        return try {
            responseHandler.handleResponse(apiService.getTodoTasks())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}