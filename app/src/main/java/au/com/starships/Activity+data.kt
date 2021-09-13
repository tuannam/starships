package au.com.starships

import android.view.View
import au.com.starships.service.RestApiFacade
import au.com.starships.service.RestApiNetworkErrorResponse
import au.com.starships.service.RestApiOKResponse
import au.com.starships.ui.ErrorDialog
import au.com.starships.ui.RecyclerViewAdapter

enum class SHIP_ORDER {
    NoOrdering,
    ByLength,
    ByPassenger,
    ByRating
}

fun MainActivity.fetchData() {
    RestApiFacade.instance.fetchShips {
        binding.loadingText.clearAnimation()
        binding.loadingText.visibility = View.GONE

        when (it) {
            is RestApiOKResponse<*> -> {
                it.response?.results?.let { ships ->
                    this.originalData.addAll(ships)
                    this.reOrderAndLoadData()
                }
            }
            is RestApiNetworkErrorResponse<*> -> {
                ErrorDialog.createOK(getString(R.string.network_error)) {
                    this.finishAffinity();
                }.display(this)
            }
            else -> {
                ErrorDialog.createOK(getString(R.string.service_error)) {
                    this.finishAffinity();
                }.display(this)
            }
        }
    }
//        RestApiFacade.instance.fetchAllShips {
//            binding.loadingText.clearAnimation()
//            binding.loadingText.visibility = View.GONE
//
//            if (it is RestApiOKResponse<*>) {
//                it.response?.results?.let { ships ->
//                    this.originalData.addAll(ships)
//                    this.reOrderAndLoadData()
//                }
//            }
//        }
}
fun MainActivity.reOrderAndLoadData() {
    val adapter = binding.recyclerView.adapter as RecyclerViewAdapter
    val ships = adapter.ships
    when (this.shipsOrderBy) {
        SHIP_ORDER.ByLength -> {
            adapter.ships = originalData.sortedBy { ship ->
                ship.length.toIntOrNull() ?: run { 0 }
            }
        }
        SHIP_ORDER.ByPassenger -> {
            adapter.ships = originalData.sortedBy { ship ->
                ship.passengers.toIntOrNull() ?: run { 0 }
            }
        }
        SHIP_ORDER.ByRating -> {
            adapter.ships = originalData.sortedBy { ship ->
                ship.hyperdriveRating.toIntOrNull() ?: run { 0 }
            }
        }
        else -> {
            adapter.ships = originalData
        }
    }
    adapter.notifyDataSetChanged()
}

fun MainActivity.reloadData() {
    val adapter = binding.recyclerView.adapter as RecyclerViewAdapter
    adapter.notifyDataSetChanged()
}