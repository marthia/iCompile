package com.example.icompile.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.icompile.data.CodeRepository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.IoScheduler

class EditorViewModel(
    val repository: CodeRepository
) : ViewModel() {

    var codeTextUi = MutableLiveData<String>()

    fun getCode() {

        repository.getCode()
            .observeOn(IoScheduler())
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {

                    codeTextUi.postValue(t)
                }

                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun setCode(text: String) {
        repository.saveCode(text)
    }

}