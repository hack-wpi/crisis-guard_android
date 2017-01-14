package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.UserPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface UserRetrievalModel {
    String ENDPOINT = "http://hack.symerit.com/";

    @GET("api/nearByProfile")
    Call<UserPojo> retrieveId(@Header("Authorization") String header, @Query("email") String email);
}
