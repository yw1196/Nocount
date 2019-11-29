package com.example.hk.whywhy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.AsyncTask;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HK on 2019-10-08.
 */

public class DetailActivity extends AppCompatActivity {

    private String title;
    private String release;
    private String director;
    private String rate;
    private String img_url;
    private String link;
    private String mID;
    private Bitmap image , resized_image;
    private int width = 300;
    private int height = 400;
    private static String URL_GETMOVIEDATA = "http://kimyw1196.dothome.co.kr/getmoviedata.php";
    private static String URL_ADDTAG = "http://kimyw1196.dothome.co.kr/addtag.php";
    private static String URL_GETTAG = "http://kimyw1196.dothome.co.kr/gettagdata.php";
    SessionManager sessionManager;
    private String user_name, edit_get;
    private Resources res;

    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Enjoy> tags = new ArrayList<>();

    private RecyclerView re_tag, re_review;

    private ReviewDialog reviewDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        res = getResources();

        //리니어 (스크롤 뷰 (리니어 ( 이미지 1 텍스트 3) ItemObject 안에 있는걸 가져와서 연결해 주어야 함
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        release = intent.getStringExtra("release");
        director = intent.getStringExtra("director");
        img_url = intent.getStringExtra("img_url");
        rate = intent.getStringExtra("rate");
        link = intent.getStringExtra("link");
        image = (Bitmap)intent.getParcelableExtra("image");
        mID = link.replaceAll("[^0-9]", "");


        re_tag = (RecyclerView) findViewById(R.id.re_tag);
        re_review = (RecyclerView) findViewById(R.id.re_review);

        //기본 영화정보
        TextView tv_title = (TextView) findViewById(R.id.review_title);
        tv_title.setText(title);

        TextView tv_release = (TextView) findViewById(R.id.review_release);
        tv_release.setText(release);

        TextView tv_director = (TextView) findViewById(R.id.review_director);
        tv_director.setText(director);

        TextView tv_rate = (TextView) findViewById(R.id.review_rate);
        tv_rate.setText(rate);

        //리뷰 버튼 리스너

        Button btn_review = (Button)findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewDialog = new ReviewDialog(DetailActivity.this, title, release, director, rate, img_url, mID);
                reviewDialog.show();
            }
        });

        //태그 및 태그 등록버튼 리스너
        TextInputLayout TI_layout = (TextInputLayout) findViewById(R.id.TI_layout);
        TI_layout.setCounterEnabled(true);
        TI_layout.setCounterMaxLength(10);
        final TextInputEditText TI_edit= (TextInputEditText) findViewById(R.id.TI_edit);
        Button TI_btn = (Button) findViewById(R.id.TI_btn);

        TI_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_get = TI_edit.getText().toString().trim();

                if(edit_get.trim().equals("")){
                    Toast.makeText(DetailActivity.this, "태그를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                tag_regist(edit_get);
                //new Async().execute();
            }
        });

        //리뷰 모아보기
        final TextView tv_sum = (TextView) findViewById(R.id.review_sum);

        ImageView iv_poster = (ImageView) findViewById(R.id.review_poster);
        if(img_url == null) {
            iv_poster.setImageBitmap(image); //or resized_image
        } else {
            GlideApp.with(this)
                    .load(img_url)
                    .override(300, 400)
                    .into(iv_poster);
        }

        sessionManager = new SessionManager(this);
        //sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        user_name = user.get(sessionManager.NAME);

        get_tag();

        //private void getDate() {
        final String movie_id = this.mID;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETMOVIEDATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("here",response.toString());

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){

                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String t1 = object.getString("replyer").trim();
                                    String t2 = object.getString("reply").trim();
                                    String t3 = object.getString("rating").trim();
                                    String t4 = object.getString("likeno").trim();
                                    String t5 = object.getString("regdate").trim();

                                    reviews.add(new Review(t1,t2,t3,t4,t5));

                                    ReviewAdapter rvAdapter = new ReviewAdapter(reviews, DetailActivity.this);
                                    rvAdapter.notifyDataSetChanged();
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
                                    re_review.setLayoutManager(layoutManager);
                                    re_review.setAdapter(rvAdapter);

                                    tv_sum.setText("총 " + reviews.size() + "건" );
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
                params.put("movie_id", movie_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /*private class Async extends AsyncTask<Void, Void, Void> {

        private String async_mID = mID;
        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(DetailActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시 기다려 주세요.");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final String movie_id = this.async_mID;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETTAG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("read");

                                if(success.equals("1")){

                                    for(int i =0;i<jsonArray.length();i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String t1 = object.getString("tagger").trim();
                                        String t2 = object.getString("tag_text").trim();
                                        int t3 = object.getInt("tag_like");

                                        tags.add(new Enjoy(t1, t2, t3));
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
                    params.put("movie_id", movie_id);
                    return  params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EnjoyAdapter eAdapter = new EnjoyAdapter(tags, DetailActivity.this, res);
            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
            re_tag.setLayoutManager(layoutManager2);
            re_tag.setAdapter(eAdapter);

            progressDialog.dismiss();
        }
    }*/

    private void tag_regist(final String edit_get){
        final String movie_id = this.mID;
        final String tagger = this.user_name;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDTAG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(DetailActivity.this, "태그 등록완료", Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("movie_id", movie_id);
                params.put("tagger", tagger);
                params.put("tag_text", edit_get);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void get_tag() {

        final String movie_id = this.mID;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETTAG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){

                                for(int i =0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String t1 = object.getString("tagger").trim();
                                    String t2 = object.getString("tag_text").trim();
                                    int t3 = object.getInt("tag_like");

                                    tags.add(new Enjoy(t1, t2, t3));

                                    EnjoyAdapter eAdapter = new EnjoyAdapter(tags, DetailActivity.this, res);
                                    RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                    re_tag.setLayoutManager(layoutManager2);
                                    re_tag.setAdapter(eAdapter);
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
                params.put("movie_id", movie_id);
                return  params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}