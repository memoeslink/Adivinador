package com.app.memoeslink.adivinador;

import android.text.Html;
import android.text.Spanned;

public class SpannerHelper {

    public static Spanned fromHtml(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }
}
