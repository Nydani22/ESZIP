package com.example.mszip.ui.foglal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FoglalViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public FoglalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is foglal fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
