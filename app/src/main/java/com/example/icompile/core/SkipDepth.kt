package com.example.icompile.core

import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList
import com.example.icompile.core.Scanner.isKeyword
import java.util.*
import kotlin.math.max

class SkipDepth {
    private val stack = Stack<Int>()

    private fun skipS() {
        when (getTokenInList(arrayListOf("(", "a"))) {

            0 -> {
                getToken("(")
                skipL()
                getToken(")")
                this.doAction(SemanticActionEnum.SA_INC)
            }
            1 -> {
                getToken("a");
                this.doAction(SemanticActionEnum.SA_ZERO)
            }
            else -> {
                abortSyntax("( , a Expected")
            }
        }
    }

    private fun skipL() {
        skipS()
        skipL1()
    }

    private fun skipL1() {
        if (isKeyword(",")) {
            getToken(",")
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