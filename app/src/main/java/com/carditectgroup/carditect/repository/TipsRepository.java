package com.carditectgroup.carditect.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carditectgroup.carditect.api.tipsapi.ApiClient;
import com.carditectgroup.carditect.api.tipsapi.ApiService;
import com.carditectgroup.carditect.model.Tips;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipsRepository {

    private final ApiService apiService;

    public TipsRepository() {
        apiService = ApiClient.getTipsRetrofit().create(
                ApiService.class);
    }

    public MutableLiveData<Tips> getTips() {

        MutableLiveData<Tips> data = new MutableLiveData<>();

        apiService.getTips().enqueue(new Callback<Tips>() {
            @Override
            public void onResponse(@NonNull Call<Tips> call, @NonNull Response<Tips> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Tips> call, @NonNull Throwable t) {

            }
        });
        return data;
    }
}
