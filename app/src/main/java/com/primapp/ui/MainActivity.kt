package com.primapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.primapp.R
import com.primapp.ui.base.BaseActivity
import com.primapp.ui.initial.VerifyOTPFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onNewIntent(intent: Intent?) {

        when(navController.currentDestination?.id){
            R.id.loginFragment->{
                if(intent?.scheme!!.equals("forgot_username", true)){
                    navController.navigate(R.id.forgotUsernameFragment)
                }else if(intent?.scheme!!.equals("forgot_password", true)){
                    navController.navigate(R.id.forgotPasswordFragment)
                }
            }

            R.id.verifyOTPFragment->{
                if(intent?.scheme!!.equals("resend_code", true)){
                    (nav_host_fragment.childFragmentManager.fragments[0] as VerifyOTPFragment).resendCode()
                }
            }

            else->{
                super.onNewIntent(intent)
            }
        }


       /* if (navController.currentDestination?.id == R.id.loginFragment &&
            intent?.scheme!!.equals("forgot_username", true)
        ) {
            navController.navigate(R.id.forgotUsernameFragment)
        } else if (navController.currentDestination?.id == R.id.loginFragment &&
            intent?.scheme!!.equals("forgot_password", true)
        ) {
            navController.navigate(R.id.forgotPasswordFragment)

        }else{
            super.onNewIntent(intent)
        }*/
    }

    override fun showTitleBar(): Boolean = false
}