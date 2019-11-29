package com.example.hk.whywhy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created by HK on 2019-11-17.
 */

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<Review> reviews;
    private Context context;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private RatingBar rc_rate;
        private TextView rc_mean, rc_review, rc_nick, rc_date;
        private Button rc_btnlike;

        public ViewHolder(View itemView) {
            super(itemView);
        rc_rate = (RatingBar)itemView.findViewById(R.id.rc_rate);
        rc_mean = (TextView)itemView.findViewById(R.id.rc_mean);
        rc_review = (TextView)itemView.findViewById(R.id.rc_review);
        rc_nick = (TextView)itemView.findViewById(R.id.rc_nick);
        rc_date = (TextView)itemView.findViewById(R.id.rc_date);
        rc_btnlike = (Button)itemView.findViewById(R.id.rc_btnlike);
        }
    }

    public ReviewAdapter(ArrayList<Review> review, Context context) {
        this.reviews = review;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_review, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        holder.rc_rate.setRating(Float.parseFloat(reviews.get(position).getRating()));
        holder.rc_mean.setText(reviews.get(position).getRating());
        holder.rc_review.setText(reviews.get(position).getReply());
        holder.rc_nick.setText(reviews.get(position).getReplyer());
        holder.rc_date.setText(reviews.get(position).getRegdate());
        holder.rc_btnlike.setText(reviews.get(position).getLikeno());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
