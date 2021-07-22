package com.primapp.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.primapp.R
import com.primapp.databinding.FragmentCommonWebViewBinding
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CommonWebView : BaseFragment<FragmentCommonWebViewBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_common_web_view

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initWebView()
        setData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.builtInZoomControls = false
        webSettings.javaScriptEnabled = true

        binding.webView.webViewClient = WebViewClient()
    }

    private fun setData() {
        val url = CommonWebViewArgs.fromBundle(requireArguments()).url
        val title = CommonWebViewArgs.fromBundle(requireArguments()).title

        setToolbar(title.toString(), toolbar)
        binding.webView.loadUrl(url)
    }

}