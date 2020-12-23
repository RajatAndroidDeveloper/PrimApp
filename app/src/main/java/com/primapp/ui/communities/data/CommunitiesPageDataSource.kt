package com.primapp.ui.communities.data

import android.util.Log
import androidx.paging.PagingSource
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class CommunitiesPageDataSource(
    private val apiService: ApiService,
    private val categoryId: Int,
    private val query: String?,
    private val filterBy: String
) :
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
            val response = apiService.getCommunities(categoryId, query, filterBy, offset, params.loadSize)
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
    /*
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Passenger> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = api.getPassengersData(nextPageNumber)
            LoadResult.Page(
                data = response.data,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.totalPages) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    * */

    companion object {
        const val STARTING_PAGE_INDEX = 0
    }
}