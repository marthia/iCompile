package com.example.icompile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.icompile.data.CodeRepository

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