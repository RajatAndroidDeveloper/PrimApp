package com.primapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primapp.model.auth.ReferenceResponseDataModel
import com.primapp.model.category.ParentCategoryResponseModel
import com.primapp.repository.CommunitiesRepository
import com.primapp.retrofit.base.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunitiesViewModel @Inject constructor(private val repo: CommunitiesRepository) : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()

    // get reference data
    private var _parentCategoryLiveData = MutableLiveData<Resource<ParentCategoryResponseModel>>()
    var parentCategoryLiveData: LiveData<Resource<ParentCategoryResponseModel>> =
        _parentCategoryLiveData

     fun getParentCategoriesList() = viewModelScope.launch {
        _parentCategoryLiveData.postValue(Resource.loading(null))
        _parentCategoryLiveData.postValue(repo.getParentCategoryList())
    }

}