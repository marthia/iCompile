package com.example.icompile.lexer

/**
 * - This file contains the enumberation of all of the tokens
 */
enum class Tokens {
    BogusToken, Program, Int, Boolean, If, Then, Else, While, Void, Function, Return, Identifier,
    Integer, Float, String, LeftBrace, RightBrace, LeftParen, RightParen, Comma, Assign, Equal, NotEqual,
    Less, Greater, GreaterEqual, LessEqual, Plus, Minus, Or, And, Multiply, Divide, Comment, Not, DoubleQuote
}