package com.example.hk.whywhy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.os.AsyncTask;

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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    SessionManager sessionManager;
    private ArrayList<Review> reviews;
    private Context context;
    private static String URL_LIKEACTION = "http://kimyw1196.dothome.co.kr/likeaction.php";
    private static String URL_GETLIKE = "http://kimyw1196.dothome.co.kr/getreviewlike.php";



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
    public void onBindViewHolder(@NonNull final ReviewAdapter.ViewHolder holder, final int position) {



        holder.rc_rate.setRating(Float.parseFloat(reviews.get(position).getRating()));
        holder.rc_mean.setText(reviews.get(position).getRating());
        holder.rc_review.setText(reviews.get(position).getReply());
        holder.rc_nick.setText(reviews.get(position).getReplyer());
        holder.rc_date.setText(reviews.get(position).getRegdate());
        holder.rc_btnlike.setText(reviews.get(position).getLikeno());


        sessionManager = new SessionManager(context);



        holder.rc_btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionManager.isLogin()) {   // 로그인 상태일 때만 값 받아옴

                    HashMap<String, String> user = sessionManager.getUserDetail();
                    final String get_id = user.get(sessionManager.ID);
                    Log.d("ID값", get_id);
                    Log.d("rno값", reviews.get(position).getRno());

                    likeaction(reviews.get(position).getRno(), get_id);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETLIKE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String success = jsonObject.getString("success");
                                        JSONArray jsonArray = jsonObject.getJSONArray("read");

                                        if(success.equals("1")){

                                            for(int i=0; i<jsonArray.length();i++){

                                                JSONObject object = jsonArray.getJSONObject(i);

                                                String like = object.getString("likeno").trim();
                                                holder.rc_btnlike.setText(like);
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
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("rno", reviews.get(position).getRno());
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(stringRequest);

                }else{
                    Toast.makeText(context, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private void likeaction(final String rno, final String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LIKEACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(context, "좋아요", Toast.LENGTH_SHORT).show();
                            }

                            if(success.equals("2")){
                                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                            }

                            if(success.equals("0")){
                                Toast.makeText(context, "에러", Toast.LENGTH_SHORT).show();
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
                params.put("rno", rno);
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void getreviewlike(final String rno){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETLIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){

                                for(int i=0; i<jsonArray.length();i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String like = object.getString("likeno").trim();

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
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rno", rno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}