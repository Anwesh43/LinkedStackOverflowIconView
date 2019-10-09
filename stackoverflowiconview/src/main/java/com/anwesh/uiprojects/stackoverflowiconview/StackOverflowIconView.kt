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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

