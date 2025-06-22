package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by kartik on 12/14/2018.
 */

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {
    private ArrayList<String> catelist;
    private static final String[] category = {"Wallpapers","Nature","Mounatins","Beach","Bike","Car","People","Architecture","Current Events","Bussiness & Work","Experimental","Fashion","Movie","Health & Wellness","Interiors","Street Photography","Technology","Travel","Textures Patterns","Animals","Food Drink","Athletics","Spirituality","Arts Culture","History"};

    private com.example.statussaverWAIG.other_Fragment fragment = null;
    private boolean flag;

    protected categoryAdapter(final ArrayList<String> catelist, final com.example.statussaverWAIG.other_Fragment fragment, final boolean flag) {
        this.fragment = fragment;
        this.flag  = flag;
        if(this.flag){
            this.catelist = catelist;
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cate_item, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(flag){
            holder.tv.setText(catelist.get(position));
        }else{
            holder.tv.setText(category[position]);
        }

        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.clickcategoryItem(holder.tv.getText().toString());
            }
        });
    }
    @Override
    public int getItemCount() {
        if(flag){
            return catelist.size();
        }else{
            return category.length;
        }

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        @SuppressLint("RestrictedApi")
        ViewHolder(final View itemView) {
            super(itemView);
            this.tv = itemView.findViewById(R.id.categortText);
        }
    }
}

