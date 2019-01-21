package mvvm.kotlin.nerdery.com.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout


object ViewAnimationUtil {
    const val DEFAULT_COLLAPSED_HEIGHT = 5 //true collapsed isn't always 0
    fun animateHeight(
        view: View, from: Int, to: Int,
        duration: Int
    ) {

        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.getLayoutParams()
            layoutParams.height = animatedValue
            view.setLayoutParams(layoutParams)
        }
        anim.duration = duration.toLong()
        anim.start()
    }

    /**
     * Easy way to expand a given view after measuring with
     * v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
     *
     * @param v
     * @param duration
     */
    fun expand(
        v: View, duration: Int,
        bStartFromZeroHeight: Boolean
    ) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.getMeasuredHeight()

        if (bStartFromZeroHeight)
            v.getLayoutParams().height = 0

        v.setVisibility(View.VISIBLE)
        val a = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                v.getLayoutParams().height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = duration.toLong()
        v.startAnimation(a)
    }

    /**
     * Easy way to just collapse any given view and any given speed
     *
     * @param v
     * @param duration
     */
    fun collapse(v: View, duration: Int) {
        val initialHeight = v.getMeasuredHeight()

        val a = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                if (interpolatedTime == 1f) {
                    v.setVisibility(View.GONE)
                } else {
                    v.getLayoutParams().height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = duration.toLong()
        v.startAnimation(a)
    }

    /**
     *
     * @param v
     * : view you want to fade in/out
     * @param bFadeIn
     * : true if you're fading in , false if you're fading out
     * @param animationListener
     * : listener for the animation, generally you care about the
     * onAnimationEnd(Animator animation) method
     */
    fun fadeView(
        v: View, bFadeIn: Boolean,
        animationListener: AnimatorListenerAdapter, duration: Int
    ) {

        v.setVisibility(View.VISIBLE)

        if (bFadeIn)
            v.setAlpha(0f)
        else
            v.setAlpha(1f)

        v.animate().alpha(if (bFadeIn) 1f else 0f).setDuration(duration.toLong())
            .setListener(animationListener)
    }

    @JvmOverloads
    fun expandIfCollapsed(v: View, duration: Int, targetHeight: Int, collapsedHeight: Int = DEFAULT_COLLAPSED_HEIGHT) {
        if (v.getHeight() <= collapsedHeight) {
            expand(v, duration, targetHeight)
        }
    }

    fun expand(v: View, duration: Int, targetHeight: Int) {
        val prevHeight = v.getHeight()

        v.setVisibility(View.VISIBLE)
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.addUpdateListener { animation ->
            v.getLayoutParams().height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }

    fun collapseIfExpanded(v: View, duration: Int, targetHeight: Int) {
        if (v.height != 0) {
            collapse(v, duration, targetHeight)
        }
    }

    fun collapse(v: View, duration: Int, targetHeight: Int) {
        val prevHeight = v.getHeight()
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                v.setVisibility(View.GONE)
            }
        })
        valueAnimator.addUpdateListener { animation ->
            v.getLayoutParams().height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }

    fun expandIfCollapsed(
        v: View, duration: Int, measureWidthParams: Int, measureHeightParams: Int,
        shouldStartFromZeroHeight: Boolean
    ) {
        if (v.height == 0) {
            expand(v, duration, measureWidthParams, measureHeightParams, shouldStartFromZeroHeight)
        }
    }

    /**
     * @param v                         View to Expand
     * @param duration                  Duration of animation
     * @param measureWidthParams        Pass in the layout params in which you want to measure e.g. LinearLayout.LayoutParams.MATCH_PARENT
     * @param measureHeightParams       Pass in the layout params in which you want to measure e.g. LinearLayout.LayoutParams.WRAP_CONTENT
     * @param shouldStartFromZeroHeight If you want the view to initially be 0 height
     */
    fun expand(
        v: View, duration: Int, measureWidthParams: Int, measureHeightParams: Int,
        shouldStartFromZeroHeight: Boolean
    ) {


        v.measure(measureWidthParams, measureHeightParams)
        val targetHeight = v.getMeasuredHeight()

        if (shouldStartFromZeroHeight)
            v.getLayoutParams().height = 0

        v.setVisibility(View.VISIBLE)
        val a = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                v.getLayoutParams().height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = duration.toLong()
        v.requestLayout() // invalidate to avoid random ui bugs
        v.startAnimation(a)
    }

    /**
     * Easy way to just collapse any given view and any given speed
     *
     * @param v        View to collapse
     * @param duration Duration of animation
     */
    fun collapseToZero(v: View, duration: Int) {
        val initialHeight = v.getMeasuredHeight()

        val a = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                if (interpolatedTime == 1f) {
                    v.setVisibility(View.GONE)
                } else {
                    v.getLayoutParams().height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = duration.toLong()
        v.startAnimation(a)
    }

}