package com.example.tracker.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    val loader = MutableLiveData<Boolean>()
}