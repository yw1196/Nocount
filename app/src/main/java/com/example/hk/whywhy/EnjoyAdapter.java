package com.example.hk.whywhy;

/**
 * Created by HK on 2019-11-20.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class EnjoyAdapter extends RecyclerView.Adapter<EnjoyAdapter.ViewHolder> {

    SessionManager sessionManager;
    private ArrayList<Enjoy> taglist;
    private Context context;
    private Resources resources;
    private Drawable drawable, drawable2;


    public  class ViewHolder extends RecyclerView.ViewHolder {
        private Button btn_enjoy;

        public ViewHolder(View itemView) {
            super(itemView);
            btn_enjoy = (Button)itemView.findViewById(R.id.btn_enjoy);
        }
    }

    public EnjoyAdapter(ArrayList<Enjoy> enjoys, Context context, Resources resources){
        this.taglist = enjoys;
        this.context = context;
        this.resources = resources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_btn, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EnjoyAdapter.ViewHolder holder, int position) {
        holder.btn_enjoy.setText("#"+taglist.get(position).getTag_text());
        //이 부분에서 check하고 custom1 or 2 로 만들어주면 될 듯 하다.
        sessionManager = new SessionManager(context);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String get_id = user.get(sessionManager.NAME);

        drawable = ResourcesCompat.getDrawable(resources, R.drawable.custom_btn2, null);
        drawable2 = ResourcesCompat.getDrawable(resources, R.drawable.custom_btn, null);

        //로그인한 상태이고 내가 단 태그이면 색상 변경
        if(sessionManager.isLogin() && get_id.trim().equals(taglist.get(position).getTagger())) {
            holder.btn_enjoy.setBackground(drawable);
        }

        holder.btn_enjoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_enjoy.getBackground() == drawable) {
                    holder.btn_enjoy.setBackground(drawable2);
                } else {
                    holder.btn_enjoy.setBackground(drawable);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return taglist.size(); }
}