package com.example.icompile.core

import android.util.Log
import com.example.icompile.data.Code
import java.util.*

class SkipExpression(private val scanner: IScanner) : IParser{

    /*
    * the main stack to hold the the generated intermediate code
    *
    * */
    private val stack = Stack<String>()

    /*
    * the variable to keep track of code changes
    *
    * */
    private var codes = ArrayList<Code>()

    override fun execute(): String {
        skipOr();

        return if (stack.size != 0)
            stack.pop() ?: "Error Parsing the phrase"
        else "Error Parsing the phrase"
    }

    private fun skipOr() {
        skipAnd()
        skipOr1()
    }

    private fun skipAnd() {
        skipComp()
        skipAnd1()
    }

    private fun skipAnd1() {
        scanner.getToken("&&")
        skipComp()
        doAction(SemanticActionEnum.SA_AND, "&&")
        skipAnd1()
    }

    private fun skipOr1() {
        scanner.getToken("||")
        skipAnd()
        doAction(SemanticActionEnum.SA_OR, "||")
        skipOr1()
    }

    private fun skipComp() {
        skipAdds()
        skipComp1()
    }

    private fun skipComp1() {
        when (scanner.getTokenInList(arrayListOf(">=", "!=", "<=","==", "<", ">"))) {

            0 -> {
                scanner.getToken(">=")
                skipAdds()
                doAction(SemanticActionEnum.SA_GREAT_EQ, ">=");
            }

            1 -> {
                scanner.getToken("!=")
                skipAdds()
                doAction(SemanticActionEnum.SA_NOT_EQ, "!=");
            }

            2 -> {
                scanner.getToken("<=")
                skipAdds()
                doAction(SemanticActionEnum.SA_LESS_EQ, "<=");
            }

            3 -> {
                scanner.getToken("==")
                skipAdds()
                doAction(SemanticActionEnum.SA_EQUAL, "==");
            }

            4 -> {
                scanner.getToken("<")
                skipAdds()
                doAction(SemanticActionEnum.SA_LESS, "<");

            }

            5 -> {
                scanner.getToken(">")
                skipAdds()
                doAction(SemanticActionEnum.SA_GREAT, ">");
            }
            else -> {
            } // nothing
        }

    }

    private fun skipAdds() {
        skipMuls()
        skipAdds1()
    }

    private fun skipAdds1() {
        when (scanner.getTokenInList(arrayListOf("+", "-"))) {

            0 -> {
                scanner.getToken("+")
                skipMuls()
                doAction(SemanticActionEnum.SA_ADD, "+");
                skipAdds1()
            }

            1 -> {
                scanner.getToken("-")
                skipMuls()
                doAction(SemanticActionEnum.SA_SUB, "-");
                skipAdds1()
            }
            else -> {
            } // nothing

        }
    }

    private fun skipMuls() {
        skipPrimary()
        skipMuls1()
    }

    fun skipMuls1() {
        when (scanner.getTokenInList(arrayListOf("*", "/"))) {

            0 -> {
                scanner.getToken("*")
                skipPrimary()
                doAction(SemanticActionEnum.SA_MUL, "*");
                skipMuls1()
            }
            1 -> {
                scanner.getToken("/")
                skipPrimary()
                doAction(SemanticActionEnum.SA_DIV, "/");
                skipMuls1()
            }
            else -> {
            } // nothing
        }
    }

    @Throws(SyntaxError::class)
    private fun skipPrimary() {
        when (scanner.getTokenInList(arrayListOf("!", "-", "(", "#id", "#str", "#float"))) {
            0 -> {
                scanner.getToken("!")
                skipPrimary()
                doAction(SemanticActionEnum.SA_NOT, "!");
            }

            1 -> {
                scanner.getToken("-")
                skipPrimary()
                doAction(SemanticActionEnum.SA_NEG, "-")
            }
            2 -> {
                scanner.getToken("(")
                skipOr()
                scanner.getToken(")")
            }
            3 -> {
                doAction(SemanticActionEnum.SA_ID, scanner.skipId())
            }
            4 -> {
                doAction(SemanticActionEnum.SA_STR, scanner.skipStr())
            }

            5 -> {
                doAction(SemanticActionEnum.SA_NUM, scanner.skipFloat())
            }
            else ->
                throw SyntaxError(" not, - , ( , id , str , num  Expected")
        }

    }

    private fun doAction(action: SemanticActionEnum, tokenVal: String = "") {
        var l = ""
        var r = ""
        var temp = ""


        when (action) {

            SemanticActionEnum.SA_ID, SemanticActionEnum.SA_STR, SemanticActionEnum.SA_NUM -> {
                stack.push(tokenVal);
            }

            SemanticActionEnum.SA_ADD, SemanticActionEnum.SA_SUB, SemanticActionEnum.SA_MUL,
            SemanticActionEnum.SA_DIV, SemanticActionEnum.SA_OR, SemanticActionEnum.SA_AND,
            SemanticActionEnum.SA_LESS, SemanticActionEnum.SA_LESS_EQ, SemanticActionEnum.SA_EQUAL,
            SemanticActionEnum.SA_GREAT, SemanticActionEnum.SA_GREAT_EQ, SemanticActionEnum.SA_NOT_EQ,
            SemanticActionEnum.SA_STAR

            -> {

                r = stack.pop()
                l = stack.pop()
                temp = UUID.randomUUID().toString()
                addCode(tokenVal, l, r, temp);
                stack.push(temp);
            }

            SemanticActionEnum.SA_NEG, SemanticActionEnum.SA_NOT -> {
                temp = UUID.randomUUID().toString()
                r = stack.pop()
                addCode(tokenVal, r, "-", temp);
                stack.push(temp);
            }

            else -> {
            } // ignore

        }
        Log.i("doAction", stack.toString())
    }

    private fun addCode(newOp: String, a1: String, a2: String, target: String) {
        codes.add(
            Code(
                operation = newOp,
                leftOperand = a1,
                rightOperand = a2,
                target = target
            )
        )
    }

}