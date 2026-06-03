package com.example.util

import kotlin.math.*

object MathEvaluator {
    fun evaluate(expression: String, isDegreeMode: Boolean = false): Double {
        if (expression.isBlank()) return 0.0
        
        // Preprocess expression and replace visual indicators
        var expr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .trim()
            
        return Parser(expr, isDegreeMode).parse()
    }

    private class Parser(val str: String, val isDegreeMode: Boolean) {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected character: " + ch.toChar() + " at " + pos)
            return x
        }

        // expression = term | expression `+` term | expression `-` term
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) {
                    val base = x
                    x += parseTerm(baseValue = base)
                } else if (eat('-'.code)) {
                    val base = x
                    x -= parseTerm(baseValue = base)
                } else return x
            }
        }

        // term = factor | term `*` factor | term `/` factor
        fun parseTerm(baseValue: Double? = null): Double {
            var x = parseFactor(baseValue = baseValue)
            while (true) {
                if (eat('*'.code)) x *= parseFactor(baseValue = null) // multiplication
                else if (eat('/'.code)) {
                    val divisor = parseFactor(baseValue = null)
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor // division
                } else return x
            }
        }

        // factor = `+` factor | `-` factor | `(` expression `)` | number | functionName factor | factor `^` factor
        fun parseFactor(baseValue: Double? = null): Double {
            if (eat('+'.code)) return +parseFactor(baseValue = null) // unary plus
            if (eat('-'.code)) return -parseFactor(baseValue = null) // unary minus

            var x: Double
            val startPos = this.pos
            if (eat('('.code)) { // parentheses
                x = parseExpression()
                eat(')'.code)
            } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) { // numbers
                while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                val numString = str.substring(startPos, this.pos)
                x = numString.toDoubleOrNull() ?: 0.0
            } else if ((ch >= 'a'.code && ch <= 'z'.code) || ch == '√'.code || ch == 'π'.code) { // functions/constants
                while ((ch >= 'a'.code && ch <= 'z'.code) || ch == '√'.code) nextChar()
                val func = str.substring(startPos, this.pos)
                if (func == "√" || func == "sqrt") {
                    x = parseFactor(baseValue = null)
                    x = sqrt(x)
                } else if (func == "cbrt") {
                    x = parseFactor(baseValue = null)
                    x = x.pow(1.0 / 3.0)
                } else if (func.isEmpty()) {
                    // fallback
                    x = 0.0
                } else {
                    x = parseFactor(baseValue = null)
                    x = when (func) {
                        "sin" -> {
                            val angle = if (isDegreeMode) Math.toRadians(x) else x
                            sin(angle)
                        }
                        "cos" -> {
                            val angle = if (isDegreeMode) Math.toRadians(x) else x
                            cos(angle)
                        }
                        "tan" -> {
                            val angle = if (isDegreeMode) Math.toRadians(x) else x
                            tan(angle)
                        }
                        "log" -> log10(x)
                        "ln" -> ln(x)
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                }
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar() + " at " + pos)
            }

            if (eat('^'.code)) x = x.pow(parseFactor(baseValue = null)) // exponentiation

            // Handle postfix characters
            while (true) {
                if (eat('!'.code)) {
                    x = factorial(x.roundToInt())
                } else if (eat('%'.code)) {
                    if (baseValue != null) {
                        x = baseValue * x * 0.01
                    } else {
                        x *= 0.01
                    }
                } else {
                    break
                }
            }

            return x
        }

        private fun factorial(n: Int): Double {
            if (n < 0) return Double.NaN
            if (n == 0 || n == 1) return 1.0
            var res = 1.0
            for (i in 2..n) {
                res *= i
            }
            return res
        }
    }
}
