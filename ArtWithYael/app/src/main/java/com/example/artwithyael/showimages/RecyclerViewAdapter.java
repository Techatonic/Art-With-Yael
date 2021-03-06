package com.example.artwithyael.showimages;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.artwithyael.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Image[] data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private Context context;

    String URLTemplate = "http://artwithyael.000webhostapp.com/Images/";

    public RecyclerViewAdapter(Context context, Image[] data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        view.getLayoutParams().width = (int) (0.5 * Resources.getSystem().getDisplayMetrics().widthPixels);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

        Glide.with(context)
                .load(URLTemplate+data[position].getImagePath())
                //.override(holder.itemView.getWidth(),350)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        holder.titleTextView.setText(data[position].getTitle());
        String date = data[position].getDate();
        String[] splittedDate = date.split("-");
        String dateToShow = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];
        holder.dateTextView.setText(dateToShow);
    }

    @Override
    public int getItemCount(){
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView titleTextView;
        TextView dateTextView;

        ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            titleTextView = itemView.findViewById(R.id.title);
            dateTextView = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(clickListener != null){
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public Image getItem(int id){
        return data[id];
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

}
