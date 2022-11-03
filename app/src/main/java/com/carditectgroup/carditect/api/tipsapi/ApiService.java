package com.carditectgroup.carditect.api.tipsapi;

import com.carditectgroup.carditect.model.Tips;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("random")
    Call<Tips> getTips();

}
