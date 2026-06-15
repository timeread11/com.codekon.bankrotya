package com.codekon.bankrotya

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorLayout: LinearLayout
    private lateinit var btnRefresh: Button

    private val targetUrl = "https://bankrotya.ru"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        errorLayout = findViewById(R.id.errorLayout)
        btnRefresh = findViewById(R.id.btnRefresh)

        setupWebView()

        swipeRefreshLayout.setOnRefreshListener {
            loadWebsite()
        }

        btnRefresh.setOnClickListener {
            loadWebsite()
        }

        loadWebsite()
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipeRefreshLayout.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeRefreshLayout.isRefreshing = false
                if (isNetworkAvailable()) {
                    showWebView()
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                showErrorLayout()
            }
        }
    }

    private fun loadWebsite() {
        if (isNetworkAvailable()) {
            showWebView()
            webView.loadUrl(targetUrl)
        } else {
            swipeRefreshLayout.isRefreshing = false
            showErrorLayout()
        }
    }

    private fun showWebView() {
        webView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun showErrorLayout() {
        webView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
