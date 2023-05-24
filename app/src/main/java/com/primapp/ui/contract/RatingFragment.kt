package com.primapp.ui.contract

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primapp.R
import com.primapp.databinding.FragmentRatingsLayoutBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.contract.RatingItem
import com.primapp.model.rating.ContentItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.contract.adapters.RatingsAdapter
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class RatingFragment : BaseFragment<FragmentRatingsLayoutBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_ratings_layout
    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.ratings), toolbar)

        if(RatingFragmentArgs.fromBundle(requireArguments()).from == "ProjectDetails"){
            ratingList = Gson().fromJson<ArrayList<RatingItem>>(RatingFragmentArgs.fromBundle(requireArguments()).ratingData, object :
                TypeToken<ArrayList<RatingItem>>(){}.type)
            if(ratingList.isNullOrEmpty()){
                binding.tvNoData.isVisible = true
            }
            setAdapter(ratingsList, ratingList, "ProjectDetails")
        }else {
            setAdapter(ratingsList, null, "Dashboard")
            viewModel.getContractRating()
        }
        attachObserver()
    }

    private var ratingsList: ArrayList<ContentItem> = ArrayList<ContentItem>()
    private var ratingList: ArrayList<RatingItem> = ArrayList<RatingItem>()
    private fun attachObserver() {
        viewModel.getRatingLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        if(it.data?.content?.isNullOrEmpty() == true){
                            binding.tvNoData.isVisible = true
                        }else{
                            binding.tvNoData.isVisible = false
                            ratingsList = it.data?.content as ArrayList<ContentItem>
                            setAdapter(ratingsList,null,"Dashboard")
                        }
                    }
                }
            }
        })
    }

    fun refreshData(){
        viewModel.getContractRating()
    }

    private fun setAdapter(ratingsList: ArrayList<ContentItem>, ratingList: ArrayList<RatingItem>?, type:String) {
        var layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllRatings.layoutManager = layoutManager
        binding.rvAllRatings.setDivider(R.drawable.recyclerview_divider)
        var ratingsAdapter = RatingsAdapter(ratingsList, ratingList, type)
        binding.rvAllRatings.adapter = ratingsAdapter
    }
}