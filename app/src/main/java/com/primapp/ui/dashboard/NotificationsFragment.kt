package com.primapp.ui.dashboard

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentNotificationsBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_notifications

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("Notifications", toolbar)
        setData()

    }

    private fun setData() {
        ivEndIcon.setImageResource(R.drawable.filter)
    }
}