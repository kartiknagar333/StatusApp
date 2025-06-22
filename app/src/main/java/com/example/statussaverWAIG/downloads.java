package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;

public class downloads extends AppCompatActivity {
    private RecyclerView imageRecycleView;
    private TextView mtv;
    private static SharedPreferences ad = null;
    private ImageView clear,mimg,back;
    private com.example.statussaverWAIG.WhatsAppStatusAdapter imgAdapter = null;
    private ArrayList<com.example.statussaverWAIG.image_model> ImgList;
    private static com.example.statussaverWAIG.Database myDB;
    private com.example.statussaverWAIG.image_model IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        final Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ad = getSharedPreferences("app_details", MODE_PRIVATE);
        myDB = new com.example.statussaverWAIG.Database(this);
        if (ad.getInt("status_bar_height",0) > 0) {
            final LinearLayout heading = findViewById(R.id.topbanner);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(0, ad.getInt("status_bar_height",0), 0, 0);
            heading.setLayoutParams(params1);
        }
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        imageRecycleView = findViewById(R.id.image_recycleview);
        mtv = findViewById(R.id.msg_img);
        mimg = findViewById(R.id.noimg_image);
        clear = findViewById(R.id.removedownloads);
        back = findViewById(R.id.backactivity);
        ImgList = new ArrayList<>();
        imageRecycleView.setHasFixedSize(true);
        imageRecycleView.scheduleLayoutAnimation();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispdialog();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetGalleryData();
                refreshLayout.setRefreshing(false);
            }
        });
        GetGalleryData();
    }
    private void dispdialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are You Sure");
        builder.setMessage("Delete All Downloads Items?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mtv.getVisibility()==View.VISIBLE){
                            Toast.makeText(downloads.this, "No Items Yet", Toast.LENGTH_SHORT).show();
                        }else{
                            myDB.detelegallery();
                            new cleargallery().execute();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void GetGalleryData(){
        ImgList = new ArrayList<>();
        final Cursor cursor = myDB.get_gallery();
        if(cursor.getCount()!=0) {
            clear.setVisibility(View.VISIBLE);
            mimg.setVisibility(View.GONE);
            mtv.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                File f = new File(cursor.getString(1));
                if(f.exists()){
                    if(f.getName().endsWith(".jpg")){
                        IMG = new com.example.statussaverWAIG.image_model(cursor.getString(1),String.valueOf(cursor.getInt(0)),false);
                    }else if(f.getName().endsWith(".gif") || f.getName().endsWith(".mp4")){
                        IMG = new com.example.statussaverWAIG.image_model(cursor.getString(1),String.valueOf(cursor.getInt(0)),true);
                    }
                    ImgList.add(IMG);
                }else{
                  myDB.delete_gallery(cursor.getString(0));
                }
            }
            imageRecycleView.setLayoutManager(new GridLayoutManager(this,3));
            imgAdapter = new com.example.statussaverWAIG.WhatsAppStatusAdapter(ImgList,this,true);
            imageRecycleView.setAdapter(imgAdapter);
            imgAdapter.notifyDataSetChanged();
            imageRecycleView.setVisibility(View.VISIBLE);
            imageRecycleView.scheduleLayoutAnimation();
        }else{
            clear.setVisibility(View.GONE);
            imageRecycleView.setVisibility(View.GONE);
            mimg.setVisibility(View.VISIBLE);
            mtv.setVisibility(View.VISIBLE);
            mtv.setText("No Items Yet\nAll the items you've downloaded will show up here.");
        }
    }
    private class cleargallery extends AsyncTask<Void, Void, Void> {
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                for(com.example.statussaverWAIG.image_model IMG  : ImgList){
                    File f = new File(IMG.getoriginal());
                    if(f.exists()){
                        f.delete();
                    }
                }
            }catch (Exception ignored){ }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ImgList.clear();
            imgAdapter.notifyDataSetChanged();
            clear.setVisibility(View.GONE);
            imageRecycleView.setVisibility(View.GONE);
            mimg.setVisibility(View.VISIBLE);
            mtv.setVisibility(View.VISIBLE);
            mtv.setText("No Items Yet\nAll the items you've downloaded will show up here.");
        }
    }
}