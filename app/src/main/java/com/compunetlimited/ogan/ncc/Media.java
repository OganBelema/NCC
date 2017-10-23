package com.compunetlimited.ogan.ncc;

/**
 * Created by belema on 10/4/17.
 */

public class Media {

    private String audio_name;
    private String audio_url;

    public Media(String audio_name, String audio_url){
        this.audio_name = audio_name;
        this.audio_url = audio_url;
    }

    public Media(){

    }
    public String getAudio_name(){
        return audio_name;
    }

    public String getAudio_url(){
        return audio_url;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }
}
