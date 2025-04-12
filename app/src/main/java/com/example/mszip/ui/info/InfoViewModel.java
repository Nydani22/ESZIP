package com.example.mszip.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public InfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Info fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
