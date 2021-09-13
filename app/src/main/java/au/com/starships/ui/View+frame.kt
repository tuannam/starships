package au.com.starships.ui

import android.view.View
import android.widget.RelativeLayout
import au.com.starships.HandSide

var View.frame: CGRect
    get() {
        val scale = UIScreen.main.scale
        return CGRect(
            left.toDouble()/ scale,
            top.toDouble() / scale,
            width.toDouble() / scale,
            height.toDouble() / scale
        )
    }
    set(newValue) {
        val scale = UIScreen.main.scale
        val params = RelativeLayout.LayoutParams((newValue.width * scale).toInt(), (newValue.height * scale).toInt())
        params.topMargin = (newValue.y * scale).toInt()
        parent?.let {
            val parentWidth = (it as View).width
            val rightPoint = ((newValue.x + newValue.width) * scale).toInt()
            if ( rightPoint > parentWidth) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.rightMargin = parentWidth - rightPoint
            } else {
                params.leftMargin = (newValue.x * scale).toInt()
            }
        } ?: run {
            params.leftMargin = (newValue.x * scale).toInt()
        }
        this.layoutParams = params
    }

fun CGRect.translate(from: View, to: View): CGRect {
    val parent = from.parent as View
    if (parent == to) {
        return this
    } else {
        val rect = CGRect(this.x + parent.frame.x, this.y + parent.frame.y, this.width, this.height)
        return rect.translate(parent, to)
    }
}



fun destinationBoxOf(view: View, side: HandSide): CGRect {
    val cellFrame = view.frame
    return when (side) {
        HandSide.Left -> {
            val outerBox = UIScreen.main.bounds.leftHalf
            val slotSize = cellFrame.size.inflateTo(outerBox.size.smallSquare.width)
            val box = slotSize.rightAlign(outerBox).verticalCenterAlign(outerBox)
            box
        }
        HandSide.Right -> {
            val outerBox = UIScreen.main.bounds.rightHalf
            val slotSize = cellFrame.size.inflateTo(outerBox.size.smallSquare.width)
            val box = slotSize.leftAlign(outerBox).verticalCenterAlign(outerBox)
            box
        }
    }
}