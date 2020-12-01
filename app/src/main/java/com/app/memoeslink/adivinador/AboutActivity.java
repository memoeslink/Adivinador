package com.app.memoeslink.adivinador;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends CommonActivity {
    private TextView content;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        content = findViewById(R.id.about_content);
        content.setClickable(true);
        content.setText(Methods.fromHtml(getString(R.string.about)));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        button = findViewById(R.id.about_back_button);

        //Set listeners
        button.setOnClickListener(view -> finish());
    }
}
