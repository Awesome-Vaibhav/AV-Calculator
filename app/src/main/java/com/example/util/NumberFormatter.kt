package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object NumberFormatter {

    fun formatNumberStringIndianStyle(str: String): String {
        if (str == "Error" || str == "Infinity" || str == "-Infinity" || str.isEmpty()) {
            return str
        }
        val isNegative = str.startsWith("-")
        val cleanStr = if (isNegative) str.substring(1) else str

        val parts = cleanStr.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "." + parts[1] else ""

        val length = integerPart.length
        if (length <= 3) {
            return (if (isNegative) "-" else "") + integerPart + decimalPart
        }

        val sb = StringBuilder()
        sb.append(integerPart.substring(length - 3))

        var idx = length - 3
        while (idx > 0) {
            val take = if (idx >= 2) 2 else idx
            val chunk = integerPart.substring(idx - take, idx)
            sb.insert(0, chunk + ",")
            idx -= take
        }

        return (if (isNegative) "-" else "") + sb.toString() + decimalPart
    }

    fun formatExpressionWithCommas(expression: String): String {
        val regex = Regex("\\d+(\\.\\d+)?")
        return regex.replace(expression) { matchResult ->
            formatNumberStringIndianStyle(matchResult.value)
        }
    }
}

class IndianGroupingVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val formattedText = NumberFormatter.formatExpressionWithCommas(originalText)

        val origToTransformed = IntArray(originalText.length + 1)
        val transformedToOrig = IntArray(formattedText.length + 1)

        var origIdx = 0
        var transIdx = 0

        while (origIdx < originalText.length && transIdx < formattedText.length) {
            if (originalText[origIdx] == formattedText[transIdx]) {
                origToTransformed[origIdx] = transIdx
                transformedToOrig[transIdx] = origIdx
                origIdx++
                transIdx++
            } else if (formattedText[transIdx] == ',') {
                transformedToOrig[transIdx] = origIdx
                transIdx++
            } else {
                origToTransformed[origIdx] = transIdx
                transformedToOrig[transIdx] = origIdx
                origIdx++
                transIdx++
            }
        }
        origToTransformed[originalText.length] = formattedText.length
        transformedToOrig[formattedText.length] = originalText.length

        while (transIdx <= formattedText.length) {
            transformedToOrig[transIdx] = originalText.length
            transIdx++
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset < 0) return 0
                if (offset > originalText.length) return formattedText.length
                return origToTransformed[offset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset < 0) return 0
                if (offset > formattedText.length) return originalText.length
                return transformedToOrig[offset]
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}
