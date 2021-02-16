package com.robo101.patientmonitoringsystem.api.tipsapi;

import com.robo101.patientmonitoringsystem.model.Tips;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("random")
    Call<Tips> getTips();

}
