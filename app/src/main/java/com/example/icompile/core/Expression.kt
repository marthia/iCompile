package com.example.icompile.core


class Expression : CoreTextParser() {


    fun skipComp() {
        skipAdds()
        skipComp1()
    }

    fun skipComp1() {
        when (getKeywordAt(listOf("<=", ">=", "<>", "<", "=", ">"))) {
            0 -> {
                skipKeyword("<=")
                skipAdds()
                //  Result : = Result+['<='];
            }
            1 -> {
                skipKeyword(">=")
                skipAdds()
//        Result : = Result+['>=']
            }
            2 -> {
                skipKeyword("<>")
                skipAdds()
//                Result : = Result+['<>'];
            }
            3 -> {
                skipKeyword("<")
                skipAdds()
//        Result : = Result+['<'];
            }
            4 -> {
                skipKeyword("=")
                skipAdds()
//            Result : = Result+['='];
            }
            5 -> {
                skipKeyword(">")
                skipAdds()
//                Result : = Result+[">"];

            }
            else -> false
        }
    }


    fun skipAdds() {
        skipMuls()
        skipAdds1()
    }


    fun skipAdds1() {
        when (getKeywordAt(listOf("+", "-", "or"))) {
            0 -> {
                skipKeyword("+");
                skipMuls()
//                Result : = Result+['+'];
                skipAdds1()
            }
            1 -> {
                skipKeyword("-");
                skipMuls()
//                Result : = Result+["-"];
                skipAdds1()
            }
            2 -> {
                skipKeyword("or")
                skipMuls()
//                Result : = Result+['or'];
                skipAdds1()
            }
            else -> false// nothing
        }
    }

    fun skipMuls() {
        skipPrimary()
        skipMuls1()
    }

    fun skipMuls1() {
        when (getKeywordAt(listOf("*", "/", "and"))) {
            0 -> {
                skipKeyword("*");
                skipPrimary()
//                Result : = Result+['*'];
                skipMuls1()
            }
            1 -> {
                skipKeyword("/");
                skipPrimary()
//            Result : = Result+['/'];
                skipMuls1()
            }
            2 -> {
                skipKeyword("and")
                skipPrimary()
//            Result : = Result+['and']
                skipMuls1()
            }
            else -> false// nothing
        }
    }

    fun skipPrimary() {
        when (getKeywordAt(listOf("not", "-", "(", "#id", "#str", "#float"))) {
            0 -> {
                skipKeyword("not");
                skipPrimary()
//            Result : = Result+['not'];
            }
            1 -> {
                skipKeyword("-");
                skipPrimary()
//            Result : = Result+['-'];
            }
            2 -> {
                skipKeyword("(");
                skipComp()
                skipKeyword(")");
            }
            3 ->
//            Result : = Result+[SkipId];

                4
            ->
//            Result : = Result+[SkipPStr];
                5
            ->
//            Result : = Result+[SkipFloat.ToString];
            else ->

                "\" not, - , ( , id , str , float \" Expected".abortSyntax()
        }
    }


    fun execute() {
        skipComp()
    }
}