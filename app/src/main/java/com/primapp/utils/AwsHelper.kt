package com.primapp.utils

object AwsHelper {

    enum class AWS_OBJECT_TYPE { COMMUNITY, USER, POST }

    fun getObjectName(type: AWS_OBJECT_TYPE, id: Int, extension: String): String {
        return "PRIM_${type}_${id}_${System.currentTimeMillis()}.${extension}"
    }

}