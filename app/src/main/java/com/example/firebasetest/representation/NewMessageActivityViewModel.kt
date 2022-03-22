package com.example.firebasetest.representation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewMessageActivityViewModel(): ViewModel() {
    private var searchBarFilterOption = MutableLiveData<String>()
    public var searchValueToLiveData: LiveData<String> = searchBarFilterOption
    public fun setValueToSearchBarLiveData(str: String){
        searchBarFilterOption.value = str
    }
}