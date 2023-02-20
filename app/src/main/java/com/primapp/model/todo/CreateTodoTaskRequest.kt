package com.primapp.model.todo

import com.google.gson.annotations.SerializedName
import com.primapp.utils.ValidationResults
import java.io.Serializable

data class CreateTodoTaskRequest(
    @SerializedName("task_name")
    var taskName: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("priority")
    var priority: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("due_date")
    var dueDate: Long? = 0L,
) : Serializable {
    fun isValidFormData(): ValidationResults {
        if (taskName.isNullOrEmpty())
            return ValidationResults.EMPTY_TITLE

//        if (description.isNullOrEmpty())
//            return ValidationResults.EMPTY_DESCRIPTION

        if (dueDate == null)
            return ValidationResults.INVALID_DUE_DATE

        if (priority.isNullOrEmpty())
            return ValidationResults.EMPTY_PRIORITY

        return ValidationResults.SUCCESS
    }

}

data class CreateTodoTaskResponse(
    @SerializedName("content")
    val content: TodoTaskItem?
)

data class MultipleTodoActionRequest(
    @SerializedName("tasks_id")
    val tasksId: List<Int>
)