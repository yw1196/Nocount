package com.example.hk.whywhy;

/**
 * Created by HK on 2019-11-20.
 */

public final class Enjoy {
    private String tno;
    private String tagger;
    private String tag_text;
    private String tag_like;

    public Enjoy(String tno, String tagger,String tag_text, String tag_like){
        this.tno = tno;
        this.tagger = tagger;
        this.tag_text = tag_text;
        this.tag_like = tag_like;
    }

    public String getTno() {return tno;}
    public String getTagger() {return tagger;}
    public String getTag_text() {return tag_text;}
    public String getTag_like() {return tag_like;}

}
