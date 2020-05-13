package com.example.icompile.core

import com.example.icompile.core.Scanner.abortSyntax
import com.example.icompile.core.Scanner.getToken
import com.example.icompile.core.Scanner.getTokenInList

class SkipExpression  {

    fun execute() {
        skipComp()
    }

    private fun skipComp() {
        skipAdds()
        skipComp1()
    }

    private fun skipComp1() {
        when (getTokenInList(arrayListOf("<=", ">=", "<>", "<", "=", ">"))) {

            0 -> {
                getToken("<=")
                skipAdds()
//            Result := Result + ['<='];
            }

            1 -> {
                getToken(">=")
                skipAdds()
//            Result := Result + ['<='];
            }

            2 -> {
                getToken("<>")
                skipAdds()
//            Result := Result + ['<='];
            }

            3 -> {
                getToken("<")
                skipAdds()
//            Result := Result + ['<='];
            }

            4 -> {
                getToken("=")
                skipAdds()
//            Result := Result + ['<='];
            }

            5 -> {
                getToken(">")
                skipAdds()
//            Result := Result + ['<='];
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
        when (getTokenInList(arrayListOf("+", "-", "or"))) {

            0 -> {
                getToken("+")
                skipMuls()
//              Result := Result + ['+'];
                skipAdds1()
            }

            1 -> {
                getToken("-")
                skipMuls()
//              Result := Result + ['+'];
                skipAdds1()
            }

            2 -> {
                getToken("or")
                skipMuls()
//              Result := Result + ['+'];
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
        when (getTokenInList(arrayListOf("*", "/", "and"))) {

            0 -> {
                getToken("*")
                skipPrimary()
//                Result := Result + ['*'];
                skipMuls1()
            }
            1 -> {
                getToken("/")
                skipPrimary()
//                Result := Result + ['*'];
                skipMuls1()
            }
            2 -> {
                getToken("and")
                skipPrimary()
//                Result := Result + ['*'];
                skipMuls1()
            }
            else -> {
            } // nothing
        }
    }

    private fun skipPrimary() {
        when (getTokenInList(arrayListOf("not", "-", "(", "#id", "#str", "#float"))) {
            0 -> {
                getToken("not")
                skipPrimary()
//                Result := Result + ['not'];
            }

            1 -> {
                getToken("-")
                skipPrimary()
//                Result := Result + ['not'];
            }
            2 -> {
                getToken("(")
                skipComp()
                getToken(")")
            }
            3 -> {
//                Result := Result + [SkipId];
            }
            4 -> {
//                Result := Result + [SkipPStr];
            }
            5 -> {
//                Result := Result + [SkipFloat.ToString];
            }
            else ->
                abortSyntax(" not, - , ( , id , str , float  Expected")
        }

    }

}