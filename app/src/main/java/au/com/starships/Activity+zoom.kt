package au.com.starships

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import au.com.starships.databinding.DetailsViewBinding
import au.com.starships.model.Ship
import au.com.starships.service.AppSettings
import au.com.starships.ui.*

enum class HandSide {
    Right,
    Left
}

fun sideOfSlot(slot: View, board: View): HandSide {
    val slotCenter = slot.frame.translate(slot, board).center
    val screenCenter = UIScreen.main.bounds.center
    return if (slotCenter.x < screenCenter.x) HandSide.Left else HandSide.Right
}

fun MainActivity.zoomIn(ship: Ship, slot: View, board: View) {
    preventZoom = true
    // Setup animation for RecycleView
    val side = sideOfSlot(slot, board)
    val cellFrame = slot.frame.translate(slot, board)
    val destinationBox = destinationBoxOf(slot, side)
    val recycleViewAnimations = AnimationSet(false)
    recycleViewAnimations.fillAfter = true
    val fromScale = 1.0
    val toScale = destinationBox.width / cellFrame.width
    this.zoomScale = toScale
    val scaleZoom = ScaleAnimation(
        fromScale.toFloat(),
        toScale.toFloat(),
        fromScale.toFloat(),
        toScale.toFloat()
    )
    scaleZoom.duration = 1000
    recycleViewAnimations.addAnimation(scaleZoom)

    val oldX = cellFrame.x
    val newX = oldX * toScale + board.frame.x
    val oldY = cellFrame.y
    val newY = oldY * toScale + board.frame.y
    rvDeltaX = (destinationBox.x - newX) * UIScreen.main.scale
    rvDeltaY = (destinationBox.y - newY) * UIScreen.main.scale
    val translateAnim = TranslateAnimation(.0f, rvDeltaX.toFloat(), .0f, rvDeltaY.toFloat())
    translateAnim.duration = 1000
    recycleViewAnimations.addAnimation(translateAnim)

    val alphaAni = AlphaAnimation(1.0f, 0.0f)
    alphaAni.duration = 1000
    recycleViewAnimations.addAnimation(alphaAni)

    // Setup overlayImageView
    overlayImageView = ImageView(baseContext)
    val imageFrame = slot.frame.translate(slot, binding.rootView)
    overlayImageView?.setBackgroundColor(Color.TRANSPARENT)
    overlayImageView?.scaleType = ImageView.ScaleType.FIT_CENTER
    overlayImageView?.setImageResource(ship.starshipClass.imageRes)
    this.binding.rootView.addView(overlayImageView)
    overlayImageView?.frame = imageFrame

    val imageViewAnimations = AnimationSet(false)
    imageViewAnimations.fillAfter = true
    imageViewAnimations.addAnimation(scaleZoom)

    val oldImageX = imageFrame.x
    val oldImageY = imageFrame.y

    overlayImageDeltaX = (destinationBox.x - oldImageX) * UIScreen.main.scale
    overlayImageDeltaY = (destinationBox.y - oldImageY) * UIScreen.main.scale
    val translateImageAnim =
        TranslateAnimation(.0f, overlayImageDeltaX.toFloat(), .0f, overlayImageDeltaY.toFloat())
    translateImageAnim.duration = 1000
    imageViewAnimations.addAnimation(translateImageAnim)

    // Do Animation
    board.startAnimation(recycleViewAnimations)
    overlayImageView?.startAnimation(imageViewAnimations)


    binding.menuBtn.animate()
        .alpha(.0f)
        .setDuration(1000)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                binding.menuBtn.isEnabled = false
            }
        })
        .start()
}

fun MainActivity.zoomOut(board: View) {
    val rvZoomOut = AnimationSet(false)
    rvZoomOut.fillAfter = true
    val fromScale = zoomScale
    val toScale = 1
    val scaleZoom = ScaleAnimation(
        fromScale.toFloat(),
        toScale.toFloat(),
        fromScale.toFloat(),
        toScale.toFloat()
    )
    scaleZoom.duration = 1000
    rvZoomOut.addAnimation(scaleZoom)

    val translateAnim = TranslateAnimation(rvDeltaX.toFloat(), .0f, rvDeltaY.toFloat(), .0f)
    translateAnim.duration = 1000
    rvZoomOut.addAnimation(translateAnim)
    rvZoomOut.setAnimationListener(object : AnimationAdapter() {
        override fun onAnimationEnd(animation: Animation?) {
        }
    })

    val alphaAni = AlphaAnimation(.0f, 1.0f)
    alphaAni.duration = 1000
    rvZoomOut.addAnimation(alphaAni)

    // Setup ZoomOut for overlay image
    val oiZoomOut = AnimationSet(false)
    oiZoomOut.fillAfter = false
    val oiScaleZoom = ScaleAnimation(
        fromScale.toFloat(),
        toScale.toFloat(),
        fromScale.toFloat(),
        toScale.toFloat()
    )
    oiScaleZoom.duration = 1000
    oiZoomOut.addAnimation(oiScaleZoom)

    val oiTranslateAnim =
        TranslateAnimation(overlayImageDeltaX.toFloat(), .0f, overlayImageDeltaY.toFloat(), .0f)
    oiTranslateAnim.duration = 1000
    oiZoomOut.addAnimation(oiTranslateAnim)
    oiZoomOut.setAnimationListener(object : AnimationAdapter() {
        override fun onAnimationEnd(animation: Animation?) {
            binding.rootView.removeView(overlayImageView)
        }
    })

    board.startAnimation(rvZoomOut)
    overlayImageView?.startAnimation(oiZoomOut)

    binding.menuBtn.animate()
        .alpha(1.0f)
        .setDuration(1000)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                binding.menuBtn.isEnabled = true
            }
        })
        .start()

    preventZoom = false
}

fun MainActivity.showDetails(ship: Ship, side: HandSide) {
    val detailsBinding = DetailsViewBinding.inflate(layoutInflater)

    detailsBinding.shipName.text = ship.name
    detailsBinding.shipModel.text = ship.model
    detailsBinding.shipMfr.text = ship.manufacturer
    detailsBinding.shipLength.text = ship.length
    detailsBinding.shipPassenger.text = ship.passengers
    detailsBinding.shipSpeed.text = ship.maxAtmospheringSpeed
    val isFavourite = AppSettings.instance.isShipFavourite(ship.name)
    detailsBinding.favouriteCheckbox.setImageResource(if (isFavourite) R.drawable.checked else R.drawable.nochecked)

    val size = UIScreen.main.bounds.size
    val params = RelativeLayout.LayoutParams(
        (size.width / 2 * UIScreen.main.scale).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    when (side) {
        HandSide.Left -> {
            params.leftMargin = (size.width / 2 * UIScreen.main.scale).toInt()
        }
        HandSide.Right -> {
            params.leftMargin = (10 * UIScreen.main.scale).toInt()
        }
    }

    params.addRule(RelativeLayout.CENTER_VERTICAL)
    detailsBinding.root.layoutParams = params
    detailsBinding.root.alpha = .0f
    this.binding.rootView.addView(detailsBinding.root)

    detailsBinding.root.animate().alpha(1.0f).setDuration(1000).start()

    detailsBinding.closeBtn.setOnClickListener {
        zoomOut(this.binding.recyclerView)
        detailsBinding.root.animate()
            .alpha(.0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.rootView.removeView(detailsBinding.root)
                }
            })
            .start()
    }

    detailsBinding.favouriteCheckbox.setOnClickListener {
        val isFavourite = !AppSettings.instance.isShipFavourite(ship.name)
        detailsBinding.favouriteCheckbox.setImageResource(if (isFavourite) R.drawable.checked else R.drawable.nochecked)
        AppSettings.instance.setShipFavourite(ship.name, isFavourite)
        this.reloadData()
    }
}

abstract class AnimationAdapter : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }
}