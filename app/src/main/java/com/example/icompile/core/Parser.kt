package com.example.icompile.core

class Parser(private val parse: IParser) {

    fun execute(): String {
        return try {

            parse.execute()

        } catch (e: SyntaxError) {

            e.print()
        }

    }
}