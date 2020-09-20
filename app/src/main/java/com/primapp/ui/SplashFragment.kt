package com.primapp.ui

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentSplashBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.coroutines.*

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_splash

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityScope.launch{
            delay(3000)
           gotoNextActivity()
        }

    }

    fun gotoNextActivity(){
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}