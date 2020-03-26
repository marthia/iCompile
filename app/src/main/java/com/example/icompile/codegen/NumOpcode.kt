package com.example.icompile.codegen

/** NumOpcode class used for bytecodes with a number op field
 * e.g. lit 5
 */
class NumOpcode(code: Codes.ByteCodes, var num: Int) :
    Code(code) {

    override fun toString(): String {
        return super.toString() + " " + num
    }

    override fun print() {
        println(toString())
    }

}