package com.example.hk.whywhy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
/**
 * Created by HK on 2019-10-07.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context mContext;

    //데이터 배열 선언
    private ArrayList<ItemObject> mList;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView_img;
        //private TextView textView_title, textView_release, texView_director, textView_rate;;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView_img = (ImageView) itemView.findViewById(R.id.imageView_img);
            //textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            //textView_release = (TextView) itemView.findViewById(R.id.textView_release);
            //texView_director = (TextView) itemView.findViewById(R.id.textView_director);
            //textView_rate = (TextView) itemView.findViewById(R.id.textView_rate);
        }
    }

    //생성자
    public MyAdapter(ArrayList<ItemObject> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_data, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {

        //holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
        //holder.textView_release.setText(String.valueOf(mList.get(position).getRelease()));
        //holder.texView_director.setText(String.valueOf(mList.get(position).getDirector()));
        //holder.textView_rate.setText(String.valueOf(mList.get(position).getRate()));
        //다 해줬는데도 GlideApp 에러가 나면 rebuild project를 해주자.
        GlideApp.with(holder.itemView).load(mList.get(position).getImg_url())
                .override(300,400)
                .into(holder.imageView_img);

        holder.imageView_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DetailActivity detailActivity = new DetailActivity(mContext);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("title", mList.get(position).getTitle());
                intent.putExtra("release", mList.get(position).getRelease());
                intent.putExtra("director", mList.get(position).getDirector());
                intent.putExtra("img_url", mList.get(position).getImg_url());
                intent.putExtra("rate", mList.get(position).getRate());
                intent.putExtra("link", mList.get(position).getDetail_link());
                mContext.startActivity(intent);
                Log.d("Adapter", "Clicked: " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
