package com.app.memoeslink.adivinador

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView

class AboutActivity : CommonActivity() {
    private var tvContent: TextView? = null
    private var btBack: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        tvContent = findViewById(R.id.about_content)
        tvContent?.isClickable = true
        tvContent?.text = TextFormatter.fromHtml(getString(R.string.about))
        tvContent?.movementMethod = LinkMovementMethod.getInstance()
        btBack = findViewById(R.id.about_back_button)

        //Set listeners
        btBack?.setOnClickListener { finish() }
    }
}