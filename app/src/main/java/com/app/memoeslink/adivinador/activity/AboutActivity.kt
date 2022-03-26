package com.app.memoeslink.adivinador.activity

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import com.app.memoeslink.adivinador.BuildConfig
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.TextFormatter
import com.memoeslink.generator.common.DateTimeHelper

class AboutActivity : CommonActivity() {
    private var tvContent: TextView? = null
    private var btBack: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        tvContent = findViewById(R.id.about_content)
        tvContent?.isClickable = true
        tvContent?.text = TextFormatter.fromHtml(
            getString(
                R.string.about,
                getString(R.string.app_name),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                DateTimeHelper.getCurrentDate().year
            )
        )
        tvContent?.movementMethod = LinkMovementMethod.getInstance()
        btBack = findViewById(R.id.about_back_button)

        //Set listeners
        btBack?.setOnClickListener { finish() }
    }
}