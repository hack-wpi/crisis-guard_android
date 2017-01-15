package com.shltr.darrieng.shltr_android.Model;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface UploaderServiceModel {
    String ENDPOINT = "http://hack.symerit.com/";

    @Multipart
    @POST("api/uploadProfilePicture")
    Call<ResponseBody> postImage(@Header("Authorization") String header, @PartMap Map<String, RequestBody> params);
}
