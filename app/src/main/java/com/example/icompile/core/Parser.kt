package com.example.icompile.core

class Parser(
    val scanner: Scanner
) {

    fun execute() {
        val e = Expression(scanner)
        e.skipComp()
    }

    class Expression(
        private val scanner: Scanner
    ) {

        fun skipComp() {
            skipAdds()
            skipComp1()
        }

        private fun skipComp1(): Boolean {
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

        private fun skipAdds() {
            skipMuls()
            skipAdds1()
        }

        private fun skipAdds1(): Boolean {
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

        private fun skipMuls() {
            skipPrimary()
            skipMuls1()
        }

        private fun skipMuls1(): Boolean {
            val result: Boolean
            when (scanner.getTokenInList(listOf("*", "/", "and"))) {
                0 -> {
                    scanner.getToken("*");
                    skipPrimary()
                    result = true
                    skipMuls1()
                }
                1 -> {
                    scanner.getToken("/");
                    skipPrimary()
                    result = true
                    skipMuls1()
                }
                2 -> {
                    scanner.getToken("and")
                    skipPrimary()
                    result = true
                    skipMuls1()
                }
                else -> result = true// nothing
            }
            return result
        }

        private fun skipPrimary(): Boolean {
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
    }
}