package com.robo101.patientmonitoringsystem.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.robo101.patientmonitoringsystem.model.Tips;
import com.robo101.patientmonitoringsystem.repository.TipsRepository;

public class TipsViewModel extends ViewModel {

    public TipsRepository tipsRepository;

    public TipsViewModel() {
        tipsRepository = new TipsRepository();
    }

    public MutableLiveData<Tips> getTips() {
        return tipsRepository.getTips();
    }
}
