package com.robo101.patientmonitoringsystem.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.robo101.patientmonitoringsystem.api.tipsapi.ApiClient;
import com.robo101.patientmonitoringsystem.api.tipsapi.ApiService;
import com.robo101.patientmonitoringsystem.response.TipsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipsRepository {

    private final ApiService apiService;

    public TipsRepository() {
        apiService = ApiClient.getTipsRetrofit().create(
                ApiService.class);
    }

    public MutableLiveData<TipsResponse> getTips() {
        MutableLiveData<TipsResponse> data = new MutableLiveData<>();
        apiService.getTips().enqueue(new Callback<TipsResponse>() {
            @Override
            public void onResponse(Call<TipsResponse> call, Response<TipsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TipsResponse> call, Throwable t) {
                Log.e("eeeerrrroooorrrrr", t.toString());
            }
        });
        return data;
    }
}
