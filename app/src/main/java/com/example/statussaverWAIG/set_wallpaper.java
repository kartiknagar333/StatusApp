package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class set_wallpaper extends AppCompatActivity {
    private ImageView showimg;
    private FloatingActionButton btncheck;
    private int screenWidth,screenHeight;
    private static SharedPreferences ad = null;
    private ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (ad.getInt("status_bar_height",0) > 0) {
            final LinearLayout set = findViewById(R.id.setbtn);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(0, ad.getInt("status_bar_height",0), 0, 0);
            set.setLayoutParams(params1);
        }

        final Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String path = bundle.getString("image_path");
        showimg = findViewById(R.id.wallpaper);
        pbar = findViewById(R.id.progressbar);
        btncheck = findViewById(R.id.check);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        if(bundle.getBoolean("IsUri")){
            Picasso.get().load(new File(path)).into(showimg, new Callback() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess() {
                    btncheck.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(path).into(showimg, new Callback() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onSuccess() {
                            btncheck.setVisibility(View.VISIBLE);
                            pbar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError(Exception e) {
                            pbar.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }else{
            Picasso.get().load(path).into(showimg, new Callback() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess() {
                    btncheck.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                    pbar.setVisibility(View.GONE);
                }
            });
        }

        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setwallpaper().execute();
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private class setwallpaper extends AsyncTask<Void, Void, Void> {
        @SuppressLint("RestrictedApi")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
            btncheck.setVisibility(View.GONE);
        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... params) {
            @SuppressLint("WrongThread")
            Bitmap bm = ((BitmapDrawable)showimg.getDrawable()).getBitmap();
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(set_wallpaper.this);
            wallpaperManager.setWallpaperOffsetSteps(1, 1);
            wallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight);
            try {
                wallpaperManager.setBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            Toast.makeText(set_wallpaper.this, "Done", Toast.LENGTH_SHORT).show();
        }
    }
}
