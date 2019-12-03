package com.example.icompile.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.icompile.R


class EditorView : AppCompatEditText {

    // using `@JvmOverloads` generates unwanted bugs and styling issues
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        init(context, attrs, defStyleAttr)
    }

    private val rect = Rect()
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.GRAY
        textSize = resources.getDimensionPixelSize(R.dimen.editor_line_number_text_size).toFloat()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Load the styled attributes and set their properties
        val attributes =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.EditorView, defStyleAttr, 0
            )
        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var baseline = baseline
        for (i in 0 until lineCount) {
            paint.let {
                canvas.drawText(
                    String.format(" %02d ", i + 1), rect.left.toFloat(),
                    baseline.toFloat(), it
                )
            }
            baseline += lineHeight
        }
    }

}