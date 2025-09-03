package com.view.captchaedittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.TextView


@SuppressLint("AppCompatCustomView")
public class CaptchaEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextView(context, attrs) {
    var captchaLength = 6;
    var intervalPadding = 20;
    var radius = 0f;
    var borderSize = 0f;
    var borderType = BorderType.rectangle
    var borderStrokeWidth = 0f;
    var norBorderColor = 0;
    var focusBorderColor = 0;
    var selectionIndex = 0;
    var onCallback: ((code: String)-> Unit)? = null
    var callbackAuto = false
    val textArray by lazy {
        CharArray(captchaLength)
    }
    val defaultPadding by lazy {
        borderStrokeWidth / 2f;
    }

    val mInputConnection by lazy {
        object : InputConnectionWrapper(BaseInputConnection(this@CaptchaEditText, false), false) {
            override fun performEditorAction(editorAction: Int): Boolean {
                if (editorAction == EditorInfo.IME_ACTION_DONE) {
                    onCallback?.let { it(text) }
                    (getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
                }
                return super.performEditorAction(editorAction)
            }
        }
    }

    val borderPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = borderStrokeWidth
            style = Paint.Style.STROKE
        }
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        borderSize = dpToPx(1)
        setFocusable(true)
        isFocusableInTouchMode = true
        context.obtainStyledAttributes(attrs, R.styleable.CaptchaEditText).apply {
            borderSize = getDimensionPixelSize(R.styleable.CaptchaEditText_borderSize, dpToPx(40).toInt()).toFloat();
            borderStrokeWidth = getDimensionPixelSize(R.styleable.CaptchaEditText_borderStrokeWidth, dpToPx(1).toInt()).toFloat();
            captchaLength = getInt(R.styleable.CaptchaEditText_captchaLength, 4);
            intervalPadding = getDimensionPixelSize(R.styleable.CaptchaEditText_intervalPadding, dpToPx(5).toInt());
            radius = getDimensionPixelSize(R.styleable.CaptchaEditText_radius, dpToPx(4).toInt()).toFloat();
            norBorderColor = getColor(R.styleable.CaptchaEditText_norBorderColor, Color.parseColor("#444444"));
            focusBorderColor = getColor(R.styleable.CaptchaEditText_focusBorderColor, Color.parseColor("#3366FF"));
            borderType = getInt(R.styleable.CaptchaEditText_borderType, BorderType.rectangle)
            callbackAuto = getBoolean(R.styleable.CaptchaEditText_callbackAuto, false)
        }.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var adaptWithSize = intervalPadding * (captchaLength - 1) + captchaLength * borderSize
        super.setMeasuredDimension((adaptWithSize + 2 * defaultPadding).toInt(), (borderSize + 2 * defaultPadding).toInt())
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(defaultPadding, defaultPadding)
        for (i in 0 until captchaLength) {
            var startX = i * (borderSize + intervalPadding).toFloat()
            if (isFocused &&
                    (selectionIndex == i ||
                        ((i == 0 && selectionIndex == -1) ||
                            (i == captchaLength - 1 && selectionIndex == captchaLength)
                        )
                    )
                )
            {
                borderPaint.color = focusBorderColor
                drawBackground(canvas, startX)
            } else {
                borderPaint.color = norBorderColor
                drawBackground(canvas, startX)
            }

            val text = textArray[i].toString()
            if (!TextUtils.isEmpty(text)) {
                val textCenterY = (paint.fontMetrics.bottom - paint.fontMetrics.top) / 2 - paint.fontMetrics.bottom + borderSize / 2
                val textCenterX = startX + (borderSize - paint.measureText(text)) / 2;
                canvas.drawText(text, textCenterX,  textCenterY, paint)
            }
        }

        canvas.restore()
    }

    fun drawBackground(canvas: Canvas, startX: Float) {
        if (borderType == BorderType.rectangle) {
            canvas.drawRoundRect(startX, 0f, startX + borderSize, borderSize.toFloat(), radius, radius, borderPaint)
        } else {
            canvas.drawLine(startX, height - defaultPadding, startX + borderSize, height - defaultPadding, borderPaint)
        }
    }

    fun dpToPx (dp: Int)  = context.resources.displayMetrics.density * dp

    override fun onTouchEvent(event: MotionEvent): Boolean {
       mGestureDetector.onTouchEvent(event);
        return true;
    }

    val mGestureDetector = GestureDetector(context, object: SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent ): Boolean {
            val resetIndex = (e.getX() / (borderSize + intervalPadding)).toInt()
            if (isFocused) {
                selectionIndex = resetIndex;
            }
            requestFocus()
            postInvalidate()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this@CaptchaEditText, InputMethodManager.SHOW_IMPLICIT)
            return super.onSingleTapUp(e)
        }
    })

    override fun onCheckIsTextEditor() = true

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        return mInputConnection
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action === KeyEvent.ACTION_DOWN) {
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && selectionIndex < captchaLength) {
                val digit = ('0' + (keyCode - KeyEvent.KEYCODE_0))
                if (selectionIndex < 0) selectionIndex = 0
                textArray[selectionIndex] = digit
                selectionIndex++
                postInvalidate()
                if (!callbackAuto && selectionIndex == captchaLength
                    && text.length == captchaLength) {
                    onCallback?.let { it(text) }
                }
                return true
            } else if (keyCode === KeyEvent.KEYCODE_DEL && selectionIndex >= 0) {
                if (selectionIndex >= 6) selectionIndex = 5
                textArray[selectionIndex] = '\u0000';
                selectionIndex--
                postInvalidate()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getText(): String{
        return textArray.filter { it != '\u0000' }.joinToString("")
    }

    public fun setCallback(callback: (code: String)->Unit) {
        this.onCallback = callback;
    }

    object BorderType {
        const val rectangle = 0
        const val underline = 1
    }
}