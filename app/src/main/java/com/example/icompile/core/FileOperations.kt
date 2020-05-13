package com.example.icompile.core

import android.util.Log
import java.io.File
import java.nio.charset.Charset
import kotlin.String

fun saveTextFile(
    text: String, path: String
): String {
    try {

        File(path).apply {
            writeText(text, Charset.defaultCharset())
        }

        return "Successfully saved"
    } catch (e: Exception) {
        Log.i("saveTextFile", e.printStackTrace().toString())
        return "Could not Save changes. see logs for more info"
    }
}

fun loadTextFromFile(
    path: String
): String? {
    try {
        File(path).apply {
            return readText(Charset.defaultCharset())
        }

    } catch (e: Exception) {
        Log.i("loadFromFile", e.printStackTrace().toString())
        return null
    }
}
