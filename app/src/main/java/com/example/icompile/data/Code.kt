package com.example.icompile.data

data class Code(

    private val leftOperand: String = "",

    private val rightOperand: String = "",

    private val operation: String = "",

    private val target: String = ""

) {
    override fun toString(): String {
        return "\nOperation : $operation \n" +
                "Left Operand : $leftOperand \n" +
                "Right Operand : $rightOperand \n" +
                "Target : $target"
    }
}