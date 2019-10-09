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
val delay : Long = 20

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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SOINode(var i : Int, val state : State = State()) {

        private var next : SOINode? = null
        private var prev : SOINode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SOINode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas?.drawSOINode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SOINode {
            var curr : SOINode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StackOverflowIcon(var i : Int, val state : State = State()) {

        private val root : SOINode = SOINode(0)
        private var curr : SOINode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StackOverflowIconView) {

        private val animator : Animator = Animator(view)
        private var soi : StackOverflowIcon = StackOverflowIcon(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            soi.draw(canvas, paint)
            animator.animate {
                soi.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            soi.startUpdating {
                animator.start()
            }
        }
    }

}
