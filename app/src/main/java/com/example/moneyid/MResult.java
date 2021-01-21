package com.example.moneyid;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MResult {
    @SerializedName("status")
    private String status;
    @SerializedName("result")
    private List<MGambar> result = new ArrayList<MGambar>();
    @SerializedName("message")
    private String message;
    @SerializedName("res")
    private String res;
    @SerializedName("nominal")
    private String nominal;
    public MResult() {}
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<MGambar> getResult() {
        return result;
    }
    public void setResult(List<MGambar> result) {
        this.result = result;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getRes() {
        return res;
    }
    public void setRes(String res) {
        this.res = res;
    }
    public String getNominal() {
        return nominal;
    }
    public void setNominal(String nominal) {
        this.nominal = nominal;
    }
}
