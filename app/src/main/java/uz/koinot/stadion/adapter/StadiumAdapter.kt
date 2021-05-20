package uz.koinot.stadion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.ItemStadiumBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.SingleBlock
import uz.koinot.stadion.utils.Utils

class StadiumAdapter: RecyclerView.Adapter<StadiumAdapter.VHolder>() {

    private var listener : SingleBlock<Stadium>? = null
    private var listenerAddImage : SingleBlock<Stadium>? = null
    private var longClickListener : SingleBlock<Stadium>? = null
    private var imageListener : ((Stadium,Int)->Unit)? = null
    private val list = ArrayList<Stadium>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemStadiumBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemStadiumBinding):RecyclerView.ViewHolder(view.root){
        fun bind(d:Stadium){
           view.apply {
               stdName.text = d.name
               verifyCount.text = d.countVerify.toString()
               notVerifyCount.text = d.countNotVerify.toString()
               val adapter = RvImageAdapter(d)
               rvImages.adapter = adapter
               adapter.setOnClickListener { stadium, pos ->
                   imageListener?.invoke(stadium,pos)
               }
               if (list.size >= 10){
                   addImage.visibility = View.GONE
               }
               addImage.setOnClickListener {
                   listenerAddImage?.invoke(d)
               }
           }
            itemView.setOnClickListener {
                listener?.invoke(d)
            }
            itemView.setOnLongClickListener {
                longClickListener?.invoke(d)
                true
            }
        }
    }

    fun submitList(ls: List<Stadium>){
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block:SingleBlock<Stadium>){
        listener = block
    }

    fun setOnLongClickListener(block:SingleBlock<Stadium>){
        longClickListener = block
    }

    fun setOnImageClickListener(block:(Stadium,Int) -> Unit){
        imageListener = block
    }

    fun setOnAddImageClickListener(block:(Stadium) -> Unit){
        listenerAddImage = block
    }
}