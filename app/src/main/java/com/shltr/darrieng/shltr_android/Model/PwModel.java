package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.PasswordPojo;
import com.shltr.darrieng.shltr_android.Pojo.UserToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PwModel {
    String LOGIN_ENDPOINT = "http://hack.symerit.com/oauth/";

    @POST("token")
    Call<UserToken> loginUser(@Body PasswordPojo user);
}
