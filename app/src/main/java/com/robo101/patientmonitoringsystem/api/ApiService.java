package com.robo101.patientmonitoringsystem.api;

import com.robo101.patientmonitoringsystem.response.TipsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET(".")
    Call<TipsResponse> getTips();

}
