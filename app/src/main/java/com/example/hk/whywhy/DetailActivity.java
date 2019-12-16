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
import android.support.v7.widget.GridLayoutManager;
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
    private String user_name, user_id, edit_get;
    private Bitmap image, resized_image;
    private int width = 300;
    private int height = 400;
    private static String URL_GETMOVIEDATA = "http://kimyw1196.dothome.co.kr/getreviewdata.php";
    private static String URL_ADDTAG = "http://kimyw1196.dothome.co.kr/addtag.php";
    private static String URL_GETTAG = "http://kimyw1196.dothome.co.kr/gettagdata.php";
    private SessionManager sessionManager;

    private Resources res;

    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Enjoy> tags = new ArrayList<>();

    private EnjoyAdapter eAdapter;
    private ReviewAdapter rvAdapter;

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
        image = (Bitmap) intent.getParcelableExtra("image");
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
        //포스터
        ImageView iv_poster = (ImageView) findViewById(R.id.review_poster);
        if (img_url == null) {
            iv_poster.setImageBitmap(image); //or resized_image
        } else {
            GlideApp.with(this)
                    .load(img_url)
                    .override(300, 400)
                    .into(iv_poster);
        }

        //리뷰 버튼 초기화 및 리스너
        Button btn_review = (Button) findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.isLogin()) {
                    reviewDialog = new ReviewDialog(DetailActivity.this, title, release, director, rate, img_url, mID, reviews, rvAdapter);
                    reviewDialog.show();
                    if (reviews.isEmpty())
                        new Async().execute();
                } else {
                    Toast.makeText(DetailActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final TextView tv_sum = (TextView) findViewById(R.id.review_sum);

        //태그 레이아웃 및 태그 등록버튼 리스너
        TextInputLayout TI_layout = (TextInputLayout) findViewById(R.id.TI_layout);
        TI_layout.setCounterEnabled(true);
        TI_layout.setCounterMaxLength(10);
        final TextInputEditText TI_edit = (TextInputEditText) findViewById(R.id.TI_edit);
        Button TI_btn = (Button) findViewById(R.id.TI_btn);

        //어뎁터
        rvAdapter = new ReviewAdapter(reviews, DetailActivity.this);
        eAdapter = new EnjoyAdapter(tags, DetailActivity.this, res);

        TI_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 했는지 확인 해야 한다.
                if(!sessionManager.isLogin()) {
                    Toast.makeText(DetailActivity.this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    //굳이 loaginActivity를 호출할 이유는 없을 것 같다.
                    //sessionManager.checkLogin();
                    return;
                }

                edit_get = TI_edit.getText().toString().trim();
                boolean checker = true;

                if (edit_get.trim().equals("")) {
                    Toast.makeText(DetailActivity.this, "태그를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(edit_get.trim().length() > 10){
                    Toast.makeText(DetailActivity.this, "태그는 10자 이내로 적어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                //tags 어레이가 비어있으면 editText값을 DB에 저장 후 recyclerview에 adapter를 연결 하여 뷰 갱신
                if (tags.isEmpty()) {
                    tag_regist(edit_get);
                    get_tag();
                    eAdapter.notifyDataSetChanged();
                    Log.d("hiiz", String.valueOf(tags.size()));
                    TI_edit.setText(null);
                    return;
                }


                Log.d("size", String.valueOf(tags.size()));
                //tags의 size가 1이 될 경우 이중포문이 돌아가지 않게 된다. 어떻게 해결해야 하지?
                for (int i = 0; i < tags.size(); i++) {
                    if (tags.size() == 1 && edit_get.equals(tags.get(i).getTag_text())) {
                        Toast.makeText(DetailActivity.this, "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show();
                        Log.d("yyy", "중복값있음");
                        TI_edit.setText(null);
                        break;
                    }
                    for (int j = 0; j < i+1; j++) {
                        Log.d("text", tags.get(j).getTag_text());
                        if (edit_get.equals(tags.get(j).getTag_text())) {
                            Toast.makeText(DetailActivity.this, "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show();
                            Log.d("zzz", "중복값있음");
                            checker = false;
                            TI_edit.setText(null);
                            break;
                        }
                    }
                }
                //true로 사용되는 지역변수 checker가 중복검사에 걸려 false가 될 시 실행되지 않는다.
                if (checker) {
                    tag_regist(edit_get);
                    tags.add(0, new Enjoy("empty", user_name, edit_get, "0"));
                    eAdapter.notifyItemInserted(0);
                    TI_edit.setText(null);
                }
            }
        });


        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        user_name = user.get(sessionManager.NAME);
        user_id = user.get(sessionManager.ID);
        //get_tag 메소드를 사용하여 태그 정보를 DB로부터 받아와 리사이클러뷰 어뎁터에 연결 후 화면에 표시
        get_tag();

        //부득이하게 메소드로 사용 불가 tv_sum값을 만족시켜주지 못한다... 왜 size값을 못 받아오지?
        //private void getDate() {
        final String movie_id = this.mID;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETMOVIEDATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("here", response.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String t0 = object.getString("rno").trim();
                                    String t1 = object.getString("replyer").trim();
                                    String t2 = object.getString("reply").trim();
                                    String t3 = object.getString("rating").trim();
                                    String t4 = object.getString("likeno").trim();
                                    String t5 = object.getString("regdate").trim();

                                    reviews.add(new Review(t0, t1, t2, t3, t4, t5));

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
                                    re_review.setLayoutManager(layoutManager);
                                    re_review.setAdapter(rvAdapter);

                                    tv_sum.setText("총 " + reviews.size() + "건");
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

    //다이얼로그에서 첫 리뷰를 갱신해주지 못해 AsyncTask를 사용하여준다.
    private class Async extends AsyncTask<Void, Void, Void> {

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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETMOVIEDATA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("here", response.toString());

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("read");

                                if (success.equals("1")) {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String t0 = object.getString("rno").trim();
                                        String t1 = object.getString("replyer").trim();
                                        String t2 = object.getString("reply").trim();
                                        String t3 = object.getString("rating").trim();
                                        String t4 = object.getString("likeno").trim();
                                        String t5 = object.getString("regdate").trim();

                                        reviews.add(new Review(t0, t1, t2, t3, t4, t5));


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

            RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
            re_review.setLayoutManager(layoutManager);
            re_review.setAdapter(rvAdapter);

            //tv_sum에 접근하지 못 함
            //tv_sum.setText("총 " + reviews.size() + "건");
            progressDialog.dismiss();
        }
    }

    private void tag_regist(final String edit_get) {
        final String movie_id = this.mID;
        final String tagger = this.user_name;
        final String id = this.user_id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDTAG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(DetailActivity.this, "태그 등록완료", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (success.equals("2")) {
                                Toast.makeText(DetailActivity.this, "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show();
                                return;
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("movie_id", movie_id);
                params.put("tagger", tagger);
                params.put("tag_text", edit_get);
                params.put("id", id);
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

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String t0 = object.getString("tno").trim();
                                    String t1 = object.getString("tagger").trim();
                                    String t2 = object.getString("tag_text").trim();
                                    String t3 = object.getString("tag_like").trim();

                                    tags.add(new Enjoy(t0, t1, t2, t3));

                                    RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(DetailActivity.this, 3);
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
                }) {
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
}