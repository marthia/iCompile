package com.example.icompile.ui.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditorViewModel() : ViewModel() {

    val bottomShortCut = MutableLiveData<Int>().apply { value = View.GONE}
}