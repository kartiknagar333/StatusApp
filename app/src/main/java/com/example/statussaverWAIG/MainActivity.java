package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends AppCompatActivity {
    private static SharedPreferences ad = null;
    private static Database myDB;
    private int time = 1000;
    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);
        if(ad.getBoolean("enterbyabout",false)){
            ad.edit().putBoolean("enterbyabout",false).commit();
        }else{
            if(!ad.getBoolean("2",false)){
                ad.edit().putBoolean("2",true).commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                myDB = new Database(this);
                myDB.clearData();
                time = 2000;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this,home.class));
                    finish();
                }
            }, time);
        }
    }
}
