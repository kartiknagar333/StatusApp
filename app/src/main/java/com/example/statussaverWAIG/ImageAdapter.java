package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kartik on 12/14/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<image_model> ImgList;
    private other_Fragment fragment = null;
    private instagram_Fragment ifragment = null;
    private View view;
    private int p = 0;
    private int temp;
    private static Intent summaryIntent;
    private static Bundle bundle;
    private Context context;
    private static com.example.statussaverWAIG.Database myDB;

    protected ImageAdapter(final ArrayList<image_model> ImgList, final other_Fragment fragment,final instagram_Fragment ifragment, final int temp) {
        this.ImgList = ImgList;
        if(fragment==null){
            this.ifragment = ifragment;
            myDB = new com.example.statussaverWAIG.Database(ifragment.getContext());
        }else{
            this.fragment = fragment;
            myDB = new com.example.statussaverWAIG.Database(fragment.getContext());
        }
        this.temp = temp;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(temp<3 ){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.insta_image_layout, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_layout, parent, false);
        }

        return new ViewHolder(view);
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(temp<3){
            if(this.ImgList.get(position).getID()==null){
                holder.rmv.setVisibility(View.GONE);
            }
            final String s = this.ImgList.get(position).getThumb();
            if(s!=null){
                Picasso.get().load(s.trim()).into(holder.iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(ImgList.get(position).isVideo()){
                            holder.icon.setVisibility(View.VISIBLE);
                        }
                        holder.p.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.p.setVisibility(View.GONE);
                        notifyItemRemoved(position);
                    }
                });
            }else{
                holder.relativeLayout.setVisibility(View.GONE);
            }

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void onClick(View view) {
                    summaryIntent = new Intent(ifragment.getContext(), slidepager.class);
                    bundle = new Bundle();
                    bundle.putSerializable("filelist",ImgList);
                    bundle.putSerializable("position",position);
                    bundle.putInt("type",2);
                    bundle.putString("insta_id",ImgList.get(position).getID());
                    summaryIntent.putExtras(bundle);
                    ifragment.getContext().startActivity(summaryIntent);
                }
            });
        }else{
            String s = null;
            if(temp==5){
                s = this.ImgList.get(position).getPortrait();
            }else if(temp==4){
                s = this.ImgList.get(position).getMedium();
            }else if(temp==6){
                s = this.ImgList.get(position).getThumb();
            }
            if(s!=null){
                Picasso.get().load(s).into(holder.iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(temp==6){
                            holder.icon.setVisibility(View.VISIBLE);
                        }
                        holder.p.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        holder.p.setVisibility(View.GONE);
                        notifyItemRemoved(position);
                    }
                });
            }else{
                holder.relativeLayout.setVisibility(View.GONE);
            }
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void onClick(View view) {
                    summaryIntent = new Intent(fragment.getContext(), slidepager.class);
                    bundle =new Bundle();
                    bundle.putSerializable("filelist",ImgList);
                    bundle.putSerializable("position",position);
                    bundle.putInt("type",temp);
                    summaryIntent.putExtras(bundle);
                    fragment.getContext().startActivity(summaryIntent);

                }
            });
        }
        if(temp<3) {
            holder.rmv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vv) {
                    final BottomSheetDialog bt = new BottomSheetDialog(ifragment.getContext(),R.style.BottomSheetDialogTheme);
                    View vi = LayoutInflater.from(ifragment.getContext()).inflate(R.layout.bottom_insta_menu,null);
                    vi.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bt.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ifragment.getContext());
                            builder.setTitle("Remove This Post?");
                            builder.setMessage("If you dont remove, this post is saved.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(myDB.delete_insta_post(ImgList.get(position).getColumn_index()) == 1){
                                                ImgList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                    vi.findViewById(R.id.sharelink).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bt.dismiss();
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_TEXT, ImgList.get(position).getLink());
                            ifragment.getContext().startActivity(Intent.createChooser(i, "Share URL"));
                        }
                    });
                    vi.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bt.dismiss();
                            Uri uri = Uri.parse(ImgList.get(position).getLink());
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                            likeIng.setPackage("com.instagram.android");
                            try {
                                ifragment.getContext().startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                ifragment.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(ImgList.get(position).getLink())));
                            }
                        }
                    });
                    bt.setContentView(vi);
                    bt.show();

                }
            });
        }

        if(temp==4){
            if(position==ImgList.size()-3){
                fragment.getUnsplashImage(true);
            }
        }else if(temp==5){
            if(position==ImgList.size()-3){
                fragment.getPexelsImage(true);
            }
        }else if(temp==6){
            if(position==ImgList.size()-3){
                fragment.getPexelsVideo(true);
            }
        }
    }
    @Override
    public int getItemCount() {
        return ImgList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
         private ImageView iv,icon,rmv;
         private RelativeLayout relativeLayout;
         private CardView cv;
         private ProgressBar p;
        @SuppressLint("RestrictedApi")
        ViewHolder(final View itemView) {
            super(itemView);
            this.relativeLayout = itemView.findViewById(R.id.image_layout);
            this.iv = itemView.findViewById(R.id.imageview);
            this.cv = itemView.findViewById(R.id.imagecard);
            this.p =  itemView.findViewById(R.id.progressbar);
            this.icon = itemView.findViewById(R.id.imgicon);
            if(temp<3){
                this.rmv = itemView.findViewById(R.id.remove);
            }
        }
    }
}

