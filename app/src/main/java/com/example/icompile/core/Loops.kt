package com.example.icompile.core
//
//class Loops : Scanner() {
//
//    fun skipS() {
//        skipI()
//        skipW()
//        skipF()
//        skipA()
//    }
//
//    private fun skipI() {
//        when {
//            isKeyword("if") -> skipE()
//            isKeyword("then") -> skipS()
//            else -> skipII()
//        }
//    }
//
//    private fun skipII(): Unit? {
//        return when {
//            isKeyword("else") -> skipS()
//            else -> return null
//
//        }
//    }
//
//    private fun skipE() {
//
//    }
//
//    private fun skipW() {
//        when {
//            isKeyword("while") -> skipE()
//            isKeyword("do") -> skipS()
//        }
//    }
//
//    private fun skipF() {
//        when {
//            isKeyword("for") -> skipId()
//            isKeyword(":=") -> skipE()
//            isKeyword("to") -> skipE()
//            isKeyword("do") -> skipS()
//        }
//    }
//
//    private fun skipA() {
//        skipId()
//        skipEE()
//    }
//
//    fun skipEE() {
//
//    }
//
//
//}