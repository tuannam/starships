package au.com.starships.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import au.com.starships.R
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class StarsView
@kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val fps: Long = 1000 / 60
    private val defaultStarCount: Int = 25
    private val threadExecutor = Executors.newSingleThreadExecutor()

    private var starCount: Int
    private var starColors: IntArray
    private var bigStarThreshold: Int
    private var minStarSize: Int
    private var maxStarSize: Int

    private var starsCalculatedFlag: Boolean = false
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var stars: List<Star> = ArrayList()
    private var starConstraints: StarConstraints


    private lateinit var timer: Timer
    private lateinit var task: TimerTask

    private val random: Random = Random()
    private var initiated: Boolean = false

    /**
     * init view's attributes
     */
    init {

        val array =
            context.obtainStyledAttributes(attrs, R.styleable.AnimatedStarsView, defStyleAttr, 0)

        starColors = intArrayOf()
        starCount =
            array.getInt(R.styleable.AnimatedStarsView_starsView_starCount, defaultStarCount)
        minStarSize =
            array.getDimensionPixelSize(R.styleable.AnimatedStarsView_starsView_minStarSize, 4)
        maxStarSize =
            array.getDimensionPixelSize(R.styleable.AnimatedStarsView_starsView_maxStarSize, 24)
        bigStarThreshold = array.getDimensionPixelSize(
            R.styleable.AnimatedStarsView_starsView_bigStarThreshold,
            Integer.MAX_VALUE
        )
        starConstraints = StarConstraints(minStarSize, maxStarSize, bigStarThreshold)

        val starColorsArrayId =
            array.getResourceId(R.styleable.AnimatedStarsView_starsView_starColors, 0)

        if (starColorsArrayId != 0) {
            starColors = context.resources.getIntArray(starColorsArrayId)
        }

        array.recycle()

    }


    /**
     * Must call this in Activity's onStart
     */
    fun onStart() {
        timer = Timer()
        task = object : TimerTask() {
            override fun run() {
                invalidateStars()
            }
        }
        timer.scheduleAtFixedRate(task, 0, fps)
    }


    /**
     * Must call this in Activity's onStop
     */
    fun onStop() {

        task.cancel()
        timer.cancel()

    }

    /**
     * get view's size and init stars every time the size of the view has changed
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h

        if (viewWidth > 0 && viewHeight > 0) {
            // init stars every time the size of the view has changed
            initStars()
        }
    }


    /**
     * Draw stars on view's canvas
     */
    override fun onDraw(canvas: Canvas?) {

        // create a variable canvas object
        var newCanvas = canvas

        // draw each star on the canvas
        stars.forEach { newCanvas = it.draw(newCanvas) }

        // reset flag
        starsCalculatedFlag = false

        // finish drawing view
        super.onDraw(newCanvas)

    }


    /**
     * create x stars with a random point location and opacity
     */
    private fun initStars() {
        // map stars instead of adding via loop - courtesy of Dominik Mičuta & Arek Olek
        stars = List(starCount) {
            Star(
                starConstraints,
                Math.round(Math.random() * viewWidth).toInt(),
                Math.round(Math.random() * viewHeight).toInt(),
                Math.random(),
                starColors[it % starColors.size],
                viewWidth,
                viewHeight,
                { starColors[random.nextInt(starColors.size)] }
            )
        }

        initiated = true

    }

    /**
     * calculate and invalidate all stars for the next frame
     */
    private fun invalidateStars() {

        if (!initiated) {
            return
        }

        // new background thread
        threadExecutor.execute({

            // recalculate stars position and alpha on a background thread
            stars.forEach { it.calculateFrame(viewWidth, viewHeight) }
            starsCalculatedFlag = true

            // then post to ui thread
            postInvalidate()

        })

    }
}

/**
 * Single star in sky view
 */
private class Star(
    val starConstraints: StarConstraints,
    var x: Int,
    var y: Int,
    var opacity: Double,
    var color: Int, viewWidth: Int,
    viewHeight: Int,
    val colorListener: () -> Int
) {
    var alpha: Int = 0
    var factor: Int = 1
    var increment: Double
    val length: Double =
        (starConstraints.minStarSize + Math.random() * (starConstraints.maxStarSize - starConstraints.minStarSize))

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var shape: StarShape

    private lateinit var hRect: RectF
    private lateinit var vRect: RectF

    /**
     * init paint, shape and some parameters
     */
    init {

        // init fill paint for small and big stars
        fillPaint.color = color

        // init stroke paint for the circle stars
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = length.toFloat() / 4f

        // init shape of star according to random size
        shape = if (length >= starConstraints.bigStarThreshold) {
            if (Math.random() < 0.7) {
                StarShape.Star
            } else {
                StarShape.Circle
            }
        } else {
            StarShape.Dot
        }

        // the alpha incerment speed will be decided according to the star's size
        increment = when (shape) {
            StarShape.Circle -> {
                Math.random() * .025
            }
            StarShape.Star -> {
                Math.random() * .030
            }
            StarShape.Dot -> {
                Math.random() * .045
            }
        }

        initLocationAndRectangles(viewWidth, viewHeight)
    }

    fun calculateFrame(viewWidth: Int, viewHeight: Int) {
        if (opacity >= 1 || opacity <= 0) {
            factor *= -1
        }
        opacity += increment * factor
        alpha = (opacity * 255.0).toInt()
        when {
            alpha > 255 -> {
                alpha = 255
            }
            alpha <= 0 -> {
                alpha = 0
                initLocationAndRectangles(viewWidth, viewHeight)

                color = colorListener.invoke()
                fillPaint.color = color
                strokePaint.color = color
                strokePaint.style = Paint.Style.STROKE
                strokePaint.strokeWidth = length.toFloat() / 4f
            }
        }
    }

    private fun initLocationAndRectangles(viewWidth: Int, viewHeight: Int) {
        x = Math.round(Math.random() * viewWidth).toInt()
        y = Math.round(Math.random() * viewHeight).toInt()
        if (shape == StarShape.Star) {
            val hLeft = (x - length / 2).toFloat()
            val hRight = (x + length / 2).toFloat()
            val hTop = (y - length / 6).toFloat()
            val hBottom = (y + length / 6).toFloat()
            hRect = RectF(hLeft, hTop, hRight, hBottom)
            val vLeft = (x - length / 6).toFloat()
            val vRight = (x + length / 6).toFloat()
            val vTop = (y - length / 2).toFloat()
            val vBottom = (y + length / 2).toFloat()
            vRect = RectF(vLeft, vTop, vRight, vBottom)
        }
    }

    fun draw(canvas: Canvas?): Canvas? {
        fillPaint.alpha = alpha
        strokePaint.alpha = alpha
        when (shape) {
            StarShape.Dot -> {
                canvas?.drawCircle(x.toFloat(), y.toFloat(), length.toFloat() / 2f, fillPaint)
            }
            StarShape.Star -> {
                canvas?.drawRoundRect(hRect, 6f, 6f, fillPaint)
                canvas?.drawRoundRect(vRect, 6f, 6f, fillPaint)
            }
            StarShape.Circle -> {
                canvas?.drawCircle(x.toFloat(), y.toFloat(), length.toFloat() / 2f, strokePaint)
            }
        }

        return canvas

    }

    private enum class StarShape {
        Circle, Star, Dot
    }
}

private class StarConstraints(val minStarSize: Int, val maxStarSize: Int, val bigStarThreshold: Int)