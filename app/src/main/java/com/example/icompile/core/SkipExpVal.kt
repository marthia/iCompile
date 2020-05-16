package com.example.icompile.core

import android.util.Log
import java.util.*


class SkipExpVal(private val scanner: IScanner) : IParser{

    /*
   * the main stack to hold the the generated intermediate code
   *
   * */
    private val stack = Stack<String>()

    @Throws(SyntaxError::class)
    override fun execute(): String {

            skipA()

        if (stack.isNotEmpty()) return stack.pop()


        throw SyntaxError(scanner.getErrorInfo())
    }

    private fun skipA() {
        skipM()
        skipA1()
        Log.i("skipA", stack.toString())
    }

    private fun skipA1() {
        when (scanner.getTokenInList(arrayListOf("+", "-"))) {
            0 -> {
                scanner.getToken("+")
                skipM()
                doAction(SemanticActionEnum.SA_ADD)
                skipA1()
            }

            1 -> {
                scanner.getToken("-")
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
        when (scanner.getTokenInList(arrayListOf("*", "/"))) {
            0 -> {
                scanner.getToken("*")
                skipP()
                doAction(SemanticActionEnum.SA_MUL)
                skipM1()
            }
            1 -> {
                scanner.getToken("/")
                skipP()
                doAction(SemanticActionEnum.SA_DIV)
            }
            else -> {
            }// null
        }

    }

    private fun skipP() {
        when (scanner.getTokenInList(arrayListOf("-", "(", "#float"))) {

            0 -> {
                scanner.getToken("-")
                skipP()
                doAction(SemanticActionEnum.SA_NEG)
            }

            1 -> {
                scanner.getToken("(")
                skipA()
                scanner.getToken(")")
            }

            2 -> {
                doAction(SemanticActionEnum.SA_NUM, scanner.skipFloat())
            }
            else -> {
                throw SyntaxError(scanner.getErrorInfo("Expression element expected : - , ( , num"))
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
