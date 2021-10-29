package com.primapp.ui.profile.source

import android.util.Log
import androidx.paging.PagingSource
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.base.ResponseHandler
import com.primapp.ui.post.reported_post.ReportedPostsFragment
import com.primapp.ui.profile.UserPostsFragment
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class UserPostListPageDataSource(
    private val responseHandler: ResponseHandler,
    private val apiService: ApiService,
    private val type: String,
    private val userId: Int
) : PagingSource<Int, PostListResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostListResult> {
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
            val response = when (type) {
                UserPostsFragment.BOOKMARK_POST -> apiService.getBookmarkedPost()
                ReportedPostsFragment.REPORTED_POST -> apiService.getReportedPosts(userId)
                else ->
                    apiService.getUserPostList(userId, offset, params.loadSize)
            }
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