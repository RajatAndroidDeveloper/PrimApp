package com.primapp.ui.base

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.primapp.R
import com.primapp.databinding.FragmentCommonWebViewBinding
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import android.webkit.WebResourceError

import android.webkit.WebResourceRequest

import android.webkit.WebView




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
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView, request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("anshul_webview","${error.errorCode} + ${error.description} + ${error.toString()}")
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("anshul_webview","Finish loading")
            }
        }
    }

    private fun setData() {
        val url = CommonWebViewArgs.fromBundle(requireArguments()).url
        val title = CommonWebViewArgs.fromBundle(requireArguments()).title

        setToolbar(title.toString(), toolbar)
        binding.webView.loadUrl(url)
    }

}