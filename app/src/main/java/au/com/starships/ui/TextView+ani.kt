package au.com.starships

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

fun TextView.blinking() {
    val anim = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 1000
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    startAnimation(anim)
}