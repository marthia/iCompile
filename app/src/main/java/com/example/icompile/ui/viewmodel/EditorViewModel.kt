package com.example.icompile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.icompile.data.CodeRepository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.IoScheduler

class EditorViewModel(
    private val repository: CodeRepository
) : ViewModel() {


    fun getCode(): String {

        return repository.getCode()

    }

    fun setCode(text: String) {
        repository.saveCode(text)
    }

}