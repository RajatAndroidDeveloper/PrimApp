package com.primapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.primapp.model.category.CommunityData
import com.primapp.model.category.CommunityListResponseModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.repository.CommunitiesRepository
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.base.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunitiesViewModel @Inject constructor(private val repo: CommunitiesRepository) :
    ViewModel() {

    var isLoading = MutableLiveData<Boolean>()

    // get Parent Category List
    private var _parentCategoryLiveData = MutableLiveData<Resource<ParentCategoryResponseModel>>()
    var parentCategoryLiveData: LiveData<Resource<ParentCategoryResponseModel>> =
        _parentCategoryLiveData

    fun getParentCategoriesList(offset: Int, limit: Int) = viewModelScope.launch {
        _parentCategoryLiveData.postValue(Resource.loading(null))
        _parentCategoryLiveData.postValue(repo.getParentCategoryList(offset, limit))
    }

    // get Parent Category List
    private var currentQueryValue: String? = null
    private var communityListResultLiveData: LiveData<PagingData<CommunityData>>? = null

    fun getCommunityListData(categoryId: Int, query: String?, filterBy: String): LiveData<PagingData<CommunityData>> {
        val lastResult = communityListResultLiveData
        if (query.equals(currentQueryValue) && lastResult != null) {
            return lastResult
        }
        currentQueryValue = query
        val newResultLiveData: LiveData<PagingData<CommunityData>> =
            repo.getCommunitiesList(categoryId, query, filterBy).cachedIn(viewModelScope)
        communityListResultLiveData = newResultLiveData
        return newResultLiveData
    }

    init {

    }

}