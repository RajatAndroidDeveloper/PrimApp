package com.primapp.ui.profile.source


import android.util.Log
import androidx.paging.PagingSource
import com.primapp.model.members.CommunityMembersData
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.ResponseHandler
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class MentorMenteeMemberListDataSource(
    private val responseHandler: ResponseHandler,
    private val apiService: ApiService,
    private val userId: Int,
    private val type: String,
    private val status: Int
) : PagingSource<Int, CommunityMembersData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityMembersData> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            /*
            *   Page = 0 Offset = 0*10 = 0
            *   Page = 1 Offset = 1*10 = 10
            *
            *   Here loadSize = limit
            * */
            val offset = page * params.loadSize
            Log.d("anshul_paging", "Page:$page LoadSize : ${params.loadSize} Offset : $offset")
            val response = apiService.getMentorMenteeList(userId, type, status, offset, params.loadSize)
            LoadResult.Page(
                data = response.content.results,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (response.content.nextPage == null) null else page + 1
            )

        } catch (exception: IOException) {
            val error = IOException("Server is not reachable")
            LoadResult.Error(error)
        } catch (exception: HttpException) {
            val error = Exception(responseHandler.getErrorMessage(exception))
            LoadResult.Error(error)
        }

    }

    companion object {
        const val STARTING_PAGE_INDEX = 0
    }
}