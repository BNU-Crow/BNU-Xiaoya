package com.xuhongxu.xiaoya.Activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhongxu.xiaoya.R;

public class ErrorActivity extends Activity {

    String errorText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Intent intent = getIntent();
        TextView errorTextView = (TextView) findViewById(R.id.errorText);

        if (intent != null) {
            errorText = intent.getStringExtra("Error");
            errorTextView.setText(errorText);
        }
    }

    void copyError(View view) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error", errorText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                .show();
    }
}
