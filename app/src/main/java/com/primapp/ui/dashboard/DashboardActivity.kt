package com.primapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {

    override fun showTitleBar(): Boolean = false

    private lateinit var navController: NavController

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


}