package com.example.icompile.core

class DecisionHelper : CoreTextParser() {

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

            skipKeyword("|")
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
            skipKeyword(".")
            skipPrimary()
            //Result := Result + '|'; // Action
            skipAnds1()
        }
    }

    private fun skipElement() {

        when (getKeywordAt(listOf("(", "#id"))) {
            0 -> {
                skipKeyword("(")
                skipOrs()
                skipKeyword(")")
            }
//               1 -> return Action
            else -> "invalid".abortSyntax()
        }
    }

    private fun skipStar() {
        skipStar()

        if (isKeyword("*")) {
            skipKeyword("*")
//            Result := Result + '*'; // Action
            skipStar()
        }
    }

}