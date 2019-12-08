package com.example.hk.whywhy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HK on 2019-10-25.
 */

public class ReviewDialog extends Dialog {

    private ImageView review_poster;
    private TextView review_title, review_release, review_director, review_rate;
    private Button btn_confrim, btn_cancle;
    private RatingBar rating_bar;
    private EditText review_edit;
    private String title, release, director, rate, img_url, mID, user_name;
    private static String URL_REGREPLY = "http://kimyw1196.dothome.co.kr/addreply.php";
    private SessionManager sessionManager;
    private ReviewAdapter rvAdapter;
    private ArrayList<Review> reviews;


    //private String mTitle;
    //private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_popup);

        review_title = (TextView) findViewById(R.id.review_title);
        review_release = (TextView) findViewById(R.id.review_release);
        review_director = (TextView) findViewById(R.id.review_director);
        review_rate = (TextView) findViewById(R.id.review_rate);

        rating_bar = (RatingBar) findViewById(R.id.rating_bar);

        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        btn_confrim = (Button) findViewById(R.id.btn_confirm);
        review_edit = (EditText) findViewById(R.id.review_edit);

        review_poster = (ImageView) findViewById(R.id.review_poster);

        review_title.setText(title);
        review_release.setText(release);
        review_director.setText(director);
        review_rate.setText(rate);
        GlideApp.with(this.getContext())
                .load(img_url)
                .override(300, 400)
                .into(review_poster);

        sessionManager = new SessionManager(getContext());

        HashMap<String, String> user = sessionManager.getUserDetail();
        user_name = user.get(sessionManager.NAME);

        //현재 시간 구하기
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        final String formatDate = sdfNow.format(date);

        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regist();
                reviews.add(0, new Review("empty",user_name, review_edit.getText().toString(), String.valueOf(rating_bar.getRating()), "0", formatDate));
                rvAdapter.notifyItemInserted(0);
                ReviewDialog.this.dismiss();

            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewDialog.this.dismiss();
            }
        });


    }

    public ReviewDialog(Context context, String title,
                        String release, String director, String rate, String img_url, String mID, ArrayList<Review> reviews, ReviewAdapter rvAdapter) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.title = title;
        this.release = release;
        this.director = director;
        this.rate = rate;
        this.img_url = img_url;
        this.mID = mID;
        this.reviews = reviews;
        this.rvAdapter = rvAdapter;
        Log.d("HEREHERE", this.mID);
    }

    private void Regist() {

        final String movie_id = this.mID;
        final String replyer = this.user_name;
        final String reply = this.review_edit.getText().toString().trim();
        final String rating = Float.toString(this.rating_bar.getRating());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGREPLY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(getContext(), "리뷰 등록 완료!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "JSON오류" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "리뷰 등록 오류... " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("movie_id", movie_id);
                params.put("replyer", replyer);
                params.put("reply", reply);
                params.put("rating", rating);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
}