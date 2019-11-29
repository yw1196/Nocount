package com.example.hk.whywhy;

/**
 * Created by HK on 2019-10-07.
 */

public class ItemObject {
    private String title;
    private String img_url;
    private String detail_link;
    private String release;
    private String director;
    private String rate;


    public ItemObject(String title, String url, String link, String release, String director, String rate){
        this.title = title;
        this.img_url = url;
        this.detail_link = link;
        this.release = release;
        this.director = director;
        this.rate = rate;
    }


    public  String getRate() {return rate; }

    public String getTitle() {
        return title;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getDetail_link() {
        return detail_link;
    }

    public String getRelease() {
        return release;
    }

    public String getDirector() {
        return director;
    }
}
