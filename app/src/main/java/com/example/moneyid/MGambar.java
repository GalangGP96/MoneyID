package com.example.moneyid;

import com.google.gson.annotations.SerializedName;

public class MGambar {
    @SerializedName("id")
    private String id;
    @SerializedName("gambar")
    private String gambar;
    private String action;
    public MGambar (){}
    public MGambar(String id, String gambar, String action){
        this.id = id;
        this.gambar = gambar;
        this.action = action;
    }
    public String getId() {return id;}
    public void setId(String id) {
        this.id = id;
    }
    public String getGambar() {
        return gambar;
    }
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}
