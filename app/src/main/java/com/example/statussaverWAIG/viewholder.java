package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class viewholder extends RecyclerView.Adapter<viewholder.videoviewholder> {
    private static SharedPreferences ad = null;
    private static Database myDB;
    private static BroadcastReceiver receiver;
    private static int type = 0;
    private static boolean downloaded = false;
    private static DownloadManager mgr;
    private static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Environment.DIRECTORY_DOWNLOADS+"/";
    private ArrayList<image_model> list;
    private static Context context;
    public viewholder(Context context,ArrayList<image_model> list,final int type,final boolean downloaded) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.downloaded = downloaded;
        myDB =  new Database(context);
    }

    @NonNull
    @Override
    public videoviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final videoviewholder vvh =  new videoviewholder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.fragment_one,parent,false
                )
        );
        return vvh;
    }

    @Override
    public void onBindViewHolder(@NonNull videoviewholder holder, @SuppressLint("RecyclerView") int position) {
        if(type==1){
            holder.perform1type(list.get(position));
        }else if(type==2){
            holder.perform2type(list.get(position));
        }else{
            if(type==6){
                holder.perform6type(list.get(position));
            }else{
                holder.pbar.setVisibility(View.VISIBLE);
                holder.path_url = list.get(position).getImagerc(type);
                holder.download_url = holder.path_url[0][1];
                holder.setUs.setVisibility(View.VISIBLE);
                Picasso.get().load(holder.download_url.trim()).into(holder.showimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.showimg.setVisibility(View.VISIBLE);
                        holder.pbar.setVisibility(View.GONE);
                        holder.stp.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.stp.setVisibility(View.GONE);
                        holder.pbar.setVisibility(View.GONE);
                    }
                });
            }
            holder.GetSize();
        }

        if(holder.path_url.length>1){
            holder.quality_btn.setVisibility(View.VISIBLE);
        }
        holder.quality_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==2 || type==6){
                    holder.showpopMenu(view,1,list.get(position).getVideosrc());
                } else{
                    holder.showpopMenu(view,1,list.get(position).getImagerc(type));
                }
            }
        });

        holder.download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==1){
                    holder.pbar.setVisibility(View.VISIBLE);
                    if(list.get(position).isVideo()){
                        holder.DWA(list.get(position).getoriginal(),true);
                    }else{
                        holder.DWA(list.get(position).getoriginal(),false);
                    }
                }else if(type==2){
                    if(list.get(position).isVideo()){
                        if(holder.minip.getVisibility()==View.VISIBLE){
                            Toast.makeText(context, "Already Downloaded.", Toast.LENGTH_SHORT).show();
                        }else{
                            holder.minip.setVisibility(View.VISIBLE);
                            holder.download_flag = 0;
                            holder.downloadfile(holder.download_url,true);
                        }
                    }else{
                        holder.showpopMenu(view,0,list.get(position).getVideosrc());
                    }
                }else{
                    if(holder.path_url.length>1){
                        if(type==6){
                            holder.showpopMenu(view,0,list.get(position).getVideosrc());
                        } else{
                            holder.showpopMenu(view,0,list.get(position).getImagerc(type));
                        }
                    }else{
                        holder.downloadfile(list.get(position).getoriginal(),false);
                    }
                }
            }
        });

        holder.whatsapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==1){
                    holder.shareWhatsApp(holder.download_url);
                }else if(type==2){
                    if(list.get(position).isVideo()){
                        if(holder.minip.getVisibility()==View.GONE){
                            if(holder.IsDownload){
                                holder.shareWhatsApp(STORAGE_PATH+holder.id+".mp4");
                            }else{
                                holder.minip.setVisibility(View.VISIBLE);
                                holder.IsDownload = false;
                                holder.download_flag = 1;
                                holder.downloadfile(holder.download_url,true);
                            }
                        }else{
                            Toast.makeText(context, "After Download You Can Share", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        holder.shareanyapp(true);
                    }
                }else{
                    holder.shareanyapp(true);
                }
            }
        });
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==1){
                    holder.sharevideo(list.get(position).getoriginal());
                }else if(type==2){
                    if(list.get(position).isVideo()){
                        if(holder.minip.getVisibility()==View.GONE){
                            if(holder.IsDownload){
                                holder.sharevideo(STORAGE_PATH+holder.id+".mp4");
                            }else{
                                holder.minip.setVisibility(View.VISIBLE);
                                holder.IsDownload = false;
                                holder.download_flag = 2;
                                holder.downloadfile(holder.download_url,true);
                            }
                        }else{
                            Toast.makeText(context, "After Download You Can Share", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        holder.shareanyapp(false);
                    }
                }else{
                    holder.shareanyapp(false);
                }
            }
        });
        holder.setUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==1){
                    Intent summaryIntent = new Intent(context, set_wallpaper.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("image_path",list.get(position).getoriginal());
                    bundle.putBoolean("IsUri",true);
                    summaryIntent.putExtras(bundle);
                    context.startActivity(summaryIntent);
                }else{
                    if(type==2){
                        holder.showpopMenu(view,2,list.get(position).getVideosrc());
                    } else{
                        holder.showpopMenu(view,2,list.get(position).getImagerc(type));
                    }
                }
            }
        });
        if(type==4){
            if(position==list.size()-3){
                ((slidepager)context).getUnsplashImage();
            }
        }else if(type==5){
            if(position==list.size()-3){
                ((slidepager)context).getPexelsImage();
            }
        }else if(type==6){
            if(position==list.size()-3){
                ((slidepager)context).getPexelsVideo();
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class videoviewholder extends RecyclerView.ViewHolder{
        private VideoView videoView;
        private String download_url,id;
        private ProgressBar pbar;
        private List<String> categories;
        private String [][] path_url;
        private int download_flag=0;
        private boolean ISfirst = false,IsDownload = false,down = false;
        private ImageView showimg;
        private ImageView download_btn,share_btn,whatsapp_btn,quality_btn,setUs;
        private TextView sizetv;
        private String size,destinationPath;
        private int r = 0;
        private long enqueue;
        private ProgressBar minip;
        private ProgressBar stp;

        public videoviewholder(@NonNull View itemView) {
            super(itemView);
            pbar = itemView.findViewById(R.id.progressbar);
            videoView = itemView.findViewById(R.id.videoview);
            showimg = itemView.findViewById(R.id.showimg);
            download_btn = itemView.findViewById(R.id.downloadbtn);
            share_btn = itemView.findViewById(R.id.shareitem);
            whatsapp_btn = itemView.findViewById(R.id.whatappshare);
            quality_btn = itemView.findViewById(R.id.changequality);
            sizetv = itemView.findViewById(R.id.sizetv);
            setUs = itemView.findViewById(R.id.setus);
            minip = itemView.findViewById(R.id.mini_pgb);
            stp = itemView.findViewById(R.id.startingprogressbar);
            if(type==2 || type==6){
                stp.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
            }else if(type==1){
                if(downloaded){
                    download_btn.setVisibility(View.GONE);
                }
            }
        }
        MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        stp.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        pbar.setVisibility(View.GONE);
                        stp.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        };
        private void perform1type(final image_model im){
            path_url = new String[1][2];
            path_url[0][0] = "";
            path_url[0][1] =  im.getoriginal();
            download_url = im.getoriginal();
            if(im.isVideo()){
                showimg.setVisibility(View.GONE);
                setUs.setVisibility(View.GONE);
                MediaController mediacontroller  =  new MediaController(context);
                mediacontroller.setAnchorView(videoView);
                videoView.setMediaController(mediacontroller);
                final Uri uri = Uri.parse(download_url.trim());
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        pbar.setVisibility(View.GONE);
                        mediaPlayer.start();
                        videoView.setVisibility(View.VISIBLE);
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        pbar.setVisibility(View.GONE);
                        mediaPlayer.start();
                        videoView.setVisibility(View.VISIBLE);
                    }
                });
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        return false;
                    }
                });
                showimg.setVisibility(View.GONE);
            }else{
                videoView.setVisibility(View.GONE);
                setUs.setVisibility(View.VISIBLE);

                Picasso.get().load(download_url.trim()).into(showimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        showimg.setVisibility(View.VISIBLE);
                        pbar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(new File(download_url.trim())).into(showimg, new Callback() {
                            @Override
                            public void onSuccess() {
                                showimg.setVisibility(View.VISIBLE);
                                pbar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(Exception e) {
                                pbar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
            getstatussize(download_url);
        }
        private void perform2type(image_model list){
            path_url =  list.getVideosrc();
            download_url = path_url[0][1];
            id = list.getID();
            if(list.isVideo()){
                stp.setVisibility(View.VISIBLE);
                MediaController mediacontroller  =  new MediaController(context);
                mediacontroller.setAnchorView(videoView);
                videoView.setMediaController(mediacontroller);
                videoView.setOnInfoListener(onInfoToPlayStateListener);
                whatsapp_btn.setVisibility(View.VISIBLE);
                share_btn.setVisibility(View.VISIBLE);
                File f = new File(STORAGE_PATH+id+".mp4");
                if(f.exists()){
                    download_btn.setVisibility(View.GONE);
                    IsDownload = true;
                    final Uri uri = Uri.parse(f.getAbsolutePath());
                    videoView.setVideoURI(uri);
                }else{
                    IsDownload = false;
                    videoView.setVideoPath(download_url);
                }
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        stp.setVisibility(View.GONE);
                        pbar.setVisibility(View.GONE);
                        videoView.start();
                        mediaPlayer.start();
                        float videoratio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                        float screenratio = videoView.getWidth() / (float) videoView.getHeight();
                        float scale = videoratio / screenratio;
                        if(scale >= 1f){
                            videoView.setScaleX(scale);
                        }else{
                            videoView.setScaleY(1f/scale);
                        }
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stp.setVisibility(View.GONE);
                        mediaPlayer.start();
                    }
                });
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        return false;
                    }
                });
                showimg.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                setUs.setVisibility(View.GONE);
                quality_btn.setVisibility(View.GONE);
            }else{
                videoView.setVisibility(View.GONE);
                setUs.setVisibility(View.VISIBLE);
                Picasso.get().load(download_url.trim()).into(showimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        showimg.setVisibility(View.VISIBLE);
                        pbar.setVisibility(View.GONE);
                        stp.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        pbar.setVisibility(View.GONE);
                        stp.setVisibility(View.GONE);
                    }
                });
            }
            GetSize();
            if(!IsDownload){
                download_Share();
            }
        }
        private void shareWhatsApp(final String v) {
            final Uri uri = Uri.parse(v);
            final Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("video/mp4");
            videoshare.setPackage("com.whatsapp");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM,uri);
            context.startActivity(videoshare);
        }
        private void sharevideo(String v){
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("video/mp4");
            Uri videoUri =  Uri.parse(v);
            share.putExtra(Intent.EXTRA_STREAM, videoUri);
            context.startActivity(Intent.createChooser(share, "Select"));
        }
        private void downloadfile(String v,boolean isVideo){
            final String name = getRandomName(isVideo);
            mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final Uri downloadUri = Uri.parse(v.trim());
            final DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true)
                    .setTitle("Downloading...")
                    .setShowRunningNotification(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name);
            assert mgr != null;
            myDB.inert_gallery(STORAGE_PATH + name);
            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
            enqueue =  mgr.enqueue(request);
        }
        private String getRandomName(boolean isVideo) {
            final String SALTCHARS = "1234567890";
            final StringBuilder salt = new StringBuilder();
            final Random rnd = new Random();
            while (salt.length() < 11) {
                int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                salt.append(SALTCHARS.charAt(index));
            }
            if(type==6){
                return salt.toString()+".mp4";
            }else if(type==2){
                if(isVideo){
                    return String.valueOf(id)+".mp4";
                }else{
                    return String.valueOf(id)+".jpg";
                }
            }else if(type==4 || type==5){
                return salt.toString()+".jpg";
            }else{
                if(isVideo){
                    return salt.toString()+".mp4";
                }else{
                    return salt.toString()+".jpg";
                }
            }
        }
        private void DWA(String url,boolean isVideo){
            destinationPath = STORAGE_PATH + getRandomName(isVideo);
            this.download_url = url;
            new downloadWhatsappstatus().execute();
        }
        private void GetSize(){
            new getsize().execute();
        }
        private class downloadWhatsappstatus extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... params) {
                File destination = new File(destinationPath);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            File f = getFile(context,Uri.parse(download_url));
                            try (InputStream in = new FileInputStream(f)) {
                                try (OutputStream out = new FileOutputStream(destinationPath)) {
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }
                                }
                            }
                        }else{
                            File source = new File(Uri.parse(download_url).getPath());
                            FileUtils.copy(new FileInputStream(source),new FileOutputStream(destination));
                        }
                    }else{
                        File source = new File(Uri.parse(download_url).getPath());
                        try (InputStream in = new FileInputStream(source)) {
                            try (OutputStream out = new FileOutputStream(destinationPath)) {
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                myDB.inert_gallery(destinationPath.trim());
                pbar.setVisibility(View.GONE);
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
        }
        public static File getFile(Context context, Uri uri) throws IOException {
            File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
            try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
                createFileFromStream(ins, destinationFilename);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return destinationFilename;
        }

        public static void createFileFromStream(InputStream ins, File destination) {
            try (OutputStream os = new FileOutputStream(destination)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = ins.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private static String queryName(Context context, Uri uri) {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        }
        private void shareanyapp(final boolean f) {
            Bitmap b = ((BitmapDrawable)showimg.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),b, "", "ShariLink");
            Uri imageUri =  Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            if(f){
                share.setPackage("com.whatsapp");
            }
            context.startActivity(Intent.createChooser(share, "Select"));
        }
        private void getstatussize(final String s){
            final File file = new File(Uri.parse(s).getPath());
            long file_size = file.length();
            if(file_size > 999999){
                file_size = (file_size / 1024) / 1024;
                size = file_size + " MB";
            }else{
                file_size = file_size / 1024;
                size = String.valueOf(file_size) + " kB";
            }
            if(size.startsWith("0")){
                sizetv.setText("");
            }else{
                sizetv.setText(size);
            }

        }
        private void showpopMenu(final View view,final int b,final String[][] src){
            path_url = src;
            final PopupMenu popupMenu = new PopupMenu(context,view);
            popupMenu.inflate(R.menu.size_menu);
            if(b==0){
                popupMenu.getMenu().getItem(0).setTitle("Download Size");
            }else if(b==1){
                popupMenu.getMenu().getItem(0).setTitle("Change Quatily");
            }else if(b==2){
                popupMenu.getMenu().getItem(0).setTitle("Set Wallpaper Size");
            }
            for(int i=0;i<path_url.length;i++){
                if(path_url[i][0]!=null){
                    popupMenu.getMenu().getItem(i+1).setTitle(path_url[i][0]);
                    popupMenu.getMenu().getItem(i+1).setVisible(true);
                    if(r==i && b==1){
                        popupMenu.getMenu().getItem(i+1).setCheckable(true);
                        popupMenu.getMenu().getItem(i+1).setChecked(true);
                    }
                }
            }
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){


                    }
                    if(r!=999){
                        download_url = path_url[r][1];
                        if(b==0){
                            downloadfile(download_url,false);
                        }else if(b==1){
                            new getsize().execute();
                            ChangeQuality(download_url.trim());
                        }else if(b==2){
                            Intent summaryIntent = new Intent(context,set_wallpaper.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("image_path",download_url.trim());
                            summaryIntent.putExtras(bundle);
                            context.startActivity(summaryIntent);
                        }
                    }
                    return false;
                }
            });
            videoView.setMediaController(new MediaController(context){
                public boolean dispatchKeyEvent(KeyEvent event)
                {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                        ((Activity) getContext()).finish();
                    return super.dispatchKeyEvent(event);
                }
            });
        }
        @SuppressLint("RestrictedApi")
        private void ChangeQuality(final String URL){
            pbar.setVisibility(View.VISIBLE);
            if(type==6){
                videoView.setVideoPath(URL);
                videoView.requestFocus();
            }else{
                Picasso.get().load(URL.trim()).into(showimg, new Callback() {
                    @Override
                    public void onSuccess() {
                        showimg.setVisibility(View.VISIBLE);
                        pbar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        pbar.setVisibility(View.GONE);
                    }
                });
            }
        }
        private class getsize extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL myUrl = new URL(download_url);
                    URLConnection urlConnection = myUrl.openConnection();
                    urlConnection.connect();
                    long file_size = urlConnection.getContentLength();
                    if(file_size > 999999){
                        file_size = (file_size / 1024) / 1024;
                        size = String.valueOf(file_size) + " MB";
                    }else{
                        file_size = file_size / 1024;
                        size = file_size + " kB";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sizetv.setText(size);
            }
        }
        private void perform6type(image_model im){
            stp.setVisibility(View.VISIBLE);
            MediaController mediacontroller  =  new MediaController(context);
            mediacontroller.setAnchorView(videoView);
            videoView.setMediaController(mediacontroller);
            share_btn.setVisibility(View.GONE);
            path_url = im.getVideosrc();
            download_url = path_url[0][1];
            whatsapp_btn.setVisibility(View.GONE);
            share_btn.setVisibility(View.GONE);
            videoView.setVideoPath(download_url);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    stp.setVisibility(View.GONE);
                    pbar.setVisibility(View.GONE);
                    videoView.start();
                    mediaPlayer.start();
                    float videoratio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                    float screenratio = videoView.getWidth() / (float) videoView.getHeight();
                    float scale = videoratio / screenratio;
                    if(scale >= 1f){
                        videoView.setScaleX(scale);
                    }else{
                        videoView.setScaleY(1f/scale);
                    }
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stp.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        private void download_Share(){
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        File fa = new File(STORAGE_PATH+id+".mp4");
                        if(!fa.exists()){
                            download_flag = 0;
                            IsDownload = false;
                            minip.setVisibility(View.GONE);
                        }
                        long downloadId = intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(enqueue);
                        Cursor c = mgr.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                download_btn.setVisibility(View.GONE);
                                minip.setVisibility(View.GONE);
                                IsDownload = true;
                                if(download_flag==1){
                                    shareWhatsApp(STORAGE_PATH+id+".mp4");
                                }else if(download_flag==2){
                                    download_url = STORAGE_PATH+id+".mp4";
                                    sharevideo(download_url);
                                }else{
                                    Toast.makeText(context, "Download Finish.", Toast.LENGTH_SHORT).show();
                                }
                                download_flag = 0;
                            }
                        }
                    }
                }

            };

        }
    }

    public void unregisterBrodcast(){
        if(type==2){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    }

}
