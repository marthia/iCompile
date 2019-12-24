package com.example.icompile.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.icompile.data.CodeRepository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class EditorViewModel(
    val repository: CodeRepository
) : ViewModel() {

    var codeTextUi = MutableLiveData<String>()

    fun getCode() {
        repository.getCode()
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {

                    codeTextUi.postValue(t)
                }

                override fun onComplete() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onSubscribe(d: Disposable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onError(e: Throwable) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
    }

}