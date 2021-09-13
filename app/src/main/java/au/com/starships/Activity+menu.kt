package au.com.starships.view

import android.view.ViewGroup
import android.widget.RelativeLayout
import au.com.starships.MainActivity
import au.com.starships.R
import au.com.starships.SHIP_ORDER
import au.com.starships.databinding.MenuViewBinding
import au.com.starships.reOrderAndLoadData
import au.com.starships.service.AppSettings

fun MainActivity.showMenu() {
    var changed = false
    preventZoom = true
    val menuBinding = MenuViewBinding.inflate(layoutInflater)
    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    menuBinding.root.layoutParams = params
    menuBinding.root.isClickable = true
    menuBinding.root.isFocusable = true
    this.updateCheckBoxes(menuBinding)
    this.binding.rootView.addView(menuBinding.root)

    menuBinding.exitBtn.setOnClickListener {
        this.finishAffinity();
    }

    menuBinding.lengthCheckbox.setOnClickListener {
        changed = true
        this.orderingTapped(SHIP_ORDER.ByLength)
        this.updateCheckBoxes(menuBinding)
    }
    menuBinding.passengerCheckbox.setOnClickListener {
        changed = true
        this.orderingTapped(SHIP_ORDER.ByPassenger)
        this.updateCheckBoxes(menuBinding)
    }
    menuBinding.ratingCheckbox.setOnClickListener {
        changed = true
        this.orderingTapped(SHIP_ORDER.ByRating)
        this.updateCheckBoxes(menuBinding)
    }

    menuBinding.closeBtn.setOnClickListener {
        this.binding.rootView.removeView(menuBinding.root)
        preventZoom = false

        if (changed) {
            AppSettings.instance.shipOrdering = this.shipsOrderBy
            reOrderAndLoadData()
        }
    }
}

fun MainActivity.orderingTapped(orderBy: SHIP_ORDER) {
    if (orderBy == this.shipsOrderBy) {
        this.shipsOrderBy = SHIP_ORDER.NoOrdering
    } else {
        this.shipsOrderBy = orderBy
    }
}

fun MainActivity.updateCheckBoxes(binding: MenuViewBinding) {
    when (shipsOrderBy) {
        SHIP_ORDER.NoOrdering -> {
            binding.lengthCheckbox.setImageResource(R.drawable.nochecked)
            binding.passengerCheckbox.setImageResource(R.drawable.nochecked)
            binding.ratingCheckbox.setImageResource(R.drawable.nochecked)
        }
        SHIP_ORDER.ByLength -> {
            binding.lengthCheckbox.setImageResource(R.drawable.checked)
            binding.passengerCheckbox.setImageResource(R.drawable.nochecked)
            binding.ratingCheckbox.setImageResource(R.drawable.nochecked)
        }
        SHIP_ORDER.ByPassenger -> {
            binding.lengthCheckbox.setImageResource(R.drawable.nochecked)
            binding.passengerCheckbox.setImageResource(R.drawable.checked)
            binding.ratingCheckbox.setImageResource(R.drawable.nochecked)
        }
        SHIP_ORDER.ByRating -> {
            binding.lengthCheckbox.setImageResource(R.drawable.nochecked)
            binding.passengerCheckbox.setImageResource(R.drawable.nochecked)
            binding.ratingCheckbox.setImageResource(R.drawable.checked)
        }
    }
}