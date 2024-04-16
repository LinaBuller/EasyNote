package com.buller.mysqlite.fragments.add

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.view.View

class CustomDragShadowBuilder( view: View) :  View.DragShadowBuilder(view) {

    override fun onDrawShadow(canvas: Canvas) {
        val path = Path();
        path.addRoundRect(
            RectF(0f, 0f, view.width.toFloat(), view.height.toFloat()),
            60f, 60f, Path.Direction.CW)
        canvas.clipPath(path)
        view.draw(canvas)
    }
}