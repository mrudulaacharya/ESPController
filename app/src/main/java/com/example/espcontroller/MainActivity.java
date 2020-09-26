package com.example.espcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "com.example.espcontroller.example.EXTRA_URL";

    EditText urlText;
    Button buttonStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStats = findViewById(R.id.buttonStats);
        urlText = findViewById(R.id.urlText);

    }

    public void getStatScreen(View view) {
        String url = urlText.getText().toString();
        if (url.equals("")) {
            Toast.makeText(this, "Enter URL to proceed", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra(EXTRA_URL, url);
            startActivity(intent);
        }

    }

}
