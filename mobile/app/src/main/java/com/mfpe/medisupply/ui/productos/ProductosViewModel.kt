package com.mfpe.medisupply.ui.productos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductosViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Productos"
    }
    val text: LiveData<String> = _text
}

