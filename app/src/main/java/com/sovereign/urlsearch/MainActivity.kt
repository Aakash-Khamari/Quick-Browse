package com.sovereign.urlsearch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {

    private lateinit var url: TextInputEditText
    private lateinit var search: MaterialButton
    private lateinit var homeButton: ImageView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var webView: WebView
    private lateinit var homeLayout: LinearLayout
    private lateinit var google : ImageView
    private lateinit var facebook : ImageView
    private lateinit var youTube : ImageView
    private lateinit var linkedIn : ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        url = findViewById(R.id.search_bar)
        search = findViewById(R.id.search_go_btn)
        homeButton = findViewById(R.id.home)
        progressBar = findViewById(R.id.progressBar)
        google = findViewById(R.id.google)
        facebook = findViewById(R.id.facebook)
        youTube = findViewById(R.id.youTube)
        linkedIn = findViewById(R.id.linkedIn)

        homeLayout = findViewById(R.id.homeLayout)
        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        search.setOnClickListener {
            performSearch()
        }

        url.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch()
                true
            } else {
                false
            }
        }

        homeButton.setOnClickListener {
            homeLayout.visibility = View.VISIBLE
            webView.visibility = View.GONE

            // Clear EditText
            url.text = null

            // Clear WebView content
            webView.loadUrl("about:blank")
        }

        google.setOnClickListener {
                webView.visibility = View.VISIBLE
                homeLayout.visibility = View.GONE
                webView.loadUrl("https://www.google.com/")
            }

        facebook.setOnClickListener {
            webView.visibility = View.VISIBLE
            homeLayout.visibility = View.GONE
            webView.loadUrl("https://www.facebook.com/")
        }

        youTube.setOnClickListener {
            webView.visibility = View.VISIBLE
            homeLayout.visibility = View.GONE
            webView.loadUrl("https://www.youtube.com/")
        }

        linkedIn.setOnClickListener {
            webView.visibility = View.VISIBLE
            homeLayout.visibility = View.GONE
            webView.loadUrl("https://www.linkedin.com/")
        }

    }

    private fun performSearch(){
        var link = url.text.toString().trim()
        if (link.isNotEmpty()) {
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = if (isValidUrl(link)) {
                    "https://$link"
                }
                else{
                    "https://www.google.com/search?q=$link"
                }

            }
            webView.visibility = View.VISIBLE
            homeLayout.visibility = View.GONE
            webView.loadUrl(link)

            hideKeyboard(search)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun isValidUrl(url: String): Boolean {
        val regex = "^(http://|https://)?(www\\.)?([\\w]+\\.)+\\w{2,63}(/\\w*)?$".toRegex()
        return url.matches(regex)
    }

    override fun onBackPressed() {
        if (webView.visibility == View.VISIBLE) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                webView.visibility = View.GONE
                homeLayout.visibility = View.VISIBLE
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    }

