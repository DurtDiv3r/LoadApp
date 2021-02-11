package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var angle = 0F

    private var valueAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()
    private var progress = 0F

    private var buttonColour = 0
    var button: LoadingButton
    private var buttonText = ""


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                Log.d("LOADINGBUTTON", "CLICKED")
                loadingStateAnimation()
            }
            ButtonState.Loading -> Log.d("LOADINGBUTTON", "LOADING")
            ButtonState.Completed -> Log.d("LOADINGBUTTON", "COMPLETED")
        }
    }


    init {
        isClickable = true
        button = findViewById(R.id.custom_button)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColour = getColor(R.styleable.LoadingButton_buttonColour, 0)
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBaseButton(canvas, buttonState)

        if (ButtonState.Clicked.equals(buttonState)) {
            drawLoadingButton(canvas)
//            drawCircle(canvas)
        }
        updateText(canvas, buttonState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawBaseButton(canvas: Canvas?, buttonState: ButtonState) {
        paint.color = buttonColour
        canvas?.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = Color.WHITE
        buttonText = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.button_loading)
            else -> context.getString(R.string.button_name)
        }
        canvas?.drawText(buttonText, widthSize / 2F, (heightSize / 2F) - ((paint.descent() + paint.ascent()) / 2F), paint)
        canvas?.save()
    }

    private fun updateText(canvas: Canvas?, buttonState: ButtonState) {
        paint.color = ResourcesCompat.getColor(resources, R.color.white, null)
        buttonText = when (buttonState) {
            ButtonState.Clicked -> context.getString(R.string.button_loading)
            else -> context.getString(R.string.button_name)
        }
        canvas?.drawText(buttonText, widthSize / 2F, (heightSize / 2F) - ((paint.descent() + paint.ascent()) / 2F), paint)
    }

//    private fun drawCircle(canvas: Canvas?) {
//        paint.color= Color.YELLOW
//        canvas?.drawArc((widthSize.toFloat() - 150f),(heightSize.toFloat() / 2) - 50f, (widthSize.toFloat()-50f),
//            (heightSize.toFloat() / 2) + 50f, 0F,angle, true,paint)
//    }

//    private fun animatedCircle() {
//        circleAnimator = ValueAnimator.ofFloat(0F, 360F).apply {
//            duration = 1000
//            addUpdateListener { valueAnimator ->
//                angle = valueAnimator.animatedValue as Float
//                valueAnimator.repeatCount = ValueAnimator.INFINITE
//                valueAnimator.repeatMode = ValueAnimator.REVERSE
//                button.invalidate()
//            }
////            disableViewDuringAnimation(loadingButton)
//            start()
//        }
//    }

    private fun loadingStateAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0F, widthSize.toFloat()).apply {
            duration = 1000
            addUpdateListener { valueAnimator ->
                progress = valueAnimator.animatedValue as Float
                valueAnimator.interpolator = LinearInterpolator()
                button.invalidate()
            }
//            disableViewDuringAnimation(loadingButton)
            start()
        }

    }

    private fun drawLoadingButton(canvas: Canvas?) {
        paint.color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
        canvas?.drawRect(
            0f,
            0f,
            progress,
            heightSize.toFloat(), paint)
//        buttonState = ButtonState.Completed
    }
}