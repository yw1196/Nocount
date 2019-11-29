package com.example.hk.whywhy;

/**
 * Created by HK on 2019-11-17.
 */

public class Review {

    private String replyer;
    private String reply;
    private String rating;
    private String regdate;
    private String likeno;

    public Review(String replyer, String reply, String rating, String likeno, String regdate) {
        this.replyer = replyer;
        this.reply = reply;
        this.rating = rating;
        this.likeno = likeno;
        this.regdate = regdate;
    }

    public String getReplyer() {return replyer;}
    public String getReply() {return reply;}
    public String getRating() {return rating;}
    public String getRegdate() {return regdate;}
    public String getLikeno() {return likeno;}

}
