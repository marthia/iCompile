package com.example.icompile.core

class SyntaxError
    (
    private val errorInfo: String
) :

    Exception() {

    fun print(): String {
        return errorInfo
    }
}