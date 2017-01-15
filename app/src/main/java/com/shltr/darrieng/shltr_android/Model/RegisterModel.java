package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.RegisterPojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterModel {
    String LOGIN_ENDPOINT = "http://hack.symerit.com/";

    @POST("register")
    Call<Void> createUser(@Body RegisterPojo user);
}
