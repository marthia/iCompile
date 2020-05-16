package com.example.icompile.core

import android.graphics.Point

class SyntaxError
(
    private val errorInfo: String
) :


Exception() {
    fun print(): String {
        return errorInfo
    }
}