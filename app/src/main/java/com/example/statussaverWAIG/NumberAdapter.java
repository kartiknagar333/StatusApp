package com.example.statussaverWAIG;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class NumberAdapter extends ListAdapter<Entity,NumberAdapter.UserHolder> {
    private OnItemClickListener listener;

    public NumberAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Entity> DIFF_CALLBACK = new DiffUtil.ItemCallback<Entity>() {
        @Override
        public boolean areItemsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getCountry_code()==(newItem.getCountry_code()) &&
                    oldItem.getDatetime().equals(newItem.getDatetime()) && oldItem.getPhone_number()==(newItem.getPhone_number());
        }
    };


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.number_item,parent,false);
        return new UserHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        final Entity user = getItem(position);
        holder.tv.setText(String.valueOf(user.getId()));
        holder.tv1.setText("+"+String.valueOf(user.getCountry_code()));
        holder.tv2.setText(String.format("%.0f", user.getPhone_number()));
        holder.tv3.setText(user.getDatetime());
        holder.tv4.setText(user.getName());
        holder.tv5.setText(user.getIso());
    }



    class UserHolder extends RecyclerView.ViewHolder{
        private TextView tv,tv1,tv2,tv3,tv4,tv5;
        private RelativeLayout layout;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.pic);
            tv1 = itemView.findViewById(R.id.tvcountrycode);
            tv2 = itemView.findViewById(R.id.tvnumber);
            tv3 = itemView.findViewById(R.id.tvdate);
            tv4 = itemView.findViewById(R.id.tvname);
            tv5 = itemView.findViewById(R.id.tviso);
            layout = itemView.findViewById(R.id.numberitem);
            tv2.setSelected(true);
            tv3.setSelected(true);
            tv4.setSelected(true);
            tv.setSelected(true);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int po = getAdapterPosition();
                    if(listener != null && po != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(po));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Entity entity);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
