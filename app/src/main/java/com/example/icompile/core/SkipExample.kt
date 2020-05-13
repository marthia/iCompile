package com.example.icompile.core

import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList


class SkipExample() {

    fun execute() {
        skipS()
    }

    private fun skipS() {
        skipX()
        getToken("d")
        skipY()
    }

    private fun skipX() {

        when (getTokenInList(arrayListOf("a", "b"))) {
            0 -> {
                getToken("a")
                skipX()
            }
            1 -> {
            }// nothing
            else -> {
                abortSyntax("a and b expected")
            }
        }
    }

    private fun skipY() {
        when (getTokenInList(arrayListOf("b", "a", "d"))) {
            0 -> {
                getToken("b")
            }
            1, 2 -> {
            } // nothing
            else -> abortSyntax("a , b and d expected")
        }
    }
}
