package com.example.whatsthatcar.ui.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val uri: MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }
}