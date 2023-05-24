package com.primapp.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.primapp.utils.ProgressDialog
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    open lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(showTitleBar())
            supportActionBar?.show()
        else
            supportActionBar?.hide()
    }

    abstract fun showTitleBar(): Boolean

    fun showLoading() {
        ProgressDialog.showProgressBar(this)
    }

    fun hideLoading() {
        ProgressDialog.dismissProgressDialog()
    }

    fun hideKeyBoard(input: View?) {
        input?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
        }
    }
}