package au.com.starships.ui

import au.com.starships.MainApplication

data class CGRect(val x: Double, val y: Double, val width: Double, val height: Double)

data class CGSize(val width: Double, val height: Double)
data class CGPoint(val x: Double, val y: Double)

val CGRect.size: CGSize
    get() = CGSize(width, height)

fun CGSize.inflateTo(maxD: Double): CGSize {
    if (width < height) {
        return CGSize(width * maxD / height, maxD)
    } else {
        return CGSize(maxD, maxD * height / width)
    }
}

val CGRect.center: CGPoint
    get() {
        return CGPoint(x + width /2 , y + height /2)
    }

fun CGRect.moveDown(d: Double): CGRect {
    val y = y + d
    return CGRect(x, y, width, height)
}

val CGSize.smallSquare: CGSize
    get() {
        if (width < height) {
            return CGSize(width, width)
        } else {
            return CGSize(height, height)
        }
    }

fun CGSize.rightAlign(rect: CGRect): CGRect {
    val x = rect.width - width
    return CGRect(x, .0, width, height)
}

fun CGSize.leftAlign(rect: CGRect): CGRect {
    return CGRect(rect.x, .0, width, height)
}

fun CGRect.verticalCenterAlign(rect: CGRect): CGRect {
    val y = (rect.height - height) / 2
    return CGRect(x, y, width, height)
}


class UIScreen(val width: Double, val height: Double, val scale: Float) {
    companion object {
        val main: UIScreen
            get() {
                val displayMetrics = MainApplication.instance.resources.displayMetrics
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                val scaleDensity = displayMetrics.scaledDensity
                return UIScreen(width.toDouble() / scaleDensity, height.toDouble() / scaleDensity, scaleDensity)
            }
    }

    val bounds: CGRect
        get() = CGRect(.0, .0, width, height)

    val size: CGSize
        get() = CGSize(main.width, main.height)
}

val CGRect.leftHalf: CGRect
    get() = CGRect(x, y, width / 2, height)

val CGRect.rightHalf: CGRect
    get() = CGRect(x = width / 2, y, width / 2, height)