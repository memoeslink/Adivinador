@file:JvmName("StringExtensions")

package com.app.memoeslink.adivinador.extensions

import android.text.Html
import android.text.SpannableString
import android.text.Spanned

const val DEFAULT_URL = "https://192.168.0.1"

fun String?.toHtmlText(): Spanned {
    return if (this.isNullOrBlank()) SpannableString("")
    else Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
}

fun String?.toLinkedHtmlText(): Spanned {
    return if (this.isNullOrBlank()) SpannableString("")
    else Html.fromHtml(
        "<a href=\"$DEFAULT_URL\">$this</a>", Html.FROM_HTML_MODE_LEGACY
    )
}