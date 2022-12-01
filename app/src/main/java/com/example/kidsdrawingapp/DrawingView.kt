package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet
import java.nio.file.Path
import java.util.jar.Attributes

class DrawingView(context:Context,attrs:AttributeSet):View(context,attrs) {

    private var mDrawPath:CustomPath?=null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint: Paint?=null
    private var mBrushSize:Float=0.toFloat()
    private var color=Color.BLACK
    private var canvas:Canvas?=null
    private var mPaths=ArrayList<CustomPath>()
    private var mUndoPaths=ArrayList<CustomPath>()


    fun onClickUndo()
    {
        if(mPaths.size>0)
        {
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }


    init{
        setUpDrawing()
    }
    private fun setUpDrawing() {
        mDrawPaint = Paint()//object of type paint
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {//this is a method of view class which is used when the size of the screeen is changed
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888 )
        canvas= Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for(path in mPaths)
        {
            mDrawPaint!!.strokeWidth=path!!.brushThickness
            mDrawPaint!!.color=path!!.color
            canvas.drawPath(path!!,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color=mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX=event!!.x
        val touchY=event!!.y

        when(event?.action)
        {MotionEvent.ACTION_DOWN->{
            mDrawPath!!.color=color
            mDrawPath?.brushThickness=mBrushSize

            mDrawPath!!.reset()
            mDrawPath?.moveTo(touchX!!,touchY!!)
        }

            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchX,touchY)
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawPath!!)
                mDrawPath=CustomPath(color,mBrushSize)
            }
            else->return false

        }
            invalidate()


        return true
    }
    fun setSizeForBrush(newsize:Float)
    {mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newsize,resources.displayMetrics)
        //this is to manage this on diiferne tscreensizes like on a igger screen tis will look differnet
        mDrawPaint?.strokeWidth=newsize
    }

    fun setColor(newColor: String)
    {   color=Color.parseColor(newColor)
        mDrawPaint!!.color=color


    }


    internal inner class CustomPath (var color: Int , var brushThickness: Float) : android.graphics.Path(){

    }


}