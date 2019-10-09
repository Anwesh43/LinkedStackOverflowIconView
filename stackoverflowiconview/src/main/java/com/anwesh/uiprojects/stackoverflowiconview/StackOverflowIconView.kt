package com.anwesh.uiprojects.stackoverflowiconview

/**
 * Created by anweshmishra on 09/10/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 5
val scGap : Float = 0.01f
val lines : Int = 5
val deg : Float = 90f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val lineColor : Int = Color.parseColor("#FF5722")
val boxColor : Int = Color.parseColor("#212121")
val backColor : Int = Color.parseColor("#BDBDBD")
val offsetFactor : Float = 5f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawLineEntry(i : Int, scale : Float, size : Float, paint : Paint) {
    val gapDeg : Float = deg / lines
    val currDeg : Float = 180f * i * gapDeg
    val xStart : Float = size / offsetFactor
    val xEnd : Float = 2 * size - xStart
    save()
    translate(size, 0f)
    rotate(currDeg)
    drawLine(xStart, 0f, xStart + (xEnd - xStart) * scale.divideScale(i, lines), 0f, paint)
    restore()
}

fun Canvas.drawLines(scale : Float, size : Float, paint : Paint) {
    paint.color = lineColor
    for (j in 0..(lines - 1)) {
        drawLineEntry(j, scale, size, paint)
    }
}

fun Canvas.drawBox(size : Float, paint : Paint) {
    paint.color = boxColor
    for (j in 0..1) {
        save()
        translate(-size + j * size, 0f)
        drawLine(0f, -size / 2, 0f, size / 2, paint)
        restore()
    }
    save()
    translate(-size, size / 2)
    drawLine(0f, 0f, 2 * size, 0f, paint)
    restore()
}

fun Canvas.drawStackOverflowIcon(scale : Float, size : Float, paint : Paint) {
    drawBox(size, paint)
    drawLines(scale, size, paint)
}

fun Canvas.drawSOINode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(w / 2, gap * (i + 1))
    drawStackOverflowIcon(scale, size, paint)
    restore()
}

class StackOverflowIconView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
