package com.example.icompile.core

class Loops : Scanner() {

    fun skipS() {
        skipI()
        skipW()
        skipF()
        skipA()
    }

    fun skipI() {
        when {
            isKeyword("if") -> skipE()
            isKeyword("then") -> skipS()
            else -> skipII()
        }
    }

    fun skipII(): Unit? {
        return when {
            isKeyword("else") -> skipS()
            else -> return null

        }
    }

    fun skipE() {

    }

    fun skipW() {
        when {
            isKeyword("while") -> skipE()
            isKeyword("do") -> skipS()
        }
    }

    fun skipF() {
        when {
            isKeyword("for") -> skipId()
            isKeyword(":=") -> skipE()
            isKeyword("to") -> skipE()
            isKeyword("do") -> skipS()
        }
    }

    fun skipA() {
        skipId()
        skipEE()
    }

    fun skipEE() {

    }


}