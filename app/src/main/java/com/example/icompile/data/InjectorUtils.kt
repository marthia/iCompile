package com.example.icompile.data

import com.example.icompile.ui.viewmodel.CodeViewModelFactory

object InjectorUtils {
    private fun getNoteRepository(): CodeRepository {
        return CodeRepository.getInstance()
    }

    fun provideCodeRepository(): CodeViewModelFactory {
        val repository =
            getNoteRepository()
        return CodeViewModelFactory(repository)
    }


}