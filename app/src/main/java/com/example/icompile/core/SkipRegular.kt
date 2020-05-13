package com.example.icompile.core

import android.util.Log
import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList
import com.example.icompile.core.Scanner.isKeyword
import com.example.icompile.core.Scanner.skipId
import java.util.*

object SkipRegular {
    private val stack = Stack<String>()

    fun execute(): String {
        skipR()
        return if (stack.size != 0)
            stack.pop() ?: "Couldn't resolve the provided code!"
        else "Couldn't resolve the provided code!"
    }

    private fun skipR() {
        skipN()
        skipR1()
    }

    private fun skipR1() {
        if (isKeyword("|")) {
            getToken("|");
            skipN()
            doAction(SemanticActionEnum.SA_OR, "|");
            skipR1()
        }
    }

    private fun skipN() {
        skipS()
        skipN1()
    }

    private fun skipN1() {
        if (isKeyword(".")) {
            getToken(".")
            skipS()
            doAction(SemanticActionEnum.SA_DOT, ".")
            skipN1()
        }
    }

    private fun skipS() {
        skipP()
        skipS1()
    }

    private fun skipS1() {
        if (isKeyword("*")) {
            getToken("*")
            doAction(SemanticActionEnum.SA_STAR)
            skipS1()
        }
    }

    private fun skipP() {
        when (getTokenInList(arrayListOf("(", "#id"))) {
            0 -> {
                getToken("(")
                skipR()
                getToken(")")
            }
            1 -> {
                doAction(SemanticActionEnum.SA_ID, skipId())
            }
            else -> abortSyntax("Regular element expected: ( , id")
        }
    }

    private fun doAction(action: SemanticActionEnum, tokenVal: String = "0") {
        val l: String
        val r: String


        when (action) {

            SemanticActionEnum.SA_OR, SemanticActionEnum.SA_DOT -> {
                r = stack.pop()
                l = stack.pop()
                stack.push(tokenVal + l + r)
            }

            SemanticActionEnum.SA_STAR -> {
                stack.push("*" + stack.pop())
            }

            SemanticActionEnum.SA_ID -> {
                stack.push(tokenVal)
            }

            else -> {
            } // ignore

        }
        Log.i("doAction", stack.toString())
    }
}
