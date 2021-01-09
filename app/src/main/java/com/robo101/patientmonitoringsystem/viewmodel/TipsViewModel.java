package com.robo101.patientmonitoringsystem.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.robo101.patientmonitoringsystem.repository.TipsRepository;
import com.robo101.patientmonitoringsystem.response.TipsResponse;

public class TipsViewModel extends ViewModel {

    public TipsRepository tipsRepository;

    public TipsViewModel() {
        tipsRepository = new TipsRepository();
    }

    public MutableLiveData<TipsResponse> getTips() {
        return tipsRepository.getTips();
    }
}
