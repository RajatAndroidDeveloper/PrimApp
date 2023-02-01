package com.primapp.model.todo

import com.google.gson.annotations.SerializedName
import com.primapp.retrofit.base.BaseDataModel
import java.io.Serializable

data class TodoListResponse(
    @SerializedName("content")
    val content: Content
) : BaseDataModel()

data class Content(
    @SerializedName("completed_tasks")
    val completedTasks: ArrayList<TodoTaskItem>?,
    @SerializedName("inprogress_tasks")
    val inprogressTasks: ArrayList<TodoTaskItem>?
)

data class TodoTaskItem(
    @SerializedName("cdate")
    val cdate: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_deleted")
    val isDeleted: Boolean?,
    @SerializedName("priority")
    var priority: String,
    @SerializedName("status")
    var status: String?,
    @SerializedName("task_name")
    var taskName: String?,
    @SerializedName("udate")
    val udate: String?,
    @SerializedName("user_id")
    val userId: Int?,
    //Local for selection
    var isSelected: Boolean = false
): Serializable