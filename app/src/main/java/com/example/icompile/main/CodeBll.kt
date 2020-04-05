package com.example.icompile.main

import android.os.Environment
import com.example.icompile.compiler.Compiler
import com.example.icompile.util.loadTextFromFile
import com.example.icompile.util.saveTextFile
import java.io.File

object CodeBll {

    private const val CODE_TEXT_FILE = "/main.txt"
    private const val BYTECODE_FILE = "/main.cod"
    private const val CODE_TEXT_FOLDER_PATH = "/iCompile"
    var FULL_PATH =
        "${Environment.getExternalStorageDirectory()}${CODE_TEXT_FOLDER_PATH}${CODE_TEXT_FILE}"
    var FULL_PATH_BYTECODE =
        "${Environment.getExternalStorageDirectory()}${CODE_TEXT_FOLDER_PATH}${BYTECODE_FILE}"

    const val COMMENT_COLOR = "#d81b60"

    const val KEYWORD_COLOR = "#3b78e7"


    fun run(): String {

        val compiler = Compiler(FULL_PATH, FULL_PATH_BYTECODE)

        return compiler.compileProgram()

    }

    fun getCode(): String {

        // create our own folder for any file : could be asked from user in future
        if (!File("${Environment.getExternalStorageDirectory()}${CODE_TEXT_FOLDER_PATH}").exists()) {

            File("${Environment.getExternalStorageDirectory()}${CODE_TEXT_FOLDER_PATH}").mkdir()

            // need to create empty files for the app to work
            File(FULL_PATH).createNewFile()
            File(BYTECODE_FILE).createNewFile()


        } else {

            if (!File(FULL_PATH).exists()) File(FULL_PATH).createNewFile()

            if (!File(FULL_PATH_BYTECODE).exists()) File(FULL_PATH_BYTECODE).createNewFile()
        }

        return loadTextFromFile(FULL_PATH).orEmpty()
    }

    fun saveCode(text: String): String {

        return if (saveTextFile(text, FULL_PATH)) "Code Successfully saved"
        else "Error Saving the code, try again later"
    }

}