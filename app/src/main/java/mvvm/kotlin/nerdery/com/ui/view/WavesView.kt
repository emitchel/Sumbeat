package mvvm.kotlin.nerdery.com.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import mvvm.kotlin.nerdery.com.R
import kotlin.random.Random


class WavesView
@JvmOverloads
/**
 * Went for something similar to https://www.youtube.com/watch?v=00LiH7anqkE
 * @param context Context
 * @param attrs AttributeSet?
 * @param defStyleAttr Int
 * @constructor
 */
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr) {

    private val wavePaint: Paint
    private var paths: MutableList<Wave> = mutableListOf()
    private var waveAnimator: ValueAnimator? = null
    private var numOfWaves = 0
    private var bpm = 60
    private var waveWidth = 0
    private var currentX = 0f

    private var animatedWaveOffset = 0f
        set(value) {
            field = value
            if (value <= 0.05) {
                generateRandomFunctionForHeightOfWaves()
            }
            postInvalidateOnAnimation()
            println("New animatedWaveOffset $value")
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        currentX = width * (1f / (numOfWaves + 1))
        paths.forEach {
            it.apply { xPosition = currentX }
                .also { currentX += width * (1f / (numOfWaves + 1)) }
        }
        generateRandomFunctionForHeightOfWaves()
    }

    init {

        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)
        waveWidth = attrs.getDimensionPixelSize(R.styleable.WavesView_waveStrokeWidth, 30)
        numOfWaves = attrs.getInteger(R.styleable.WavesView_numOfWaves, 10)
        bpm = attrs.getInteger(R.styleable.WavesView_bpm, 60)
        for (i in 0 until numOfWaves) {
            paths.add(Wave(Path()))
        }

        //init paint with custom attrs
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrs.getColor(R.styleable.WavesView_waveColor, 0)
            strokeWidth = waveWidth.toFloat()
            style = Paint.Style.STROKE
            pathEffect = CornerPathEffect(5f)
        }
        attrs.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                animatedWaveOffset = it.animatedValue as Float
            }
            //half way up half way down, so 500ms is 60 bpm (half second up, half second down, 1 beat per second)
            //30 000ms is 1bpm, 30s up, 30s down
            duration = (30000 / bpm).toLong()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            reverse()
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    fun endWaves(callback: (() -> Unit)) {
        waveAnimator?.addUpdateListener {
            if (it.animatedValue as Float >= .9f) {
                waveAnimator?.pause()
                callback()
            }
        }
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun generateRandomFunctionForHeightOfWaves() {
        val verticalSpread = (100..120).shuffled().first()
        println("verticalSpread = $verticalSpread")
        val horizontalSpread = listOf(.15, .18, .2, .22, .25).shuffled().first()
        println("horizontalSpread $horizontalSpread")
        val horizontalShift = (0..1000).shuffled().first()
        val height = (2..5).shuffled().first()
        println("height $height")

        paths.forEach { wave ->
            wave.maxHeight = verticalSpread *
                    (Math.sin((wave.xPosition.toDouble() * horizontalSpread) + horizontalShift) + height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paths.forEach {

            val direction = if (it.movingUp) 1 else -1

            if (!it.stopMoving) {
                it.path.reset()
                it.height = it.maxHeight * animatedWaveOffset * direction
                it.path.moveTo(it.xPosition, height.toFloat() - 0)
                it.path.lineTo(it.xPosition, (Math.max((height - it.height), 0.toDouble()).toFloat()))
            }
            it.path.close()
            canvas.drawPath(it.path, wavePaint)
        }
    }

    private fun drawSporadicPaths(canvas: Canvas) {
        paths.forEach {
            it.path.reset()
            if (it.movingUp && it.height >= it.maxHeight) {
                it.movingUp = false
                it.shuffleMaxHeight()
                it.shuffleSpeedModifier()
            } else if (!it.movingUp && it.height <= it.minHeight) {
                it.movingUp = true
                it.shuffleMinHeight()
                it.shuffleSpeedModifier()
            }

            val direction = if (it.movingUp) 1 else -1
            it.height += (10 * direction * it.speedModifier).toInt()
            it.path.moveTo(currentX, height.toFloat() - 30) //30 padding from bottom
            it.path.lineTo(currentX, ((height - it.height)).toFloat())
            it.path.close()
            canvas.drawPath(it.path, wavePaint)
        }
    }

    data class Wave(
        var path: Path,
        var height: Double = 0.0,
        var maxHeight: Double = 0.0,
        var minHeight: Double = 0.0,
        var movingUp: Boolean = true,
        var speedModifier: Double = 0.0
    ) {
        init {
            shuffleMaxHeight()
            shuffleMinHeight()
            shuffleSpeedModifier()
        }

        var xPosition = 0f
        var stopMoving = false

        fun shuffleMaxHeight() {
            maxHeight = Random.nextDouble(100.0, 750.0)
        }

        fun shuffleMinHeight() {
            minHeight = Random.nextDouble(0.0, maxHeight)
        }

        fun shuffleSpeedModifier() {
            speedModifier = listOf(1.0, 1.5, 2.0, 2.5).shuffled().first().toDouble()
        }

    }

}