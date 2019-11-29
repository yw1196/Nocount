package com.example.hk.whywhy;

/**
 * Created by HK on 2019-11-20.
 */

public final class Enjoy {
    private String tagger;
    private String tag_text;
    private int tag_like;

    public Enjoy(String tagger,String tag_text,int tag_like){
        this.tagger = tagger;
        this.tag_text = tag_text;
        this.tag_like = tag_like;
    }


    public String getTagger() {return tagger;}
    public String getTag_text() {return tag_text;}
    public int getTag_like() {return tag_like;}

}
