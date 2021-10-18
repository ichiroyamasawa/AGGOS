package com.example.aggos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FAQActivity extends AppCompatActivity {

    private Button backBtn;
    private Button understoodBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        backBtn = findViewById(R.id.btnFAQBack);
        understoodBtn = findViewById(R.id.btnFAQUnderstood);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });

        understoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });

    }

    public void openMainPage(){
        Intent intent = new Intent(FAQActivity.this ,MainActivity.class);
        startActivity(intent);
        finish();
    }
}