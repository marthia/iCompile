package com.example.icompile.codegen


class StringOpCode(code: Codes.ByteCodes, var string: String) :
    Code(code) {

    override fun toString(): String {
        return super.toString() + " " + string
    }

    override fun print() {
        println(toString())
    }

}