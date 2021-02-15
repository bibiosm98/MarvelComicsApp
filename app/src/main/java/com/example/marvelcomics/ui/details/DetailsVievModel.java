package com.example.marvelcomics.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailsVievModel extends ViewModel {

        private MutableLiveData<String> mText;

        public DetailsVievModel() {
            mText = new MutableLiveData<>();
            mText.setValue("This is details fragment");
        }

        public LiveData<String> getText() {
            return mText;
        }
}