package com.example.icompile.codegen

import com.example.icompile.codegen.Codes.ByteCodes

/** The Code class records bytecode information
 */
open class Code(val bytecode: ByteCodes?) {

    override fun toString(): String {
        return bytecode.toString()
    }

    open fun print() {
        println(toString())
    }

}