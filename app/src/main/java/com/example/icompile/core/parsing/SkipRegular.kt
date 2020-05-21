package com.example.icompile.core.parsing

import android.util.Log
import com.example.icompile.core.lexing.IScanner
import com.example.icompile.core.SemanticActionEnum
import com.example.icompile.core.SyntaxError
import java.util.*

class SkipRegular(private val scanner: IScanner) :
    IParser {

    /*
   * the main stack to hold the the generated intermediate code
   *
   * */
    private val stack = Stack<String>()

    override fun execute(): String {
        skipR()

        if (stack.isNotEmpty()) return stack.pop()

        throw SyntaxError(scanner.getErrorInfo())
    }

    private fun skipR() {
        skipN()
        skipR1()
    }

    private fun skipR1() {
        if (scanner.isKeyword("|")) {
            scanner.getToken("|");
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
        if (scanner.isKeyword(".")) {
            scanner.getToken(".")
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
        if (scanner.isKeyword("*")) {
            scanner.getToken("*")
            doAction(SemanticActionEnum.SA_STAR)
            skipS1()
        }
    }

    private fun skipP() {
        when (scanner.getTokenInList(arrayListOf("(", "#id"))) {
            0 -> {
                scanner.getToken("(")
                skipR()
                scanner.getToken(")")
            }
            1 -> {
                doAction(SemanticActionEnum.SA_ID, scanner.skipId())
            }
            else -> throw SyntaxError("Regular element expected: ( , id")
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
