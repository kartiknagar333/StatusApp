package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by kartik on 12/14/2018.
 */

public class WhatsAppStatusAdapter extends RecyclerView.Adapter<WhatsAppStatusAdapter.ViewHolder> {
    private ArrayList<image_model> filelist;
    private Context context = null;
    private static Intent summaryIntent;
    private static Bundle bundle;
    private static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Environment.DIRECTORY_DOWNLOADS+"/";
    private boolean download = false;
    private static com.example.statussaverWAIG.Database myDB;
    private static String download_url;
    protected WhatsAppStatusAdapter(final ArrayList<image_model> filelist, final Context context,final boolean download) {
        this.filelist = filelist;
        this.context = context;
        this.download = download;
        myDB = new com.example.statussaverWAIG.Database(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.big_circle, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(filelist.get(position).getoriginal().endsWith(".jpg")){
            if (30 <= Build.VERSION.SDK_INT) {
                if(download){
                    Picasso.get().load(new File(filelist.get(position).getoriginal())).into(holder.iv);
                }else{
                    Picasso.get().load(filelist.get(position).getoriginal()).into(holder.iv);
                }
            }else{
                Picasso.get().load(new File(filelist.get(position).getoriginal())).into(holder.iv);
            }
        }else{
            holder.videoicon.setVisibility(View.VISIBLE);
            if(30 <= Build.VERSION.SDK_INT){
                try {
                    holder.iv.setImageBitmap(createThumbnail(context,Uri.parse(filelist.get(position).getoriginal())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filelist.get(position).getoriginal(), MediaStore.Video.Thumbnails.MINI_KIND);
                holder.iv.setImageBitmap(thumb);
            }
        }
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                summaryIntent = new Intent(context, slidepager.class);
                bundle =new Bundle();
                bundle.putSerializable("filelist",filelist);
                bundle.putSerializable("position",position);
                bundle.putBoolean("downloaded",download);
                bundle.putInt("type",1);
                summaryIntent.putExtras(bundle);
                context.startActivity(summaryIntent);
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                final BottomSheetDialog bt = new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);
                View vi = LayoutInflater.from(context).inflate(R.layout.bottom_download_menu,null);
                if(download){
                    vi.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                    vi.findViewById(R.id.save).setVisibility(View.GONE);
                }
                vi.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bt.dismiss();
                        download_url = filelist.get(position).getoriginal();
                        new downloadWhatsappstatus().execute();
                    }
                });
                vi.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure for delete this file?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(myDB.delete_gallery(filelist.get(position).getID()) == 1){
                                            File f = new File(filelist.get(position).getoriginal());
                                            if(f.exists()){
                                                if(f.delete()){
                                                    filelist.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) { }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                vi.findViewById(R.id.sharewa).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt.dismiss();
                        if(filelist.get(position).getoriginal().endsWith(".jpg")){
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/jpeg");
                            Uri videoUri =  Uri.parse(filelist.get(position).getoriginal());
                            share.putExtra(Intent.EXTRA_STREAM, videoUri);
                            share.setPackage("com.whatsapp");
                            context.startActivity(Intent.createChooser(share, "Select"));
                        }else{
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("video/mp4");
                            Uri videoUri =  Uri.parse(filelist.get(position).getoriginal());
                            share.putExtra(Intent.EXTRA_STREAM, videoUri);
                            share.setPackage("com.whatsapp");
                            context.startActivity(Intent.createChooser(share, "Select"));
                        }
                    }
                });
                vi.findViewById(R.id.sharewo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt.dismiss();
                        if(filelist.get(position).getoriginal().endsWith(".jpg")){
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/jpeg");
                            Uri videoUri =  Uri.parse(filelist.get(position).getoriginal());
                            share.putExtra(Intent.EXTRA_STREAM, videoUri);
                            context.startActivity(Intent.createChooser(share, "Select"));
                        }else{
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("video/mp4");
                            Uri videoUri =  Uri.parse(filelist.get(position).getoriginal());
                            share.putExtra(Intent.EXTRA_STREAM, videoUri);
                            context.startActivity(Intent.createChooser(share, "Select"));
                        }
                    }
                });
                bt.setContentView(vi);
                bt.show();

            }
        });
        
    }
    @Override
    public int getItemCount() {
        return filelist.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv,videoicon,remove;
        @SuppressLint("RestrictedApi")
        ViewHolder(final View itemView) {
            super(itemView);
            this.iv = itemView.findViewById(R.id.imageview);
            this.videoicon = itemView.findViewById(R.id.imgicon);
            this.remove = itemView.findViewById(R.id.remove);
        }
    }
    private String getRandomName(final boolean isVideo) {
        final String SALTCHARS = "1234567890";
        final StringBuilder salt = new StringBuilder();
        final Random rnd = new Random();
        while (salt.length() < 11) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        if(isVideo){
            return salt.toString()+".mp4";
        }else{
            return salt.toString()+".jpg";
        }
    }
    private class downloadWhatsappstatus extends AsyncTask<Void, Void, Void> {
        String destinationPath;
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            if(download_url.endsWith(".mp4")){
                destinationPath = STORAGE_PATH + getRandomName(true);
            }else{
                destinationPath = STORAGE_PATH + getRandomName(false);
            }
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
    private static Bitmap getID(Context context, Uri uri){
        Cursor c = MediaStore.Video.query(context.getContentResolver(),uri, new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA});
        if (c!=null){
            c.moveToFirst();
            long id = c.getLong(0);
            Log.d("ss", "getID: "+c.getString(0));
            Log.d("ss", "getID: "+c.getLong(0));
            Log.d("ss", "getID: "+c.getInt(0));
            Log.d("ss", "getID: "+c.getType(0));

            c.close();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 1;
            try {
                return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, options);
            }catch (java.lang.SecurityException ex){
                ex.printStackTrace();
                //TODO: add create ThumbnailUtils.createVideoThumbnail
            }
        }
        return null;
    }
    public static Bitmap createThumbnail(Context activity, Uri path) throws IOException {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(activity, path);
            bitmap = mediaMetadataRetriever.getFrameAtTime(100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}

