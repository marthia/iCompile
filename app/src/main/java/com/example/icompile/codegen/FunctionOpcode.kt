package com.example.icompile.codegen

import com.example.icompile.codegen.Codes.ByteCodes

/**
 * FunctionOpcode class records bytecodes that are functions, taking in start and end
 * positions in the sourceCode:
 * e.g. LABEL xyz
 */
class FunctionOpcode
/**
 * @param code is the bytecode being created
 * @param label is the string representation of the label of interest
 */(code: ByteCodes?, var label: String, var start: Int, var end: Int) :
    Code(code) {
    override fun print() {
        println(toString())
    }

    override fun toString(): String {
        return super.toString() + " " + label + " " + start + " " + end
    }

}