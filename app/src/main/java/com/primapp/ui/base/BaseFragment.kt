package com.primapp.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.primapp.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject


abstract class BaseFragment<DB : ViewDataBinding> : DaggerFragment() {

    var isLoaded: Boolean = false

    open lateinit var binding: DB

    @Inject
    open lateinit var viewModelFactory: ViewModelProvider.Factory

    @LayoutRes
    abstract fun getLayoutRes(): Int

    private fun init(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(inflater, container)
        super.onCreateView(inflater, container, savedInstanceState)

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onResume() {
        super.onResume()
        isLoaded = true
    }

    fun showHelperDialog(message:String){
        val bundle = Bundle()
        bundle.putString("message",message)
        findNavController().navigate(R.id.popUpHelpMessage,bundle)
    }

    fun hideKeyBoard(input: View?) {
        input?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
        }
    }

}