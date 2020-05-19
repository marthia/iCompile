package com.example.icompile.core

import java.util.*
import kotlin.math.max

class SkipDepth (private val scanner: IScanner): IParser{

    /*
   * the main stack to hold the the generated intermediate code
   *
   * */
    private val stack = Stack<Int>()

    override fun execute(): String {
        skipS()

        if (stack.isNotEmpty()) return stack.pop().toString()

        throw SyntaxError(scanner.getErrorInfo())
    }

    @Throws(SyntaxError::class)
    private fun skipS() {
        when (scanner.getTokenInList(arrayListOf("(", "a"))) {

            0 -> {
                scanner.getToken("(")
                skipL()
                scanner.getToken(")")
                doAction(SemanticActionEnum.SA_INC)
            }
            1 -> {
                scanner.getToken("a");
                doAction(SemanticActionEnum.SA_ZERO)
            }
            else -> {
                throw SyntaxError("( , a Expected")
            }
        }
    }

    private fun skipL() {
        skipS()
        skipL1()
    }

    private fun skipL1() {
        if (scanner.isKeyword(",")) {
            scanner.getToken(",")
            skipS();
            doAction(SemanticActionEnum.SA_MAX);
            skipL1()
        }
    }

    private fun doAction(action: SemanticActionEnum) {
        when (action) {
            SemanticActionEnum.SA_ZERO -> {
                stack.push(0)
            }
            SemanticActionEnum.SA_INC -> {
                stack.push(stack.pop() + 1)
            }
            SemanticActionEnum.SA_MAX -> {
                stack.push(max(stack.pop(), stack.pop()))
            }
            else -> {} // ignore
        }
    }

}