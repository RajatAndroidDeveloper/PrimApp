package com.primapp.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.primapp.R
import com.primapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {

    override fun showTitleBar(): Boolean = false

    private lateinit var navController: NavController

    var bottomNavLabels: ArrayList<String> = arrayListOf(
        "UpdatesFragment",
        "NotificationsFragment",
        "CommunitiesFragment",
        "ProfileFragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initData()
        setupNavigation()
    }

    private fun initData() {
        navController = findNavController(R.id.nav_host_fragment)
    }

    private fun setupNavigation() {
        bottomNavigationView.setupWithNavController(navController)
        // To stop loading fragment again and again
        bottomNavigationView.setOnNavigationItemReselectedListener { }
    }

    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        //Hide Show the toolbar here
        if (bottomNavLabels.contains(destination.label)) {
            bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navListener)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navListener)
    }

}