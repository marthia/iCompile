package com.example.icompile.core

class Parser(
    val scanner: Scanner
) {

    fun execute() {
        val e = Expression()
        e.execute(scanner)
    }

    class Expression {

        lateinit var scanner: Scanner

        fun skipComp() {
            skipAdds()
            skipComp1()
        }

        fun skipComp1(): Boolean {
            return when (scanner.getTokenInList(listOf("<=", ">=", "<>", "<", "=", ">"))) {
                0 -> {
                    scanner.getToken("<=")
                    skipAdds()
                    true
                }
                1 -> {
                    scanner.getToken(">=")
                    skipAdds()
                    true
                }
                2 -> {
                    scanner.getToken("<>")
                    skipAdds()
                    true
                }
                3 -> {
                    scanner.getToken("<")
                    skipAdds()
                    true
                }
                4 -> {
                    scanner.getToken("=")
                    skipAdds()
                    true
                }
                5 -> {
                    scanner.getToken(">")
                    skipAdds()
                    true

                }
                else -> false
            }
        }


        fun skipAdds() {
            skipMuls()
            skipAdds1()
        }


        fun skipAdds1(): Boolean {
            return when (scanner.getTokenInList(listOf("+", "-", "or"))) {
                0 -> {
                    scanner.getToken("+");
                    skipMuls()
                    true
                    skipAdds1()
                }
                1 -> {
                    scanner.getToken("-");
                    skipMuls()
                    true
                    skipAdds1()
                }
                2 -> {
                    scanner.getToken("or")
                    skipMuls()
                    true
                    skipAdds1()
                }
                else -> false// nothing
            }
        }

        fun skipMuls() {
            skipPrimary()
            skipMuls1()
        }

        fun skipMuls1(): Boolean {
            return when (scanner.getTokenInList(listOf("*", "/", "and"))) {
                0 -> {
                    scanner.getToken("*");
                    skipPrimary()
                    true
                    skipMuls1()
                }
                1 -> {
                    scanner.getToken("/");
                    skipPrimary()
                    true
                    skipMuls1()
                }
                2 -> {
                    scanner.getToken("and")
                    skipPrimary()
                    true
                    skipMuls1()
                }
                else -> false// nothing
            }
        }

        fun skipPrimary(): Boolean {
            return when (scanner.getTokenInList(listOf("not", "-", "(", "#id", "#str", "#float"))) {
                0 -> {
                    scanner.getToken("not");
                    skipPrimary()
                    true
                }
                1 -> {
                    scanner.getToken("-");
                    skipPrimary()
                    true
                }
                2 -> {
                    scanner.getToken("(");
                    skipComp()
                    scanner.getToken(")");
                    false
                }
                3, 4, 5 -> {
                    true
                }

                else -> {
                    scanner.abortSyntax("\" not, - , ( , id , str , float \" Expected")
                    false
                }
            }
        }


        fun execute(scanner: Scanner) {
            this.scanner = scanner
            skipComp()
        }
    }
}