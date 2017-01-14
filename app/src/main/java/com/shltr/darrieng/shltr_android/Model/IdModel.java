package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.UserId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface IdModel {
    String ENDPOINT = "http://hack.symerit.com/";

    @GET("api/getUserId")
    Call<UserId> retrieveId(@Header("Authorization") String header, @Query("email") String email);
}
