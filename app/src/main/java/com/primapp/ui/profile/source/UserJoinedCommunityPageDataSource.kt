package com.primapp.ui.profile.source

import android.util.Log
import androidx.paging.PagingSource
import com.primapp.model.community.CommunityData
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class UserJoinedCommunityPageDataSource(private val apiService: ApiService, private val filter: String) :
    PagingSource<Int, CommunityData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityData> {
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
            val response = apiService.getJoinedCommunityList(offset, params.loadSize, filter)
            LoadResult.Page(
                data = response.content.results,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (response.content.nextPage == null) null else page + 1
            )

        } catch (exception: IOException) {
            val error = IOException("Please Check Internet Connection")
            LoadResult.Error(error)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }

    companion object {
        const val STARTING_PAGE_INDEX = 0
    }
}