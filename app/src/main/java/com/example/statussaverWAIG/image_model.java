package com.example.statussaverWAIG;

import java.io.Serializable;

public class image_model implements Serializable {
    private String thumb,original,large2x,large,medium,small,portrait,landscape,id=null,link;
    private String[][] src;
    private boolean IsVideo = false;
    private int p=0,column_index;

    protected image_model(final String thumb_img_url,final String original,final String large2x,final String large
            ,final String medium,final String small,final String portrait,final String landscape) {
        this.thumb = thumb_img_url;
        this.original = original;
        this.large2x = large2x;
        this.large = large;
        this.medium = medium;
        this.small = small;
        this.portrait = portrait;
        this.landscape = landscape;
    }

    protected image_model(final String thumb_img_url,final String[][] src){
        this.IsVideo = true;
        this.thumb = thumb_img_url;
        this.src = src;
    }
    protected image_model(final String original,final String  id){
        this.original = original;
        this.id = id;
    }
    protected image_model(final String original,final boolean IsVideo){
        this.original = original;
        this.IsVideo = IsVideo;
    }
    protected image_model(final String original,final String  id,final boolean IsVideo){
        this.original = original;
        this.id = id;
        this.IsVideo = IsVideo;
    }
    protected image_model(final String[][] src){
        this.src = src;
        this.thumb = src[0][1];
    }
    protected image_model(final int column_index,final String thumb_img_url,final String[][] src,final boolean IsVideo,final String  id,final String link){
        this.column_index = column_index;
        this.id = id;
        this.link = link;
        if(IsVideo){
            this.IsVideo = true;
            this.thumb = thumb_img_url;
            this.src = src;
        }else{
            this.src = src;
            this.thumb = src[0][1];
        }
    }
    protected int getColumn_index(){
        return this.column_index;
    }
    protected String getLink(){
        return this.link;
    }
    protected String getID(){
        return this.id;
    }
    protected boolean isVideo() {
        return this.IsVideo;
    }

    protected String[][] getVideosrc() {
        return this.src;
    }

    protected String[][] getImagerc(final int temp) {
        p = 0;
        final String[][] temparry = new String[8][2];
        if(temp==4){
            if(!this.medium.isEmpty()){
                temparry[p][0] = "Medium";
                temparry[p][1] = this.medium;
                p++;
            }if(!this.portrait.isEmpty()){
                temparry[p][0] = "Portrait";
                temparry[p][1] = this.portrait;
                p++;
            }
        }else{
            if(!this.medium.isEmpty()){
                temparry[p][0] = "Portrait";
                temparry[p][1] = this.portrait;
                p++;

            }if(!this.portrait.isEmpty()){
                temparry[p][0] = "Medium";
                temparry[p][1] = this.medium;
                p++;
            }
        }
        if(!this.original.isEmpty()){
            temparry[p][0] = "Original";
            temparry[p][1] = this.original;
            p++;
        }if(!this.large2x.isEmpty()){
            temparry[p][0] = "Large2x";
            temparry[p][1] = this.large2x;
            p++;
        }if(!this.large.isEmpty()){
            temparry[p][0] = "Large";
            temparry[p][1] = this.large;
            p++;
        }if(!this.small.isEmpty()){
            temparry[p][0] = "Small";
            temparry[p][1] = this.small;
            p++;
        }if(!this.landscape .isEmpty()){
            temparry[p][0] = "Landscape";
            temparry[p][1] = this.landscape ;
            p++;
        }if(!this.thumb.isEmpty()){
            temparry[p][0] = "Thumbnail";
            temparry[p][1] = this.thumb;
            p++;
        }
        this.src = new String[p][2];
        for(int y=0;y<p;y++){
            this.src[y][0] = temparry[y][0];
            this.src[y][1] = temparry[y][1];
        }
        return this.src;
    }

    protected String getThumb() {
        if(this.thumb.isEmpty()){
            return this.original;
        }else{
            return this.thumb;
        }
    }

    protected String getoriginal() {
        return this.original;
    }

    protected String getLarge2x() {
        return this.large2x;
    }

    protected String getLarge() {
        return this.large;
    }

    protected String getMedium() {
        return this.medium;
    }

    protected String getSmall() {
        return this.small;
    }

    protected String getPortrait() {
        return this.portrait;
    }

    protected String getLandscape() {
        return this.landscape;
    }
}
