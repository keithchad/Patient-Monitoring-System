package com.robo101.patientmonitoringsystem.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.robo101.patientmonitoringsystem.api.tipsapi.ApiClient;
import com.robo101.patientmonitoringsystem.api.tipsapi.ApiService;
import com.robo101.patientmonitoringsystem.model.Tips;


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
