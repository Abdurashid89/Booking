package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.ItemImageBinding
import uz.koinot.stadion.databinding.RvImageBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.SingleBlock

class RvImageAdapter(val stadium: Stadium): RecyclerView.Adapter<RvImageAdapter.VHolder>() {

    private var imageListener : ((Stadium, Int)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        RvImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(stadium.photos[position])

    override fun getItemCount() = stadium.photos.size

    inner class VHolder(val view: RvImageBinding): RecyclerView.ViewHolder(view.root){
        @SuppressLint("SetTextI18n")
        fun bind(d: Photos){
            view.imageView.load(CONSTANTS.IMAGE_URL + d.id.toString()){
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)

            }
            itemView.setOnClickListener {
                imageListener?.invoke(stadium,adapterPosition)
            }

        }
    }
    fun setOnClickListener(block: (Stadium,Int) -> Unit){
        imageListener = block
    }
}