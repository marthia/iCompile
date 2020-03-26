package com.example.icompile.codegen

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.util.*

/**
 * This class will hold the generated program bytecodes
 */
class Program {
    private val program =
        ArrayList<Code>()

    /**
     * store the new bytecode in the program vector
     * @param code is the bytecode to store
     */
    fun storeop(code: Code) {
        program.add(code)
    }

    /**
     * print all of the bytecodes that have been generated
     * @param outFile a String indicating where to print the bytecodes
     */
    fun printCodes(outFile : String): String {
        var out: PrintWriter? = null
        try {
            val byteCodeFile = File(outFile)
            out = PrintWriter(FileOutputStream(byteCodeFile))
        } catch (e: IOException) {
            println(e.toString())
        }

        // generating the code both for showcasing in app and also preserving in file
        val stringBuilder = StringBuilder()
        for (nextCode in program) {
            println(nextCode.toString())
            out!!.println(nextCode.toString())
            stringBuilder.appendln(nextCode.toString())
        }
        out!!.close()

        return stringBuilder.toString()
    }
}