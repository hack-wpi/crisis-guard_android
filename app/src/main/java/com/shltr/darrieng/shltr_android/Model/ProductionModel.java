package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.CompleteIdentificationModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ProductionModel {
    String ENDPOINT = "http://hack.symerit.com/";

    @GET("api/getProductionJson")
    Call<CompleteIdentificationModel> pilferData(
        @Header("Authorization") String header, @Query("user_id") int user_id);
}
