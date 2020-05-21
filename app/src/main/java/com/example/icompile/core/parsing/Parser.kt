package com.example.icompile.core.parsing

import com.example.icompile.core.SyntaxError

class Parser(private val parse: IParser) {

    fun execute(): String {
        return try {

            parse.execute()

        } catch (e: SyntaxError) {

            e.print()
        }

    }
}