package com.example.instafullscreen;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;
import android.window.OnBackInvokedDispatcher;

public class MainActivity extends Activity {
    private WebView web;
    private static final String HOME = "https://www.instagram.com/";

    private static final String FILTER_JS = """
        (function() {
          if (window.__instaNoReels) return;
          window.__instaNoReels = true;
          const isReel = (href) => { try { return /^\/reels?(\/|$)/i.test(new URL(href, location.href).pathname); } catch(e) { return false; } };
          const style = document.createElement('style');
          style.textContent = 'a[href^="/reel/"],a[href^="/reels/"],a[href*="instagram.com/reel/"],a[href*="instagram.com/reels/"]{display:none!important}';
          (document.head || document.documentElement).appendChild(style);
          function clean() {
            if (/^\/reels?(\/|$)/i.test(location.pathname)) { location.replace('/'); return; }
            document.querySelectorAll('a[href]').forEach(a => {
              if (!isReel(a.getAttribute('href'))) return;
              const tile = a.closest('article');
              if (tile && tile.querySelectorAll('a[href]').length <= 3) tile.style.display = 'none';
              a.style.display = 'none';
            });
          }
          let pending = false;
          new MutationObserver(() => {
            if (!pending) { pending = true; setTimeout(() => { pending = false; clean(); }, 250); }
          }).observe(document.documentElement, {childList:true,subtree:true});
          document.addEventListener('click', e => {
            const a = e.target.closest && e.target.closest('a[href]');
            if (a && isReel(a.href)) { e.preventDefault(); e.stopPropagation(); }
          }, true);
          clean();
        })();
        """;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        web = new WebView(this);
        setContentView(web);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(FILTER_JS, null);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String path = uri.getPath();
                String hostCheck = uri.getHost();
                if (hostCheck != null && (hostCheck.equals("instagram.com") || hostCheck.endsWith(".instagram.com"))
                        && path != null && path.matches("(?i)^/reels?(/.*)?$")) return true;
                String scheme = uri.getScheme();
                if ("https".equals(scheme) || "http".equals(scheme)) {
                    String host = uri.getHost();
                    if (host != null && (host.equals("instagram.com") || host.endsWith(".instagram.com") || host.equals("facebook.com") || host.endsWith(".facebook.com"))) return false;
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                }
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) {}
                return true;
            }
        });
        if (state != null) web.restoreState(state); else web.loadUrl(HOME);
        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, () -> {
            if (web.canGoBack()) web.goBack(); else finish();
        });
    }
    @Override protected void onSaveInstanceState(Bundle out) { web.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onPause() { CookieManager.getInstance().flush(); super.onPause(); }
    @Override protected void onDestroy() { web.destroy(); super.onDestroy(); }
}
