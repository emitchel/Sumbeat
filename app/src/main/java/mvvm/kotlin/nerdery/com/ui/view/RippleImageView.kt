package mvvm.kotlin.nerdery.com.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView


class RippleImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        setBackgroundResource(getThemeSelectableBackgroundId(getContext()))
    }

    fun getThemeSelectableBackgroundId(context: Context): Int {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        var colorAttr = context.getResources()
            .getIdentifier("selectableItemBackgroundBorderless", "attr", context.getPackageName())

        if (colorAttr == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.selectableItemBackgroundBorderless
            } else {
                colorAttr = android.R.attr.selectableItemBackground
            }
        }

        val outValue = TypedValue()
        context.getTheme().resolveAttribute(colorAttr, outValue, true)
        return outValue.resourceId
    }
}