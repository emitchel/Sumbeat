package mvvm.kotlin.nerdery.com.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Relatively easy way to manage multiple fragments in a "view pager" without allowing scrolling between them
 *
 * Ideal for bottom bar scenarios, leaving the lifecycle management to the adapter
 */
class NoScrollViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}