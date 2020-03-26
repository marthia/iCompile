package com.example.icompile.codegen

import com.example.icompile.codegen.Codes.ByteCodes

/**
 * VarOpcode class used for bytecodes with addresses
 * and string information - e.g. LOAD 0 x
 */
class VarOpcode
/**
 * @param code is the bytecode being created
 * @param location is the offset from the start of the current frame
 * @param varname is the name of the variable being loaded/stored
 */(code: ByteCodes?, var location: Int, var varname: String) :
    Code(code!!) {
    override fun toString(): String {
        return super.toString() + " " + location + " " + varname
    }

    override fun print() {
        println(toString())
    }

}