@file:JvmName("StringExtensions")

package com.app.memoeslink.adivinador.extensions

import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.URLUtil
import java.util.regex.Pattern

const val DEFAULT_URL = "https:192.168.0.1"

fun String?.toHtmlText(): Spanned {
    return if (this.isNullOrBlank()) SpannableString("")
    else Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
}

fun String?.toLinkedHtmlText(): Spanned {
    return if (this.isNullOrBlank()) SpannableString("")
    else Html.fromHtml(
        "<a href=\\\"$DEFAULT_URL\\\">$this</a>", Html.FROM_HTML_MODE_LEGACY
    )
}

fun String?.toClickableText(
      pair: Pair<String, () -> Unit>? = Pair("$DEFAULT_URL/action") {}
): SpannableStringBuilder {
    return if (this.isNullOrBlank()) SpannableStringBuilder("")
    else {
        val sequence: CharSequence = this.toHtmlText()
        val sb = SpannableStringBuilder(sequence)
        val p = Pattern.compile("<a[^>]+(\\s+href=\"(.*?)\"[^>]*)?>[\\p{IsLatin}\\w\\s]*</a>")
        val m = p.matcher(sb)

        while (m.find()) {
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    when {
                        m.group(2) == "$DEFAULT_URL/${pair?.first}" -> pair?.second
                        URLUtil.isValidUrl(m.group(2)) -> pair?.second
                        else -> {}
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            sb.setSpan(
                clickableSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        sb
    }
}