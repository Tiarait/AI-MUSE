package ua.tiar.aim.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class PlaceholderTransformation(private val placeholder: String, private val color: Color) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return placeholderFilter(placeholder)
    }

    private fun placeholderFilter(placeholder: String): TransformedText {
        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return 0
            }
        }

        val builder = AnnotatedString.Builder()
        builder.withStyle(style = SpanStyle(color = color)) {
            append(placeholder)
        }

        return TransformedText(builder.toAnnotatedString(), numberOffsetTranslator)
    }
}