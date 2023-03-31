package com.primapp.ui.earning

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentTotalEarningAndSpendingBinding
import com.primapp.extensions.setDivider
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class TotalEarningAndSpendingFragment : BaseFragment<FragmentTotalEarningAndSpendingBinding>() {

    lateinit var adapter: TotalEarningSpendingAdapter
    private var earningSpendingList: ArrayList<EarningSpendingData> = ArrayList<EarningSpendingData>()

    override fun getLayoutRes() = R.layout.fragment_total_earning_and_spending

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        if(requireArguments().getString("type")?:"" == "Earning"){
            setToolbar(getString(R.string.total_earning), toolbar)
        }else{
            setToolbar(getString(R.string.total_spent), toolbar)
        }
        setAdapter()
    }

    private fun setAdapter() {
        earningSpendingList = createEarningSendingList()
        binding.rvTotalEarningSpending.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        adapter = TotalEarningSpendingAdapter(requireContext(), requireArguments().getString("type")?:"",earningSpendingList)
        binding.rvTotalEarningSpending.adapter = adapter
    }

    private fun createEarningSendingList(): java.util.ArrayList<EarningSpendingData> {
        var earningList = ArrayList<EarningSpendingData>()
        earningList.add(EarningSpendingData("Apple TV tester - need you to have Apple TV + iPAD + Macbook + iPhone to test Airplay","100.0","Jan 10, 2023"))
        earningList.add(EarningSpendingData("Getting app upload","200.0","Feb 14, 2023"))
        earningList.add(EarningSpendingData("Apple TV tester - need you to have Apple TV","200.0","Feb 20, 2023"))
        earningList.add(EarningSpendingData("Getting app deleted","200.0","Feb 23, 2023"))
        earningList.add(EarningSpendingData("iPAD + Macbook + iPhone to test Airplay","250.0","March 11, 2023"))
        earningList.add(EarningSpendingData("Macbook + iPhone to test Airplay","20.0","March 24, 2023"))
        return earningList
    }

}