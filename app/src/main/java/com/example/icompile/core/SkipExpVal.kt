package com.example.icompile.core

import android.util.Log
import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList
import com.example.icompile.core.Scanner.skipFloat
import com.example.icompile.core.Scanner.skipInt
import java.util.*


object SkipExpVal {
    private val stack = Stack<String>()

    fun execute(): String {
        skipA()
        return if (stack.size  != 0)
            stack.pop() ?: "Could not parse the phrase"
        else "Could not parse the phrase"
    }

    private fun skipA() {
        skipM()
        skipA1()
        Log.i("skipA", stack.toString())
    }

    private fun skipA1() {
        when (getTokenInList(arrayListOf("+", "-"))) {
            0 -> {
                getToken("+")
                skipM()
                doAction(SemanticActionEnum.SA_ADD)
                skipA1()
            }

            1 -> {
                getToken("-")
                skipM()
                doAction(SemanticActionEnum.SA_SUB)
                skipA1()
            }
            else -> {
            }
        }

    }

    private fun skipM() {
        skipP()
        Log.i("skipM", stack.toString())
        skipM1()
        Log.i("skipM", stack.toString())
    }

    private fun skipM1() {
        when (getTokenInList(arrayListOf("*", "/"))) {
            0 -> {
                getToken("*")
                skipP()
                doAction(SemanticActionEnum.SA_MUL)
                skipM1()
            }
            1 -> {
                getToken("/")
                skipP()
                doAction(SemanticActionEnum.SA_DIV)
            }
            else -> {
            }// null
        }

    }

    private fun skipP() {
        when (getTokenInList(arrayListOf("-", "(", "#int"))) {

            0 -> {
                getToken("-")
                skipP()
                doAction(SemanticActionEnum.SA_NEG)
            }

            1 -> {
                getToken("(")
                skipA()
                getToken(")")
            }

            2 -> {
                doAction(SemanticActionEnum.SA_NUM, skipInt())
            }
            else -> {
                abortSyntax("Expression element expected : - , ( , num")
            }
        }

    }


    private fun doAction(action: SemanticActionEnum, tokenVal: String = "0") {
        val l: String
        val r: String


        when (action) {

            SemanticActionEnum.SA_ADD -> {
                stack.push((stack.pop().toDouble() + stack.pop().toDouble()).toString())
            }

            SemanticActionEnum.SA_SUB -> {
                r = stack.pop()
                l = stack.pop()
                stack.push((l.toDouble() - r.toDouble()).toString())
            }

            SemanticActionEnum.SA_MUL -> {
                stack.push((stack.pop().toDouble() * stack.pop().toDouble()).toString())
            }

            SemanticActionEnum.SA_DIV -> {
                r = stack.pop()
                l = stack.pop()
                stack.push((l.toDouble() / r.toDouble()).toString())
            }

            SemanticActionEnum.SA_NEG -> {
                stack.push((-stack.pop().toDouble()).toString())
            }

            SemanticActionEnum.SA_NUM -> {
                stack.push(tokenVal)
            }

            else -> {} // ignore

        }
        Log.i("doAction", stack.toString())
    }


}
