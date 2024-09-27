package ua.tiar.aim.ui.components

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ua.tiar.aim.network.JavaScriptInterface
import ua.tiar.aim.viewmodel.AppViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(modifier: Modifier = Modifier, url: String = "", viewModel: AppViewModel) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)

                        view.evaluateJavascript(
                            """
                            (function() {
                                let html = document.body.innerHTML;
                                let match = html.match(/sitekey: \'(.*?)\'/);
                                if (match && match[1]) {
                                    return match[1];
                                }
                                return "null";
                            })();
                            """.trimIndent()
                        ) { s ->
                            Log.e("WebView", "sitekey=${s}")
                            if (s != "null") {
                                view.evaluateJavascript(
                                    """
                                    let script = document.createElement("script");
                                    script.src = "https://challenges.cloudflare.com/turnstile/v0/api.js?onload=onloadTurnstileCallback";
                                    script.defer = true;
                                    document.body.appendChild(script);
        
                                    function onloadTurnstileCallback() {
                                        turnstile.render('#waitingEl', {
                                            sitekey: '${s.trim('"')}',
                                            callback: function(token) {
                                                AndroidInterface.sendToken(token);
                                            },
                                            'error-callback': function() {
                                                AndroidInterface.sendToken("null");
                                                console.error('Turnstile error');
                                            }
                                        });
                                    }
                                    """.trimIndent(), null
                                )
                            }
                        }
                    }
                }
                addJavascriptInterface(JavaScriptInterface { token ->
                    Log.e("WebView", "token=${token}")
                    viewModel.getUserVerify(token = token)
                }, "AndroidInterface")
            }
        },
        update = {
            it.loadUrl(url)
        },
        modifier = modifier.fillMaxSize()
    )
}