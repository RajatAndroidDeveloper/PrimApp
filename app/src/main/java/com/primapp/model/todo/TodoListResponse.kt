package com.primapp.model.todo

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel

data class TodoListResponse(
    @SerializedName("content")
    val content: Content
) : BaseDataModel()

data class Content(
    @SerializedName("completed_tasks")
    val completedTasks: List<TodoTaskItem>?,
    @SerializedName("inprogress_tasks")
    val inprogressTasks: List<TodoTaskItem>?
)

data class TodoTaskItem(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("description")
    val description: Any?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_deleted")
    val isDeleted: Boolean?,
    @SerializedName("priority")
    val priority: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user_id")
    val userId: Int?
)