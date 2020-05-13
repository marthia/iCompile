package com.example.icompile.util

import java.util.regex.Matcher
import java.util.regex.Pattern

object ScannerUtil {

    fun containsSpecialCharacter(s: String?): Boolean {
        return !(s?.matches("[^A-Za-z0-9 ]".toRegex()) ?: false)
    }
}