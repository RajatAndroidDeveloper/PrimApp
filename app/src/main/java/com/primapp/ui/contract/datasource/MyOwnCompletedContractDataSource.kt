package com.primapp.ui.contract.datasource

import android.util.Log
import androidx.paging.PagingSource
import com.primapp.model.mycontracts.CompletedContractsItem
import com.primapp.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class MyOwnCompletedContractDataSource  (private val contractType: String, private val apiService: ApiService) : PagingSource<Int, CompletedContractsItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CompletedContractsItem> {
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
            val response = apiService.getMyOwnContracts(contractType, offset, params.loadSize)
            LoadResult.Page(
                data = response.content?.results?.completedContracts!!,
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