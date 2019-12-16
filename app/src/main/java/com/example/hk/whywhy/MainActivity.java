package com.example.hk.whywhy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.AsyncTask;
import android.util.Log;

//menu
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ScrollView sv1, sv2;
    private RecyclerView recyclerView, recyclerView2, mRcyclerViewMovieList;

    //네이버api용 코드
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;

    private final static String IMAGE = "image";
    private final static String LINK = "link";
    private final static String TITLE = "title";
    private final static String USERRATING = "userRating";
    private final static String PUBDATE = "pubDate";
    private final static String DIRECTOR = "director";
    private final static String ACTOR = "actor";

    private final static String TAGBEFORE = "before";
    private final static String TAGAFTER = "after";

    //크롤링 arraylist
    private ArrayList<ItemObject> list = new ArrayList();
    private ArrayList<ItemObject> list2 = new ArrayList();

    //recyclerview 제한 카운터
    private int counter = 0;
    private int counter2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerview2);
        mRcyclerViewMovieList = (RecyclerView) findViewById(R.id.recyclerViewMovieList);
        sv1 = (ScrollView) findViewById(R.id.sv1);
        sv2 = (ScrollView) findViewById(R.id.sv2);
        sv2.setVisibility(View.GONE);

        //AsyncTask 작동시킴(파싱)
        new Description().execute();
    }

    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }


    private class Description extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://movie.naver.com/movie/running/current.nhn").timeout(10*1000).get(); // 10초는 기다려 드릴게
                Document doc2 = Jsoup.connect("https://movie.naver.com/movie/running/premovie.nhn").timeout(10*1000).get();

                Elements mElementDataSize = doc.select("ul[class=lst_detail_t1]").select("li"); //필요한 녀석만 꼬집어서 지정
                Elements mElementDataSize2 = doc2.select("ul[class=lst_detail_t1]").select("li");

                int mElementSize = mElementDataSize.size(); //목록이 몇개인지 알아낸다. 그만큼 루프를 돌려야 하나깐.
                int mElementSize2 = mElementDataSize2.size();

                for(Element elem : mElementDataSize){ //이렇게 요긴한 기능이
                    //영화목록 <li> 에서 다시 원하는 데이터를 추출해 낸다.
                    String my_title = elem.select("li dt[class=tit] a").text();
                    String my_link = elem.select("li div[class=thumb] a").attr("href");
                    String my_imgUrl = elem.select("li div[class=thumb] a img").attr("src");
                    //특정하기 힘들다... 저 앞에 있는집의 오른쪽으로 두번째집의 건너집이 바로 우리집이야 하는 식이다.
                    Element rElem = elem.select("dl[class=info_txt1] dt").next().first();
                    String my_release = rElem.select("dd").text();
                    Element dElem = elem.select("dt[class=tit_t2]").next().first();
                    String my_director = "감독: " + dElem.select("a").text();
                    String my_rate = "네이버 평점:" + elem.select("li div[class=star_t1] span[class=num]").text();
                    Log.d("현재상영작 감독 - ",my_director);
                    //Log.d("test", "test" + mTitle);
                    //ArrayList에 계속 추가한다.
                    list.add(new ItemObject(my_title, my_imgUrl, my_link, my_release, my_director, my_rate));
                    counter++;
                    if(counter >= 10)
                        break;
                }

                for (Element elem : mElementDataSize2) {
                    String my_title2 = elem.select("li dt[class=tit] a").text();
                    String my_link2 = elem.select("li div[class=thumb] a").attr("href");
                    String my_imgUrl2 = elem.select("li div[class=thumb] a img").attr("src");
                    //특정하기 힘들다... 저 앞에 있는집의 오른쪽으로 두번째집의 건너집이 바로 우리집이야 하는 식이다.
                    Element rElem2 = elem.select("dl[class=info_txt1] dt").next().first();
                    String my_release2 = rElem2.select("dd").text();
                    Element dElem2 = elem.select("dl > dd:nth-child(3) > dl > dt.tit_t2").next().first();
                    String my_director2 = "감독: " + dElem2.select("a").text();
                    Log.d("예정작 감독 - ", my_director2);
                    list2.add(new ItemObject(my_title2, my_imgUrl2, my_link2, my_release2, my_director2, " "));
                    counter2++;
                    if(counter2 >= 10)
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //ArraList를 인자로 해서 어답터와 연결한다.
            MyAdapter myAdapter = new MyAdapter(list, MainActivity.this);                       //HORIZONTAL로 바꿔주는 코드
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            MyAdapter myAdapter2 = new MyAdapter(list2, MainActivity.this);
            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView2.setLayoutManager(layoutManager2);
            recyclerView2.setAdapter(myAdapter2);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQueryHint("영화 검색");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {

                sv1.setVisibility(View.GONE);
                sv2.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                callMovieSearchAPI(s);

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sv1.setVisibility(View.VISIBLE);
                sv2.setVisibility(View.GONE);
                return false;
            }
        });

        final SessionManager sessionManager = new SessionManager(this);

        MenuItem item_login = menu.add(0,0,0, "로그인");
        MenuItem item_logout = menu.add(0, 0, 0, "로그아웃");
        MenuItem item_myinfo = menu.add(0,0,0, "내정보");

        if(sessionManager.isLogin()) {
            item_login.setEnabled(false);
        } else {
            item_logout.setEnabled(false);
            item_myinfo.setEnabled(false);
        }

        item_login.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
        });

        item_logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sessionManager.logout();
                return true;
            }
        });

        item_myinfo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                HashMap<String, String> user = sessionManager.getUserDetail();
                String name = user.get(sessionManager.NAME);
                String email = user.get(sessionManager.EMAIL);
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            //to do

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //네이버 api

    public final void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter) {
        Log.d("test-setRecyclerAdapter", TAGBEFORE);
        mRecyclerViewAdapter = recyclerAdapter;
        Log.d("test-setRecyclerAdapter", mRecyclerViewAdapter + TAGAFTER);
    }

    public final void setRecyclerView() {
        Log.d("test-setRecyclerView", TAGBEFORE);
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRcyclerViewMovieList.setLayoutManager(mRecyclerViewLayoutManager);
        mRcyclerViewMovieList.setAdapter(mRecyclerViewAdapter);
        Log.d("test-setRecyclerView", mRecyclerViewAdapter + TAGAFTER);
    }

    public final void refreshRecyclerView() {
        Log.d("test-refresh", TAGBEFORE);
        mRecyclerViewAdapter.notifyDataSetChanged();
        Log.d("test-refresh", mRecyclerViewAdapter + TAGAFTER);
    }

    /*private String getMovieSearchURL() throws UnsupportedEncodingException {
        String movieSearchText = mEditTextMovieSearch.getText().toString();
        Log.d("test-movieSearchText", movieSearchText);
        String text = URLEncoder.encode(movieSearchText, "UTF-8");
        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text; // json 결과

        return apiURL;
    }*/

    private HttpURLConnection makeMovieSearchConnection(URL apiEndPoint) throws IOException {
        String clientId = "IT9hEd58EVBQaYX18Ot4";
        String clientSecret = "MZTgAOHFee";
        HttpURLConnection myConnection =
                (HttpURLConnection) apiEndPoint.openConnection();

        Log.d("test-myConnection", myConnection.toString());

        myConnection.setRequestMethod("GET");
        myConnection.setRequestProperty("X-Naver-Client-Id", clientId);
        myConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);
        return myConnection;
    }

    private Map<Integer, Movie> makeCachedMoviesForRecycle(JSONArray movieList) throws JSONException, IOException {
        Map<Integer, Movie> mCachedMovies = new LinkedHashMap<>();

        for (int movieListIdx = 0; movieListIdx < movieList.length(); ++movieListIdx) {
            JSONObject movie = movieList.getJSONObject(movieListIdx);

            Bitmap bmp = find(movie);

            Movie movieModel = makeMovie(movie, bmp);

            mCachedMovies.put(movieListIdx, movieModel);
        }

        return mCachedMovies;
    }

    private Bitmap find(JSONObject movie) throws JSONException, IOException {
        String image = movie.getString(IMAGE);
        if (image.length() > 1) {
            URL imageURL = new URL(image);
            HttpURLConnection myConnection = (HttpURLConnection) imageURL.openConnection();
            return BitmapFactory.decodeStream(myConnection.getInputStream());
        }
        return null;
    }

    private Movie makeMovie(JSONObject movie, Bitmap bmp) throws JSONException {

        return new Movie(
                movie.getString(LINK),
                bmp,
                movie.getString(TITLE),
                movie.getString(USERRATING),
                movie.getString(PUBDATE),
                movie.getString(DIRECTOR),
                movie.getString(ACTOR));
    }

    private void callMovieSearchAPI(final String ss) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here
                BufferedReader br = null;

                try {
                    // Create URL
                    // 클라우드 url에는 port를 안붙인다.
                    String movieSearchText = ss;
                    Log.d("test-movieSearchText", movieSearchText);
                    String text = URLEncoder.encode(movieSearchText, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;
                    URL apiEndpoint = new URL(apiURL);

                    HttpURLConnection myConnection = makeMovieSearchConnection(apiEndpoint);

                    int responseCode = myConnection.getResponseCode();

                    Log.d("test-responseCode", Integer.toString(responseCode));

                    if (responseCode == 200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(myConnection.getErrorStream()));
                    }

                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }

                    Log.d("test-response", response.toString());

                    JSONObject jsonObj = new JSONObject(response.toString());
                    final JSONArray movieList = jsonObj.getJSONArray("items");

                    br.close();
                    myConnection.disconnect();

                    final Map<Integer, Movie> mCachedMovies = makeCachedMoviesForRecycle(movieList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("test-runOnUiThread", TAGBEFORE);

                            mRecyclerViewAdapter = new RecyclerAdapter(getApplicationContext(), mCachedMovies);

                            setRecyclerAdapter(mRecyclerViewAdapter);
                            setRecyclerView();
                            refreshRecyclerView();
                            Log.d("test-runOnUiThread", TAGAFTER);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
