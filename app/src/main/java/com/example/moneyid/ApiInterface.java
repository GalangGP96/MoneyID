package com.example.moneyid;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("Home/user")
    Call<MResult> postGambar(@Part MultipartBody.Part file,
                             @Part("id") RequestBody id,
                             @Part("nominal") RequestBody
                                     nominal,
                             @Part("keaslian") RequestBody
                                     keaslian,
                             @Part("action") RequestBody
                                     action);
    @Multipart
    @POST("Citra/user")
    Call<MResult> postUji(@Part MultipartBody.Part file,
                          @Part("id") RequestBody id,
                          @Part("action") RequestBody action);
}
