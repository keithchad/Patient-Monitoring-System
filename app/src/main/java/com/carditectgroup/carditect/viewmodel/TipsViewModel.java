package com.carditectgroup.carditect.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.carditectgroup.carditect.model.Tips;
import com.carditectgroup.carditect.repository.TipsRepository;

public class TipsViewModel extends ViewModel {

    public TipsRepository tipsRepository;

    public TipsViewModel() {
        tipsRepository = new TipsRepository();
    }

    public MutableLiveData<Tips> getTips() {
        return tipsRepository.getTips();
    }
}
