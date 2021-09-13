package au.com.starships.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.starships.R
import au.com.starships.databinding.StarShipBinding
import au.com.starships.service.AppSettings
import au.com.starships.model.Ship

interface OnSelectShipListener {
    fun shipSelected(ship: Ship, binding: StarShipBinding)
}

class RecyclerViewAdapter(val context: Context, var ships: List<Ship>, val listener: OnSelectShipListener)
    :RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StarShipBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ship = this.ships[position]

        val isFavourite = AppSettings.instance.isShipFavourite(ship.name)
        holder.binding.favBtn.setImageResource(if (isFavourite) R.drawable.checked else R.drawable.unchecked)

        holder.binding.shipName.text = ship.name
        holder.binding.shipImage.setImageResource(ship.starshipClass.imageRes)
        holder.binding.shipContainer.setOnClickListener {
            listener.shipSelected(ship, holder.binding)
        }
        holder.binding.favBtn.setOnClickListener {
            val isFavourite = !AppSettings.instance.isShipFavourite(ship.name)
            AppSettings.instance.setShipFavourite(ship.name, isFavourite)
            holder.binding.favBtn.setImageResource(if (isFavourite) R.drawable.checked else R.drawable.unchecked)
        }
    }

    override fun getItemCount(): Int {
        return ships.size
    }

    class ViewHolder(val binding: StarShipBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}