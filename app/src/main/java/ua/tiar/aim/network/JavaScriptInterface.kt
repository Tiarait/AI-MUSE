package ua.tiar.aim.network

import android.webkit.JavascriptInterface

class JavaScriptInterface(private val onTokenReceived: (String) -> Unit) {
    @JavascriptInterface
    fun sendToken(token: String) {
        onTokenReceived(token)
    }
}