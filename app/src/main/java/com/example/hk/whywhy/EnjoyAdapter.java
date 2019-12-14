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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnjoyAdapter extends RecyclerView.Adapter<EnjoyAdapter.ViewHolder> {

    SessionManager sessionManager;
    private ArrayList<Enjoy> taglist;
    private Context context;
    private Resources resources;
    private Drawable drawable, drawable2;
    private static String URL_TLIKEACTION = "http://kimyw1196.dothome.co.kr/tlikeaction.php";
    private static String URL_TAGLIKEID = "http://kimyw1196.dothome.co.kr/gettaglikeid.php";


    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btn_enjoy;

        public ViewHolder(View itemView) {
            super(itemView);
            btn_enjoy = (Button) itemView.findViewById(R.id.btn_enjoy);
        }
    }

    public EnjoyAdapter(ArrayList<Enjoy> enjoys, Context context, Resources resources) {
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
    public void onBindViewHolder(@NonNull final EnjoyAdapter.ViewHolder holder, final int position) {
        holder.btn_enjoy.setText("#" + taglist.get(position).getTag_text());
        //이 부분에서 check하고 custom1 or 2 로 만들어주면 될 듯 하다.
        sessionManager = new SessionManager(context);
        final HashMap<String, String> user = sessionManager.getUserDetail();
        final String get_id = user.get(sessionManager.NAME);
        final String db_id = user.get(sessionManager.ID);

        //태그 좋아요 정보
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TAGLIKEID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();

                                    //로그인한 상태이고 내가 공감을 누른 태그면 색상 변경
                                    if (sessionManager.isLogin()) {
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            if (db_id.trim().equals(id)) {
                                                Log.d("zz", id);
                                                holder.btn_enjoy.setBackground(drawable);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tno", taglist.get(position).getTno());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        drawable = ResourcesCompat.getDrawable(resources, R.drawable.custom_btn2, null);
        drawable2 = ResourcesCompat.getDrawable(resources, R.drawable.custom_btn, null);

        //로그인한 상태이고 내가 단 태그이면 색상 변경
        if (sessionManager.isLogin() && get_id.trim().equals(taglist.get(position).getTagger())) {
            holder.btn_enjoy.setBackground(drawable);
        } else {
            holder.btn_enjoy.setBackground(drawable2);
        }
        //로그인 상태에서만 클릭 가능

        holder.btn_enjoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if else로 로그인 체크
                if (sessionManager.isLogin()) {
                    if (holder.btn_enjoy.getBackground() == drawable) {
                        tlikeaction(taglist.get(position).getTno(), db_id);
                        holder.btn_enjoy.setBackground(drawable2);
                    } else {
                        tlikeaction(taglist.get(position).getTno(), db_id);
                        holder.btn_enjoy.setBackground(drawable);
                    }
                } else {
                    Toast.makeText(context, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taglist.size();
    }

    private void tlikeaction(final String tno, final String id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TLIKEACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(context, "공감", Toast.LENGTH_SHORT).show();
                            }

                            if (success.equals("2")) {
                                Toast.makeText(context, "공감 취소", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tno", tno);
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}