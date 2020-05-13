package com.example.icompile.core

import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList
import com.example.icompile.core.Scanner.isKeyword

class RegularExpression {

    fun skipOrs() {
        skipAnds()
        skipOrs1()
    }

    private fun skipAnds() {
        skipPrimary()
        skipAnds1()
    }

    private fun skipOrs1() {
        if (isKeyword("|")) {

            getToken("|")
            skipAnds()
            // Result : = Result+'|'; // Action
            skipOrs1()
        }
    }

    private fun skipPrimary() {
        skipElement()
        skipStar()
    }

    private fun skipAnds1() {
        if (isKeyword(".")) {
            getToken(".")
            skipPrimary()
            //Result := Result + '|'; // Action
            skipAnds1()
        }
    }

    private fun skipElement() {

        when (getTokenInList(listOf("(", "#id"))) {
            0 -> {
                getToken("(")
                skipOrs()
                getToken(")")
            }
//               1 -> return Action
            else -> abortSyntax("invalid")
        }
    }

    private fun skipStar() {
        skipStar()

        if (isKeyword("*")) {
            getToken("*")
//            Result := Result + '*'; // Action
            skipStar()
        }
    }

}
