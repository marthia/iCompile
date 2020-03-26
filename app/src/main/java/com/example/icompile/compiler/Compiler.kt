package com.example.icompile.compiler

import com.example.icompile.codegen.Codegen
import com.example.icompile.constrain.Constrainer
import com.example.icompile.parser.Parser
import com.example.icompile.parser.ParsingResult
import java.io.File
import java.util.*
import kotlin.properties.Delegates

/**
 * The Compiler class contains the main program for compiling
 * a source program to bytecodes
 */
class Compiler(
    /**
     * The Compiler class reads and compiles a source program
     */
    var sourceFile: String,
    var FULL_PATH_BYTECODE: String
) {
    private val sourceLines = Vector<String>()


    fun compileProgram(): String {


        val parser = Parser(sourceFile)
        var parsingResult: ParsingResult? = null
        return  try {
            parsingResult = parser.execute()
            val con = Constrainer(parsingResult.tree!!, parser)
            con.execute()
            val generator = Codegen(parsingResult.tree!!)
            val program = generator.execute()
            program.printCodes(FULL_PATH_BYTECODE)

        } catch (e: java.lang.Exception) {
            val split = parsingResult?.syntaxErrorLineNumber?.split(":")
             "Error occurred at line ' ${split?.get(0)} ' \n cannot resolve " +
                    "token ' ${split?.get(1)} '"
        }
    }

    /*
     * getSourceLines opens the source file, saves each line as a String,
     * populates these into a Vector, and returns that Vector.
     * @return: Vector of Strings
     */
    fun getSourceLines(): Vector<String> {
        try {
            val sep = System.getProperty("file.separator")
            var line: String
//            val file = File("src" + sep + "interpreter" + sep + sourceFile)
            val file = File(sourceFile)
            val source = Scanner(file)
            var index = 1
            while (source.hasNextLine()) {
                line = index++.toString() + ". " + source.nextLine()
                sourceLines.add(line)
            }
            source.close()
        } catch (e: Exception) {
            println(e)
        }
        return sourceLines
    }

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            if (args.isEmpty()) {
//                // System.out.println("***Incorrect usage, try: java compiler.Compiler <file>");
//                // System.exit(1);
//            }
//            // (new Compiler(args[0])).compileProgram();
//        }
//    }

}