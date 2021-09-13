package au.com.starships

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import au.com.starships.databinding.ActivityMainBinding
import au.com.starships.databinding.StarShipBinding
import au.com.starships.model.Ship
import au.com.starships.service.AppSettings
import au.com.starships.ui.*
import au.com.starships.view.showMenu


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var zoomScale = .0
    var rvDeltaX = .0
    var rvDeltaY = .0
    var overlayImageDeltaX = .0
    var overlayImageDeltaY = .0
    var preventZoom = false
    lateinit var shipsOrderBy: SHIP_ORDER
    var overlayImageView: ImageView? = null
    var originalData = mutableListOf<Ship>()

    override fun onCreate(savedInstanceState: Bundle?) {
        this.removeStatusBar()

        this.shipsOrderBy = AppSettings.instance.shipOrdering
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2, GridLayoutManager.HORIZONTAL, false)
        }
        val adapter = RecyclerViewAdapter(this, this.originalData, onShipSelected)
        binding.recyclerView.adapter = adapter

        binding.menuBtn.setOnClickListener {
            this.showMenu()
        }
        binding.loadingText.blinking()

        this.fetchData()
    }

    private val onShipSelected = object : OnSelectShipListener {
        override fun shipSelected(ship: Ship, binding: StarShipBinding) {
            if (!preventZoom) {
                val slot = binding.shipImage
                val board = this@MainActivity.binding.recyclerView
                zoomIn(ship, slot, board)
                showDetails(ship, sideOfSlot(slot, board))
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.starsBig.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.starsBig.onStop()
    }
}