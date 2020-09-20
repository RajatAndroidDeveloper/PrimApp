package com.primapp.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.primapp.R
import com.primapp.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun showTitleBar(): Boolean = false
}