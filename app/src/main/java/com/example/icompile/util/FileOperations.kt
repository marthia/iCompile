package com.example.icompile.util

import android.util.Log
import java.io.File
import java.nio.charset.Charset

fun saveTextFile(
    text: String, path: String
): Boolean {
    try {

        File(path).apply {
            writeText(text, Charset.defaultCharset())
        }

        return true
    } catch (e: Exception) {
        Log.i("saveTextFile", e.printStackTrace().toString())
        return false
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
