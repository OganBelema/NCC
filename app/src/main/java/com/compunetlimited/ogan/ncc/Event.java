package com.compunetlimited.ogan.ncc;

/**
 * Created by belema on 9/27/17.
 */

public class Event {

    private String date;
    private String event;

    public Event(String date, String event){
        this.date = date;
        this.event = event;
    }

    public Event(){

    }

    public void setDate(String date){
        this.date = date;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public String getDate(){
        return date;
    }

    public String getEvent(){
        return event;
    }

}