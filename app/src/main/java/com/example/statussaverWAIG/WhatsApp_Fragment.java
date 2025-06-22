package com.example.statussaverWAIG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;


public class WhatsApp_Fragment extends Fragment {
    private static final String[] permissons = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_RUNTIME_PERMISSION = 666;
    private WhatsAppStatusAdapter WASAdapter = null,adpter;
    private RecyclerView imageRecycleView;
    private TextView mtv;
    private ArrayList<image_model> img= new ArrayList<>(),video= new ArrayList<>();
    private static SharedPreferences ad = null;
    private TextView ptv,vtv,ctv;
    private image_model IMG;
    private static final String NEW_WHATSAPP_STATUS_URI = "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
    private static final String NEW_WHATSAPPB_STATUS_URI = "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses";
    private static final String WHATSAPP_STATUS_URI = "/WhatsApp/Media/.Statuses";
    private static final String WHATSAPPB_STATUS_URI = "/WhatsApp Business/Media/.Statuses";
    private static final String dpath = "/storage/emulated/0/";
    private ImageView mimg;
    private boolean isvideoType = false,WNON = false;
    private FloatingActionButton imgbutton,videobutton,chatbutton;
    private SwipeRefreshLayout refreshLayout;
    private int flag = 0;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.whatsapp_fragment_activity, viewGroup, false);
        ad = getContext().getSharedPreferences("app_details", 0);

        imgbutton = v.findViewById(R.id.imgbutton);
        videobutton = v.findViewById(R.id.videobutton);
        chatbutton = v.findViewById(R.id.chat);
        imgbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkred)));
        imageRecycleView = v.findViewById(R.id.image_recycleview);
        mtv = v.findViewById(R.id.msg_img);
        mimg = v.findViewById(R.id.noimg_image);
        ptv = v.findViewById(R.id.ptv);
        vtv = v.findViewById(R.id.vtv);
        ctv = v.findViewById(R.id.ctv);
        refreshLayout = v.findViewById(R.id.refresh);
        imageRecycleView.setHasFixedSize(true);
        imageRecycleView.scheduleLayoutAnimation();

        if(30 > Build.VERSION.SDK_INT){
            if (CheckPermission(getContext(), permissons[0])) {
                refreshLayout.setEnabled(true);
                new getstatuprocess().execute();
            }else {
                refreshLayout.setEnabled(false);
                mtv.setText("Click Me & Allow The Permission & Save WhatsApp Status");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Without Your Permission You Can not Save Status.");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestPermission(getActivity(), permissons, REQUEST_RUNTIME_PERMISSION);
                    }
                });
                builder.show();
            }
        }
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isvideoType = false;
                imgbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkred)));
                videobutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blackGrey)));
                ptv.setTextColor(getResources().getColor(R.color.darkred));
                vtv.setTextColor(getResources().getColor(R.color.white));
                if (30 <= Build.VERSION.SDK_INT) {
                    if(WNON){
                        mtv.setVisibility(View.VISIBLE);
                        mtv.setText("Does Not Find Any WhatsApp Status");
                        mimg.setVisibility(View.VISIBLE);
                        imageRecycleView.setVisibility(View.GONE);
                    }else{
                        new getstatuprocessAPI30().execute();
                    }
                }else {
                    new getstatuprocess().execute();
                }
            }
        });

        videobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isvideoType = true;
                imgbutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blackGrey)));
                videobutton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkred)));
                ptv.setTextColor(getResources().getColor(R.color.white));
                vtv.setTextColor(getResources().getColor(R.color.darkred));
                if (30 <= Build.VERSION.SDK_INT) {
                    if(WNON){
                        mtv.setVisibility(View.VISIBLE);
                        mtv.setText("Does Not Find Any WhatsApp Status");
                        mimg.setVisibility(View.VISIBLE);
                        imageRecycleView.setVisibility(View.GONE);
                    }else{
                        new getstatuprocessAPI30().execute();
                    }
                }else{
                    new getstatuprocess().execute();
                }

            }
        });

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), com.example.statussaverWAIG.OpenChat.class));
            }
        });


        mtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermission(getContext(), permissons[0])) {
                    refreshLayout.setEnabled(true);
                    new getstatuprocess().execute();
                }else {
                    refreshLayout.setEnabled(false);
                    mtv.setText("Click Me & Allow The Permission & Save WhatsApp Status");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Without Your Permission You Can not Save Status.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestPermission(getActivity(), permissons, REQUEST_RUNTIME_PERMISSION);
                        }
                    });
                    builder.show();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (30 <= Build.VERSION.SDK_INT) {
                    if(WNON){
                        mtv.setVisibility(View.VISIBLE);
                        mtv.setText("Does Not Find Any WhatsApp Status");
                        mimg.setVisibility(View.VISIBLE);
                        imageRecycleView.setVisibility(View.GONE);
                    }else{
                        new getstatuprocessAPI30().execute();
                        refreshLayout.setRefreshing(false);
                    }
                }else{
                    new getstatuprocess().execute();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        return v;
    }
    private class getstatuprocess extends AsyncTask<Void, Void, Void> {
        boolean exists = false,vexists = false;

        @Override
        protected void onPreExecute() {
            mtv.setVisibility(View.VISIBLE);
            mtv.setText("Loading...");
            mimg.setVisibility(View.VISIBLE);
            imageRecycleView.setVisibility(View.GONE);

            img = new ArrayList<>();
            video = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            File[] files = new File(
                    Environment.getExternalStorageDirectory().toString()+NEW_WHATSAPP_STATUS_URI).listFiles();
            if(files!=null){
                for (File file : files){
                    if(file.getName().endsWith(".jpg")){
                        exists = true;
                        Log.d("image", "doInBackground: "+file.getAbsolutePath());
                        IMG = new image_model(file.getAbsolutePath(),false);
                        img.add(IMG);
                    }else if(file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")){
                        vexists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),true);
                        video.add(IMG);
                    }
                }
            }
            files = new File(Environment.getExternalStorageDirectory().toString()+NEW_WHATSAPPB_STATUS_URI).listFiles();
            if(files!=null){
                for (File file : files){
                    if(file.getName().endsWith(".jpg")){
                        exists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),false);
                        img.add(IMG);
                    }else if(file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")){
                        vexists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),true);
                        video.add(IMG);
                    }
                }
            }
            files = new File(Environment.getExternalStorageDirectory().toString()+WHATSAPP_STATUS_URI).listFiles();
            if(files!=null){
                for (File file : files){
                    if(file.getName().endsWith(".jpg")){
                        exists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),false);
                        img.add(IMG);
                    }else if(file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")){
                        vexists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),true);
                        video.add(IMG);
                    }
                }
            }
            files = new File(Environment.getExternalStorageDirectory().toString()+WHATSAPPB_STATUS_URI).listFiles();
            if(files!=null){
                for (File file : files){
                    if(file.getName().endsWith(".jpg")){
                        exists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),false);
                        img.add(IMG);
                    }else if(file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")){
                        vexists = true;
                        IMG = new com.example.statussaverWAIG.image_model(file.getAbsolutePath(),true);
                        video.add(IMG);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isvideoType){
                if(vexists){
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
                    WASAdapter = new com.example.statussaverWAIG.WhatsAppStatusAdapter(video,getContext(),false);
                    imageRecycleView.setLayoutManager(new GridLayoutManager(getContext(),3));
                    imageRecycleView.setAdapter(WASAdapter);
                    WASAdapter.notifyDataSetChanged();
                    imageRecycleView.setVisibility(View.VISIBLE);
                    imageRecycleView.scheduleLayoutAnimation();
                }else{
                    imageRecycleView.setVisibility(View.GONE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setVisibility(View.VISIBLE);
                    mtv.setText("Does Not Find Any WhatsApp Video Status");
                }
            }else{
                if(exists){
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
                    WASAdapter = new com.example.statussaverWAIG.WhatsAppStatusAdapter(img,getContext(),false);
                    imageRecycleView.setLayoutManager(new GridLayoutManager(getContext(),3));
                    imageRecycleView.setAdapter(WASAdapter);
                    WASAdapter.notifyDataSetChanged();
                    imageRecycleView.setVisibility(View.VISIBLE);
                    imageRecycleView.scheduleLayoutAnimation();
                }else{
                    imageRecycleView.setVisibility(View.GONE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setVisibility(View.VISIBLE);
                    mtv.setText("Does Not Find Any WhatsApp Photo Status");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_RUNTIME_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (30 > Build.VERSION.SDK_INT) {
                        new getstatuprocess().execute();
                        refreshLayout.setEnabled(true);
                    }
                }else{
                    if (30 <= Build.VERSION.SDK_INT) {
                        if (!com.example.statussaverWAIG.PermissionUtils.neverAskAgainSelected(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Click Permissions->Storage->Allow All Files");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            builder.show();
                        }else{
                            RequestPermission(getActivity(), permissons, REQUEST_RUNTIME_PERMISSION);
                        }
                    }else{
                        refreshLayout.setEnabled(false);
                        mtv.setText("Click Me & Allow The Permission & Save WhatsApp Status");
                        if (!com.example.statussaverWAIG.PermissionUtils.neverAskAgainSelected(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Click Permissions->Storage->Allow All Files");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            builder.show();
                        }else{
                            RequestPermission(getActivity(), permissons, REQUEST_RUNTIME_PERMISSION);
                        }
                    }
                }
                return;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void RequestPermission(Activity thisActivity, String[] Permission, int Code) {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RUNTIME_PERMISSION);
    }
    public boolean CheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onStart() {
        super.onStart();
        if (30 <= Build.VERSION.SDK_INT) {
            if(new File(Environment.getExternalStorageDirectory() + NEW_WHATSAPP_STATUS_URI).exists()){
                if(ad.getBoolean("NW",false)){
                    getContext().getContentResolver().takePersistableUriPermission(Uri.parse(ad.getString("NW_U","")),Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getpermission2();
                }else{
                    dispdialog("WhatsApp",1);
                }
            }else {
                getpermission2();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getpermission2(){
        if(new File(Environment.getExternalStorageDirectory() + NEW_WHATSAPPB_STATUS_URI).exists()){
            if(ad.getBoolean("NWB",false)){
                getContext().getContentResolver().takePersistableUriPermission(Uri.parse(ad.getString("NWB_U","")),Intent.FLAG_GRANT_READ_URI_PERMISSION);
                new getstatuprocessAPI30().execute();
            }else{
                dispdialog("WhatsApp Business",2);
            }
        }else{
            if(!ad.getBoolean("NWB", false) && !ad.getBoolean("NW", false)){
                getpermission3();
            }else{
                new getstatuprocessAPI30().execute();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getpermission3(){
        if(new File(Environment.getExternalStorageDirectory() + WHATSAPP_STATUS_URI).exists()){
            if(ad.getBoolean("OW",false)){
                getContext().getContentResolver().takePersistableUriPermission(Uri.parse(ad.getString("OW_U","")),Intent.FLAG_GRANT_READ_URI_PERMISSION);
                new getstatuprocessAPI30().execute();
            }else{
                dispdialog("WhatsApp",3);
            }
        }else{
            WNON = true;
        }
    }

    private void dispdialog(String s,int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(s);
        builder.setMessage("Without Your Permission,You Cannot Get status.\n1. Click [USE THIS FOLDER] BUTTON\n2. Click [ALLOW] BUTTON");
        builder.setCancelable(false);
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(i==1){
                    openDirectory("primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses",1);
                }else if(i==2){
                    openDirectory("primary:Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses",2);
                }else{
                    openDirectory("primary:WhatsApp/Media/.Statuses",3);
                }
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openDirectory(String std,int rc) {
        try {
            startActivityForResult((new Intent("android.intent.action.OPEN_DOCUMENT_TREE"))
                            .putExtra("android.provider.extra.INITIAL_URI", (Parcelable) DocumentsContract
                                    .buildDocumentUri("com.android.externalstorage.documents",
                                            std)), rc);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == 1) {
            getContext().getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ad.edit().putBoolean("NW",true).commit();
            ad.edit().putString("NW_U", String.valueOf(data.getData())).commit();
        }else if(requestCode == 2){
            getContext().getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ad.edit().putBoolean("NWB",true).commit();
            ad.edit().putString("NWB_U", String.valueOf(data.getData())).commit();
        }else if (requestCode == 3){
            getContext().getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ad.edit().putBoolean("OW",true).commit();
            ad.edit().putString("OW_U", String.valueOf(data.getData())).commit();
        }
    }
    private class getstatuprocessAPI30 extends AsyncTask<Void, Void, Void> {
        boolean exists = false,vexists = false;

        @Override
        protected void onPreExecute() {
            mtv.setVisibility(View.VISIBLE);
            mtv.setText("Loading...");
            mimg.setVisibility(View.VISIBLE);
            imageRecycleView.setVisibility(View.GONE);

            img = new ArrayList<>();
            video = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(ad.getBoolean("NW",false)){
                DocumentFile fromTreeUri = DocumentFile.fromTreeUri(getContext(), Uri.parse(ad.getString("NW_U","")));
                DocumentFile[] documentFiles = fromTreeUri.listFiles();
                for (int i = 0; i < documentFiles.length; i++) {
                    if(documentFiles[i].getUri().getPath().endsWith(".jpg")){
                        exists = true;
                        IMG = new image_model(documentFiles[i].getUri().toString(),false);
                        img.add(IMG);
                    }else if(  documentFiles[i].getUri().getPath().endsWith(".gif") || documentFiles[i].getUri().getPath().endsWith(".mp4")){
                        vexists = true;
                        IMG = new image_model(documentFiles[i].getUri().toString(),true);
                        video.add(IMG);
                    }
                }
            }
            if(ad.getBoolean("NWB",false)){
                DocumentFile fromTreeUri3 = DocumentFile.fromTreeUri(getContext(), Uri.parse(ad.getString("NWB_U","")));
                DocumentFile[] documentFiles3 = fromTreeUri3.listFiles();
                for (int i = 0; i < documentFiles3.length; i++) {
                    if(documentFiles3[i].getUri().getPath().endsWith(".jpg")){
                        exists = true;
                        IMG = new image_model(documentFiles3[i].getUri().toString(),false);
                        img.add(IMG);
                    }else if(  documentFiles3[i].getUri().getPath().endsWith(".gif") || documentFiles3[i].getUri().getPath().endsWith(".mp4")){
                        vexists = true;
                        IMG = new image_model(documentFiles3[i].getUri().toString(),true);
                        video.add(IMG);
                    }
                }
            }
            if(ad.getBoolean("OW",false)){
                DocumentFile fromTreeUri5 = DocumentFile.fromTreeUri(getContext(), Uri.parse(ad.getString("OW_U","")));
                DocumentFile[] documentFiles5 = fromTreeUri5.listFiles();
                for (int i = 0; i < documentFiles5.length; i++) {
                    if(documentFiles5[i].getUri().getPath().endsWith(".jpg")){
                        exists = true;
                        IMG = new image_model(documentFiles5[i].getUri().toString(),false);
                        img.add(IMG);
                    }else if(  documentFiles5[i].getUri().getPath().endsWith(".gif") ||   documentFiles5[i].getUri().getPath().endsWith(".mp4")){
                        vexists = true;
                        IMG = new image_model(documentFiles5[i].getUri().toString(),true);
                        video.add(IMG);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isvideoType){
                if(vexists){
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
                    WASAdapter = new com.example.statussaverWAIG.WhatsAppStatusAdapter(video,getContext(),false);
                    imageRecycleView.setLayoutManager(new GridLayoutManager(getContext(),3));
                    imageRecycleView.setAdapter(WASAdapter);
                    WASAdapter.notifyDataSetChanged();
                    imageRecycleView.setVisibility(View.VISIBLE);
                    imageRecycleView.scheduleLayoutAnimation();
                }else{
                    imageRecycleView.setVisibility(View.GONE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setVisibility(View.VISIBLE);
                    mtv.setText("Does Not Find Any WhatsApp Video Status");
                }
            }else{
                if(exists){
                    mtv.setVisibility(View.GONE);
                    mimg.setVisibility(View.GONE);
                    WASAdapter = new com.example.statussaverWAIG.WhatsAppStatusAdapter(img,getContext(),false);
                    imageRecycleView.setLayoutManager(new GridLayoutManager(getContext(),3));
                    imageRecycleView.setAdapter(WASAdapter);
                    WASAdapter.notifyDataSetChanged();
                    imageRecycleView.setVisibility(View.VISIBLE);
                    imageRecycleView.scheduleLayoutAnimation();
                }else{
                    imageRecycleView.setVisibility(View.GONE);
                    mimg.setVisibility(View.VISIBLE);
                    mtv.setVisibility(View.VISIBLE);
                    mtv.setText("Does Not Find Any WhatsApp Photo Status");
                }
            }
        }
    }

}