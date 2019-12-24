package com.example.icompile.data

import android.os.Environment
import com.example.icompile.core.loadTextFromFile
import com.example.icompile.core.saveTextFile
import io.reactivex.Observable
import java.io.File

class CodeRepository() {


    fun getCode(): Observable<String> {

        val code = loadTextFromFile(
            "${Environment.getRootDirectory()}${CODE_TEXT_FOLDER_PATH}${CODE_TEXT_FILE}"
        ).orEmpty()

        return Observable.just(code)
    }


    fun saveCode(text: String): String {
        // create our own folder for any file : could be asked from user in future
        if (!File("${Environment.getRootDirectory()}${CODE_TEXT_FOLDER_PATH}").exists()) {
            File("${Environment.getRootDirectory()}${CODE_TEXT_FOLDER_PATH}${CODE_TEXT_FILE}")
                .mkdir()
        }

        return saveTextFile(
            text,
            "${Environment.getRootDirectory()}"
        )
    }

    companion object {

        private const val CODE_TEXT_FILE = "/main.txt"
        private const val CODE_TEXT_FOLDER_PATH = "/iCompile"
        // For Singleton instantiation
        @Volatile
        private var instance: CodeRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: CodeRepository().also { instance = it }
            }
    }
}